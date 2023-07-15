package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.dao.ImageDao;
import com.campus.trade.domain.Image;
import com.campus.trade.service.ImageService;
import org.springframework.stereotype.Service;

/**
* @author xiaolin
* @description 针对表【t_image】的数据库操作Service实现
* @createDate 2023-07-13 11:12:35
*/
@Service
public class ImageServiceImpl extends ServiceImpl<ImageDao, Image>
    implements ImageService{

}




