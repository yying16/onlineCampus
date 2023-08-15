package com.campus.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.user.dao.CardDao;
import com.campus.user.dao.CardSMDao;
import com.campus.user.domain.Card;
import com.campus.user.domain.CardSM;
import com.campus.user.dto.AddCardSMForm;
import com.campus.user.service.CardSMService;
import com.campus.user.service.CardService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:49
 */

@Service
public class CardSMServiceImpl extends ServiceImpl<CardSMDao, CardSM>
        implements CardSMService {
    @Override
    public boolean addCardSM(AddCardSMForm addCardSMForm) {
        CardSM cardSM = new CardSM();

        BeanUtils.copyProperties(addCardSMForm, cardSM);
        int insert = baseMapper.insert(cardSM);
        return insert > 0;
    }
}
