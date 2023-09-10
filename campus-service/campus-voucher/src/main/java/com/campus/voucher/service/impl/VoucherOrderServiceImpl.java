package com.campus.voucher.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.util.R;
import com.campus.common.util.SpringContextUtil;
import com.campus.voucher.asyn.ErrorCreateVO;
import com.campus.voucher.dao.VoucherOrderDao;
import com.campus.voucher.domain.SeckillVoucher;
import com.campus.voucher.domain.Voucher;
import com.campus.voucher.domain.VoucherOrder;
import com.campus.voucher.service.SeckillVoucherService;
import com.campus.voucher.service.VoucherOrderService;
import com.campus.voucher.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: chb
 * @Date: 2023/09/07/17:30
 * @Description:
 */
@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderDao, VoucherOrder> implements VoucherOrderService {

    @Resource
    private SeckillVoucherService seckillVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    KafkaTemplate<String, VoucherOrder> kafkaTemplate;

//    @Resource
//    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

//    private static final ExecutorService VOUCHER_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    public R seckill(String voucherId, String userId) {
        String orderId = IdWorker.getIdStr();
        try {
            Long result = stringRedisTemplate.execute(
                    SECKILL_SCRIPT,
                    Collections.emptyList(),
                    voucherId, userId, orderId);

            int r = result.intValue();
            System.out.println(r);
            if (r != 0) {
                return R.failed(null, r == 1 ? "库存不足" : "用户只能抢一次");
            }
            //封装voucherOrder
            VoucherOrder voucherOrder = buildVoucherOrder(voucherId, orderId, userId);
            //放入kafka消息队列

            ListenableFuture<SendResult<String, VoucherOrder>> future = kafkaTemplate.send("VOUCHER", voucherOrder);
            future.addCallback(res -> log.info("消息成功同步到topic:{} partition:{}", res.getRecordMetadata().topic(), res.getRecordMetadata().partition()),
                    ex -> log.error("消息同步失败，原因：{}", ex.getMessage()));


            return R.ok(orderId, "抢券成功");
        } catch (Exception e) {
            LoggerFactory.getLogger(VoucherOrderServiceImpl.class).error("优惠券秒杀业务出错：" + e.toString());
            e.printStackTrace();
            return R.failed(null, "系统出错");
        }
    }

    private VoucherOrder buildVoucherOrder(String voucherId, String orderId, String userId) {
        String voucherOrderJson = stringRedisTemplate.opsForValue().get(RedisConstants.VOUCHER_KEY + voucherId);
        Voucher voucher = JSONObject.parseObject(voucherOrderJson, Voucher.class);
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setVoucherOrderId(orderId);
        voucherOrder.setExpireTime(voucher.getExpireTime());
        return voucherOrder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(VoucherOrder voucherOrder) {
        RedissonClient redissonClient = ((RedissonClient) SpringContextUtil.getBean("redissonClient"));
        RLock lock = redissonClient.getLock("seckill:user:" + voucherOrder.getUserId());
        try {
            boolean tryLock = lock.tryLock();
            if (!tryLock) {
                log.error("用户只能抢一次");
                return;
            }

            String userId = voucherOrder.getUserId();
            String voucherId = voucherOrder.getVoucherId();

            int count = this.query().eq("user_id", userId).eq("voucher_id", voucherId).count().intValue();
            if (count > 0) {
                log.error("用户只能抢一次");
                return;
            }

            boolean success = seckillVoucherService.update()
                    .setSql("stock = stock - 1")
                    .eq("voucher_id", voucherId)
                    .gt("stock", 0).update();

            if (!success) {
                log.error("库存不足!");
                return;
            }
            //创建订单
            save(voucherOrder);
        }
        //可以处理出错信息处理
//        catch (Exception e) {
//            LoggerFactory.getLogger(VoucherOrderServiceImpl.class).error(e.toString());
//            ErrorCreateVO errorCreateVO = (ErrorCreateVO) SpringContextUtil.getBean("errorCreateVO");
//            errorCreateVO.createFail(voucherOrder, e.toString());
//            throw new RuntimeException();
//        }
        finally {
            lock.unlock();
        }
    }
}
