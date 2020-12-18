package com.dospyer.refactor.evolution.step4;

import com.dospyer.refactor.bean.Performance;
import com.dospyer.refactor.bean.Play;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: peigen
 * @Date: 2020/12/18 上午11:06
 */
@Data
@NoArgsConstructor
public class TragedyCalculator extends PerformanceCalculator {

    public TragedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }

    public int getAmount() {
        int result = 40000;
        if (this.getPerformance().getAudience() > 30) {
            result += 1000 * (this.getPerformance().getAudience() - 30);
        }
        return result;
    }
}
