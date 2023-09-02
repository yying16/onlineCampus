package com.campus.recruit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitUpdateForm {
    String recruitId; // 招募id
    String content; // 内容
    String photo; // 图片链接
    String requirement; // 招募要求(json)(key:小标题,value:格式)
    Integer recruitNum; // 招募人数
    String recruitDdl; //招募截至时间
    String recruitTel; //招募联系方式
}
