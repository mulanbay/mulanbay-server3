package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.business.handler.CacheHandler;
import cn.mulanbay.common.queue.LimitQueue;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.pms.common.CacheKey;
import cn.mulanbay.pms.server.Cpu;
import cn.mulanbay.pms.server.Mem;
import cn.mulanbay.pms.server.ServerDetail;
import cn.mulanbay.pms.server.SysFile;
import cn.mulanbay.pms.server.SystemResourceType;
import cn.mulanbay.pms.web.bean.res.chart.ChartData;
import cn.mulanbay.pms.web.bean.res.chart.ChartYData;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统（监控数据）
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/server")
public class ServerController extends BaseController {

    @Autowired
    CacheHandler cacheHandler;

    /**
     * 获取系统详情
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBean detail() {
        ServerDetail serverDetail = new ServerDetail();
        serverDetail.copyTo();
        return callback(serverDetail);
    }

    /**
     * 系统监控统计
     *
     * @return
     */
    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    public ResultBean stat(@RequestParam(name = "resourceType") SystemResourceType resourceType) {
        String key = CacheKey.getKey(CacheKey.SERVER_DETAIL_MONITOR_TIMELINE);
        LimitQueue<ServerDetail> queue = cacheHandler.get(key, LimitQueue.class);
        if (queue == null) {
            queue = new LimitQueue<>(0);
        } if (resourceType == SystemResourceType.DISK) {
            return callback(createDiskChartData(queue));
        } else if (resourceType == SystemResourceType.MEMORY) {
            return callback(createMemoryChartData(queue));
        } else {
            return callback(createCpuChartData(queue));
        }
    }

    /**
     * 磁盘统计
     * @param queue
     * @return
     */
    private ChartData createDiskChartData(LimitQueue<ServerDetail> queue) {
        ChartData chartData = new ChartData();
        chartData.setTitle("磁盘监控");
        chartData.setLegendData(new String[]{"磁盘空闲空间", "已使用比例"});
        //混合图形下使用
        chartData.addYAxis("磁盘空闲空间","G");
        chartData.addYAxis("已使用比例","%");
        ChartYData yData1 = new ChartYData("磁盘空闲空间","G");
        ChartYData yData2 = new ChartYData("已使用比例","%");
        int n = queue.size();
        for (int i = 0; i < n; i++) {
            ServerDetail sd = queue.get(i);
            SysFile di = sd.getSysFiles().get(0);
            chartData.getXdata().add(DateUtil.getFormatDate(sd.getDate(), "MM-dd HH:mm"));
            yData1.getData().add(NumberUtil.getValue(di.getFreeSpace() * 1.0 / 1024 / 1024 / 1024,0));
            yData2.getData().add(NumberUtil.getValue((di.getTotalSpace() - di.getFreeSpace()) * 1.0 / di.getTotalSpace() * 100,0));
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return chartData;
    }

    /**
     * 内存统计
     * @param queue
     * @return
     */
    private ChartData createMemoryChartData(LimitQueue<ServerDetail> queue) {
        ChartData chartData = new ChartData();
        chartData.setTitle("内存监控");
        chartData.setLegendData(new String[]{"空闲空间", "已使用比例"});
        //混合图形下使用
        chartData.addYAxis("空闲空间","G");
        chartData.addYAxis("已使用比例","%");
        ChartYData yData1 = new ChartYData("空闲空间","G");
        ChartYData yData2 = new ChartYData("已使用比例","%");
        int n = queue.size();
        for (int i = 0; i < n; i++) {
            ServerDetail sd = queue.get(i);
            chartData.getXdata().add(DateUtil.getFormatDate(sd.getDate(), "MM-dd HH:mm"));
            Mem mem = sd.getMem();
            yData1.getData().add(NumberUtil.getValue(mem.getFreePhysicalMemorySize() * 1.0 / 1024 / 1024 / 1024,0));
            yData2.getData().add(NumberUtil.getValue((mem.getTotalMemorySize() - mem.getFreePhysicalMemorySize()) * 1.0 / mem.getTotalMemorySize() * 100,0));
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return chartData;
    }

    /**
     * CPU统计
     * @param queue
     * @return
     */
    private ChartData createCpuChartData(LimitQueue<ServerDetail> queue) {
        ChartData chartData = new ChartData();
        chartData.setTitle("CPU监控");
        chartData.setLegendData(new String[]{"使用率", "空闲率"});
        //混合图形下使用
        chartData.addYAxis("使用率","%");
        chartData.addYAxis("空闲率","%");
        ChartYData yData1 = new ChartYData("使用率","%");
        ChartYData yData2 = new ChartYData("空闲率","%");
        int n = queue.size();
        for (int i = 0; i < n; i++) {
            ServerDetail sd = queue.get(i);
            chartData.getXdata().add(DateUtil.getFormatDate(sd.getDate(), "MM-dd HH:mm"));
            Cpu cpu = sd.getCpu();
            yData1.getData().add(NumberUtil.getValue(cpu.getSysCpuRate() * 100,0));
            yData2.getData().add(NumberUtil.getValue(cpu.getIdleCpuRate() * 100,0));
        }
        chartData.getYdata().add(yData1);
        chartData.getYdata().add(yData2);
        return chartData;
    }
}
