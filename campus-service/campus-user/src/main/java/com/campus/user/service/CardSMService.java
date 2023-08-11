package com.campus.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.user.domain.CardSM;
import com.campus.user.dto.AddCardSMForm;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:45
 */
public interface CardSMService extends IService<CardSM> {
    boolean addCardSM(AddCardSMForm addCardSMForm);
}
