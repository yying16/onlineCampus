package com.campus.voucher.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.service.ServiceCenter;
import com.campus.voucher.dao.VoucherDao;
import com.campus.voucher.domain.SeckillVoucher;
import com.campus.voucher.domain.Voucher;
import com.campus.voucher.service.SeckillVoucherService;
import com.campus.voucher.service.VoucherService;
import com.campus.voucher.utils.RedisConstants;
import com.campus.voucher.vo.VoucherVo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static com.campus.voucher.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * @Author: chb
 * @Date: 2023/09/06/19:58
 * @Description:
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherDao, Voucher> implements VoucherService {

    @Resource
    private SeckillVoucherService seckillVoucherService;

    @Autowired
    private StringRedisTemplate redisTemplate;

//    @Resource
//    private ServiceCenter serviceCenter;

    @Override
    @Transactional
    public boolean createSeckillVoucher(VoucherVo voucherVo) {
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherVo,voucher);
        String id = IdWorker.getIdStr();
        voucher.setVoucherId(id);
        String time = DateTime.now().toString();
        voucher.setCreateTime(time);
        voucher.setUpdateTime(time);
        String voucherString = JSONObject.toJSONString(voucher);
        redisTemplate.opsForValue().set(RedisConstants.VOUCHER_KEY + id, voucherString);
        boolean voucherSave = this.save(voucher);
        if (!voucherSave){
            return voucherSave;
        }
        //封装秒杀优惠券信息
        SeckillVoucher seckillVoucher = getSeckillVoucher(voucher);
        boolean seckillVoucherSave = seckillVoucherService.save(seckillVoucher);
        if (!seckillVoucherSave){
            return seckillVoucherSave;
        }
        redisTemplate.opsForValue().set(SECKILL_STOCK_KEY + id , seckillVoucher.getStock().toString());
        return true;
    }

    @Override
    public Voucher getVoucherDetail(String voucherId) {
        Voucher voucher = this.getById(voucherId);
        return voucher;
    }

    @Override
    public List<Voucher> getVoucherList() {
        LambdaQueryWrapper<Voucher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Voucher::getStatus, 1);
        queryWrapper.eq(Voucher::getDeleted, 0);
        List<Voucher> list = this.list(queryWrapper);
        return list;
    }


    @NotNull
    private SeckillVoucher getSeckillVoucher(Voucher voucher) {
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getVoucherId());
        seckillVoucher.setCreateTime(voucher.getCreateTime());
        seckillVoucher.setUpdateTime(voucher.getUpdateTime());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucher.setStock(voucher.getStock());
        return seckillVoucher;
    }
}
