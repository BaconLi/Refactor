package com.dospyer.refactor.dto;

import com.dospyer.refactor.bean.Play;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午4:47
 */
@Data
@NoArgsConstructor
public class PerformancesDto {
    private Play playFor;
    private String playID;
    private int audience;
}
