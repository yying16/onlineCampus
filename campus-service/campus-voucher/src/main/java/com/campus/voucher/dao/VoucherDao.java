package com.campus.voucher.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.voucher.domain.Voucher;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: chb
 * @Date: 2023/09/06/19:55
 * @Description:
 */
@Mapper
public interface VoucherDao extends BaseMapper<Voucher> {
}
