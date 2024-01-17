package cn.mulanbay.pms.web.controller.system;

import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.pms.handler.SystemConfigHandler;
import cn.mulanbay.pms.util.FileUtil;
import cn.mulanbay.pms.web.bean.req.system.backup.BackupFileDeleteForm;
import cn.mulanbay.pms.web.bean.res.system.backup.FileVo;
import cn.mulanbay.pms.web.controller.BaseController;
import cn.mulanbay.web.bean.request.PageSearch;
import cn.mulanbay.web.bean.response.ResultBean;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

/**
 * 备份管理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/backup")
public class BackupController extends BaseController {

    @Value("${mulanbay.backup.filePath}")
    String backupFilePath;

    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBean list(PageSearch sf) {
        List<FileVo> list = new ArrayList<>();
        String backupPath = backupFilePath;
        File file = new File(backupPath);
        long totalSize = 0L;
        if (file.exists()) {
            for (File f : file.listFiles()) {
                FileVo fb = new FileVo();
                fb.setFileName(f.getName());
                fb.setPath(f.getPath());
                fb.setDirectory(f.isDirectory());
                long size = f.length();
                fb.setSize(size);
                totalSize += size;
                fb.setLastModifyTime(new Date(f.lastModified()));
                list.add(fb);
            }
            Collections.sort(list, new Comparator<FileVo>() {
                @Override
                public int compare(FileVo o1, FileVo o2) {
                    return o2.getLastModifyTime().compareTo(o1.getLastModifyTime());
                }
            });
        }
        FileVo total = new FileVo();
        total.setFileName("合计");
        total.setPath(backupPath);
        total.setDirectory(true);
        total.setSize(totalSize);
        total.setLastModifyTime(new Date(0L));
        list.add(total);
        PageResult<FileVo> res = new PageResult<FileVo>();
        res.setBeanList(list);
        res.setMaxRow(list.size());
        return callbackDataGrid(res);
    }

    /**
     * 下载
     *
     * @param fileName
     * @param response
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(@RequestParam(name = "fileName")String fileName, HttpServletResponse response) {
        try {
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = backupFilePath + "/" + fileName;

            //response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtil.setFileDownloadHeader(request, realFileName));
            FileUtil.writeBytes(filePath, response.getOutputStream());
        } catch (Exception e) {
            logger.error("下载文件失败", e);
        }
    }

    /**
     * 删除
     *
     * @param dr
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid BackupFileDeleteForm dr) {
        String fullPath = backupFilePath + "/" + dr.getFileName();
        FileUtil.deleteFile(fullPath);
        return callback(null);
    }

}
