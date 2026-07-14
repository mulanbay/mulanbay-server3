package cn.mulanbay.pms.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.common.util.FileUtil;
import cn.mulanbay.common.util.MimeTypeUtils;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.pms.persistent.domain.Resources;
import cn.mulanbay.pms.persistent.enums.BussSource;
import cn.mulanbay.pms.persistent.enums.ResourcesType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.mulanbay.pms.common.PmsCode.STORE_FILE_ERROR;

/**
 *
 * @author fenghong
 * @date 2024/3/4
 */
@Component
public class ResourcesHandler extends BaseHandler {

    /**
     * 图片地址
     */
    @Value("${mulanbay.picture.folder}")
    String pictureFolder;

    @Autowired
    BaseService baseService;

    public ResourcesHandler() {
        super("资源处理器");
    }

    /**
     * 存储文件
     * @param file
     * @return
     */
    public String storePicture(MultipartFile file, Long referId, BussSource bussSource){
        String extractFilename = null;
        try {
            // 获取原文件名
            extractFilename = this.extractFilename(file);
            // 创建文件实例
            File filePath = new File(pictureFolder, extractFilename);
            FileUtil.checkPathExits(filePath);
            // 写入文件
            file.transferTo(filePath);
            if(referId!=null){
                createResources(extractFilename,ResourcesType.PICTURE,referId,bussSource);
            }
        } catch (IOException e) {
            throw new ApplicationException(STORE_FILE_ERROR);
        }
        return extractFilename;
    }

    /**
     * 存储文件
     * @param files
     * @return
     */
    public List<String> storePictures(MultipartFile[] files, Long referId,BussSource bussSource){
        List<String> res = new ArrayList<>();
        for (MultipartFile file : files) {
            String extractFilename = this.storePicture(file,referId,bussSource);
            res.add(extractFilename);
        }
        return res;
    }

    private void createResources(String path, ResourcesType type,Long referId,BussSource bussSource){
        Resources resources = new Resources();
        resources.setType(type);
        resources.setPath(path);
        resources.setReferId(referId);
        resources.setBussSource(bussSource);
        baseService.saveObject(resources);
    }

    /**
     * 删除资源
     * @param resId
     */
    public void deleteResource(Long resId){
        Resources bean = baseService.getObject(Resources.class,resId);
        File file = new File(pictureFolder, bean.getPath());
        boolean b = FileUtil.deleteFile(file);
        if(b){
            baseService.deleteObject(bean);
        }
    }
    /**
     * 编码文件名
     */
    private String extractFilename(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        fileName = DateUtil.getFormatDate(new Date(), "yyyyMMdd") + "/" + StringUtil.genUUID() + "." + extension;
        return "/" + fileName;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    private String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtil.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

}
