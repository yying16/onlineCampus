package com.campus.gateway.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录用户的访问行为
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_behaviour")
public class Behaviour {
    String behaviourId; // 行为id
    String method; // 请求方法
    String uri; // uri
    String header; // 请求头(jsonStr)
    String path; //请求路径
    String params; //请求参数
    /**请求体只能访问一次，所以没办法拿*/
    String token; // token
    String uid; // 用户id
    String service; // 服务模块名称
    Integer status; // 状态码
    String reasonPhrase; // 响应结果
    Long duration; // 响应时间-请求时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String requestTime; // 请求时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String responseTime; // 响应时间
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String createTime; // 创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String updateTime; // 更新时间
}
