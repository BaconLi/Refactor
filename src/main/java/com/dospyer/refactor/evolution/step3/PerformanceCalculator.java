package com.dospyer.refactor.evolution.step3;

import com.dospyer.refactor.bean.Performance;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.dto.PerformanceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: peigen
 * @Date: 2020/12/18 上午11:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceCalculator {
    private Performance performance;
    private Play play;

    public int getVolumeCredits() {
        int result = 0;
        result += Math.max(this.performance.getAudience() - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(this.play.getType())) {
            result += Math.floor(this.performance.getAudience() / 5);
        }
        return result;
    }

    /**
     * 完成提炼函数后，我会看看提炼出来的函数，看是否能进⼀步提升其表达能⼒。
     * ⼀般我做的第⼀件事就是给⼀些变量改名，使它们更简洁，⽐如将 thisAmount重命名为 result。
     * <p>
     * 编码⻛格：永远将函数的返回值命名为“result”，这样我⼀眼就能知道它的作⽤。
     */
    public int getAmount() {
        int result;
        switch (this.play.getType()) {
            case "tragedy":
                result = 40000;
                if (this.performance.getAudience() > 30) {
                    result += 1000 * (this.performance.getAudience() - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (this.performance.getAudience() > 20) {
                    result += 10000 + 500 * (this.performance.getAudience() - 20);
                }
                result += 300 * this.performance.getAudience();
                break;
            default:
                throw new RuntimeException("unknown type: " + this.play.getType());
        }
        return result;
    }
}
