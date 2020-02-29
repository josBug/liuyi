package com.example.demo.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.StorageClass;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
public class BasicController {

    @RequestMapping(value = "/goods/upload",method = RequestMethod.POST)
    public String uploadGoodsFile(@RequestParam("file") MultipartFile file) throws IOException {
// Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "LTAI4FcX2npMWL38FD3N322E";
        String accessKeySecret = "cw0i6FPJKqLlDZuvvwxhn8XNzNpKYm";

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);

// 上传Byte数组。
        byte[] content = file.getBytes();
        // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        metadata.setObjectAcl(CannedAccessControlList.PublicRead);
        ossClient.putObject("lywss", file.getOriginalFilename(), new ByteArrayInputStream(content), metadata);
// 关闭OSSClient。
        ossClient.shutdown();
        return getUrl(file.getOriginalFilename());
    }

    private String getUrl(String fileName) {
        return String.format("https://lywss.oss-cn-beijing.aliyuncs.com/%s?x-oss-process=image/resize,w_100", fileName);
    }
}
