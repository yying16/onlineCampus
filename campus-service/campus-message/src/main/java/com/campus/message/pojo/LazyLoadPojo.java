package com.campus.message.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LazyLoadPojo {
    String uid;
    String fid;
    Integer num;
}
