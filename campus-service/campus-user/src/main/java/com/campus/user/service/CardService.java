package com.campus.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.util.R;
import com.campus.user.domain.Card;
import com.campus.user.dto.AddCardForm;
import com.campus.user.dto.QueryCardForm;

import java.util.List;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:45
 */
public interface CardService extends IService<Card> {
    boolean addCard(AddCardForm addCardForm);

    void listCard(Page<Card> cardPage, QueryCardForm card);

    R useCard(String cardKey, String uid);
}
