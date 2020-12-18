package com.dospyer.refactor.dto;

import com.dospyer.refactor.bean.Performance;
import com.dospyer.refactor.bean.Play;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午4:47
 */
@Data
@NoArgsConstructor
public class PerformanceDto extends Performance {
    private Play play;
    private int amount;
    private int volumeCredits;
}
