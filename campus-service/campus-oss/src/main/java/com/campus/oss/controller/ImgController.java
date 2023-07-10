package com.campus.oss.controller;

import com.campus.common.util.R;
import com.campus.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @auther xiaolin
 * @creatr 2023/5/13 22:24
 */

@Api(tags="阿里云图片上传")
@RestController
@RequestMapping("eduoss/fileoss")
//@CrossOrigin
public class ImgController {

    @Autowired
    private OssService ossService;

    @ApiOperation(value = "文件上传")
    @PostMapping
    public R uploadOssFile(MultipartFile file){

        //获取上传的文件 MultipartFile
        //返回上传oss的路径
        String url = ossService.uploadFileAvatar(file);
        return R.ok(url,"上传成功");
    }

}
