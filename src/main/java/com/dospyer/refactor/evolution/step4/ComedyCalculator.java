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
public class ComedyCalculator extends PerformanceCalculator {

    public ComedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }

    public int getAmount() {
        int result = 30000;
        if (this.getPerformance().getAudience() > 20) {
            result += 10000 + 500 * (this.getPerformance().getAudience() - 20);
        }
        result += 300 * this.getPerformance().getAudience();
        return result;
    }
}
