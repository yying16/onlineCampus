package com.campus.contact.dto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCommentForm {
    @ApiParam(required = true)
    String dynamicId; // 动态id
    @ApiParam(required = true)
    String commentId; // 评论id
}