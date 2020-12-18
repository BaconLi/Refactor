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
@AllArgsConstructor
public abstract class PerformanceCalculator {
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
    public abstract int getAmount() ;
}
