package com.campus.trade.task;

import com.campus.common.bloomFilter.BloomFilterService;
import com.campus.trade.domain.Product;
import com.campus.trade.service.ProductService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: chb
 * @Date: 2023/08/30/20:30
 * @Description:
 */
@Component
public class ProductBloomTask {

    @Autowired
    private ProductService productService;

    @Autowired
    private BloomFilterService bloomFilterService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void task(){
        try {
            List<Product> productList = productService.list();
            List<String> strings = productList.stream().map(Product::getProductId).collect(Collectors.toList());
            for (String s : strings) {
                String key = "product" + s;
                bloomFilterService.bloomSet(key);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(ProductBloomTask.class).error("ProductBloomTask定时器出错");
        }

    }
}
