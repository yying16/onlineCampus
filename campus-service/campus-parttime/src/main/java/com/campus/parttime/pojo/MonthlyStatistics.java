package com.campus.parttime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * author kakakaka
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyStatistics implements Serializable {
    String date;
    Double rate;
}
