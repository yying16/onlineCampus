package com.campus.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.util.R;
import com.campus.user.dao.CardDao;
import com.campus.user.dao.UserDao;
import com.campus.user.domain.Card;
import com.campus.user.domain.CardSM;
import com.campus.user.domain.DetailsChange;
import com.campus.user.domain.User;
import com.campus.user.dto.AddCardForm;
import com.campus.user.dto.QueryCardForm;
import com.campus.user.service.CardSMService;
import com.campus.user.service.CardService;
import com.campus.user.service.DetailsChangeService;
import com.campus.user.service.UserService;
import com.campus.user.util.CardNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:46
 */

@Service
public class CardServiceImpl extends ServiceImpl<CardDao, Card>
        implements CardService {

    @Autowired
    CardSMService cardSMService;


    @Autowired
    UserService userService;

    @Autowired
    DetailsChangeService detailsChangeService;


    @Autowired
    UserDao userDao;

    @Override
    public boolean addCard(AddCardForm addCardForm) {
        //生成卡密数量
        Integer number = addCardForm.getNumber();
        //生成卡密类型id
        String cardSMId = addCardForm.getCardSMId();

        //生成卡密
        for (int i = 0; i < number; i++) {
            Card card = new Card();
            card.setCardsmid(cardSMId);
            //生成卡号
            String cardKey = CardNumberGenerator.generateCardKey();
            card.setCardKey(cardKey);
            //生成卡密
            int insert = baseMapper.insert(card);
            if (insert <= 0) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    @Override
    public void listCard(Page<Card> cardPage, QueryCardForm card) {
        QueryWrapper<Card> wrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(card.getCardKey())) {
            wrapper.eq("card_key", card.getCardKey());
        }
        if (StringUtils.hasLength(card.getCardsmid())) {
            wrapper.eq("cardsmid", card.getCardsmid());
        }

        if (card.getStatus() != null) {
            wrapper.eq("status", card.getStatus());
        }

        if (StringUtils.hasLength(card.getUid())) {
            wrapper.eq("uid", card.getUid());
        }
        baseMapper.selectPage(cardPage, wrapper);
    }

    @Override
    public R useCard(String cardKey, String uid) {
        QueryWrapper<Card> wrapper = new QueryWrapper<>();
        wrapper.eq("card_key", cardKey);
        Card card = baseMapper.selectOne(wrapper);
        if (card == null) {
            return R.failed(null, "卡密不存在");
        }
        if (card.getStatus()) {
            return R.failed(null, "卡密已使用");
        }
        //判断有效期
        String cardsmid = card.getCardsmid();
        CardSM cardSM = cardSMService.getById(cardsmid);
        if (cardSM == null) {
            return R.failed(null, "卡密类型不存在");
        }
        if (cardSM.getValidity() != null) {
            Integer validity = cardSM.getValidity();
            if (validity > 0) {
                //判断有效期
                //获取卡密创建时间
                String createTime = card.getCreateTime();
                //转为时间戳
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                Date date = null;
                long validityTime;
                try {
                    date = dateFormat.parse(createTime);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, validity);
                    Date newDate = calendar.getTime();
                    validityTime = newDate.getTime();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                //获取当前时间戳
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis > validityTime) {
                    return R.failed(null, "卡密已过期");
                }
            }
        }


        //判断卡密类型
        BigDecimal money = cardSM.getMoney();
        if (money != null) {
            //充值
            //调用充值接口
            //获取用户余额
            User user = userDao.selectById(uid);
            BigDecimal balance = user.getBalance();
            //计算余额
            balance = balance.add(money);
            //修改用户余额
            userService.updateBalance(uid, balance);

            //产生零钱明细记录

            DetailsChange detailsChange = new DetailsChange();
            detailsChange.setUid(uid);
            detailsChange.setMoney(money);
            detailsChange.setType(0);
            detailsChange.setRemark("卡密充值");
            detailsChange.setBalance(balance);
            detailsChange.setAvatar("https://gitee.com/lin-xugeng/image2/raw/master/img/202308181630253.jpeg");
            boolean save = detailsChangeService.save(detailsChange);



        }
        String virtualGoods = cardSM.getVirtualGoods();
        if (StringUtils.hasLength(virtualGoods)) {
            //发放虚拟商品
            //TODO
        }
        //修改卡密状态
        card.setStatus(true);
        card.setUid(uid);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        String dateString = dateFormat.format(date);
        card.setTime(dateString);
        baseMapper.updateById(card);
        return R.ok(null, "使用成功");
    }
}
