package com.dospyer.refactor.dto;

import lombok.*;

import java.util.List;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午11:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class StatementData {
    @NonNull
    private String customer;
    @NonNull
    private List<PerformanceDto> Performances;
    private int totalAmount;
    private int totalVolumeCredits;
}
