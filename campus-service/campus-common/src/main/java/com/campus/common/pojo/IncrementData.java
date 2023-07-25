package com.campus.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncrementData<T> {
    String id;
    Class<T> clazz;
    String[] args;
}
