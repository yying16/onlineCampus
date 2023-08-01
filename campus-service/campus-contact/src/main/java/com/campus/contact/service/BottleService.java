package com.campus.contact.service;

import com.campus.contact.domain.Bottle;

public interface BottleService {

    /**
     * 捞个漂流瓶
     * */
    Bottle grabBottle(Integer category);
}
