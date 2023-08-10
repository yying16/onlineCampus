package com.campus.parttime.pojo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author kakakaka
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FavoritesList {
    String favoritesId; // 收藏记录编号
    String userId; // 用户编号
    String jobId; // 兼职编号
    String jobTitle; // 兼职标题
    Boolean deleted;    // 逻辑删除
}
