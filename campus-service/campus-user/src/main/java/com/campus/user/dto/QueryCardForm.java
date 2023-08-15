package com.campus.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/8/11 13:23
 */

@Data
public class QueryCardForm {


    String cardKey;

    Boolean status;

    String cardsmid;

    String uid;// 使用者的用户id
}
