package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Miluo
 * @description 文件上传
 **/

@RestController
@Api(tags = "文件上传管理")
@RequestMapping("/admin/product")
public class FileUploadController {
    @Value("${file.url}")
    private String fileUrl;

    @ApiOperation("文件上传")
    @PostMapping("/fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws IOException, MyException {
        String imgUrl = "";
        if (file != null){
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            byte[] fileBytes = file.getBytes();

            String path = ClassUtils.getDefaultClassLoader().getResource("tracker.conf").getPath();
            ClientGlobal.init(path);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerClientConnection = trackerClient.getConnection();
            StorageClient1 storageClient1 = new StorageClient1(trackerClientConnection, null);

            String uploadFile1 = storageClient1.upload_file1(fileBytes, extension, null);
            imgUrl = fileUrl+ uploadFile1;
            System.out.println(imgUrl);
        }

        return Result.ok(imgUrl);
    }
}
