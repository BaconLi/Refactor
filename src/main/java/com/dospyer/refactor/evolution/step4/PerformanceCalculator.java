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

    /**
     * 下⼀个要替换的条件表达式是观众量积分的计算。
     * 我回顾了⼀下前⾯关于未来戏剧类型的讨论，发现⼤多数剧类在计算积分时都会检查观众数是否达到30，仅⼀⼩部分品类有所不同。
     * 因此，将更为通⽤的逻辑放到超类作为默认条件，出现特殊场景时按需覆盖它，听起来⼗分合理。
     * 于是我将⼀部分喜剧的逻辑下移到⼦类。
     */
    public int getVolumeCredits() {
//        int result = Math.max(this.performance.getAudience() - 30, 0);
//        // add extra credit for every ten comedy attendees
//        if ("comedy".equals(this.play.getType())) {
//            result += Math.floor(this.performance.getAudience() / 5);
//        }
//        return result;

        // 修改为：
        return Math.max(this.performance.getAudience() - 30, 0);
    }

    /**
     * 完成提炼函数后，我会看看提炼出来的函数，看是否能进⼀步提升其表达能⼒。
     * ⼀般我做的第⼀件事就是给⼀些变量改名，使它们更简洁，⽐如将 thisAmount重命名为 result。
     * <p>
     * 编码⻛格：永远将函数的返回值命名为“result”，这样我⼀眼就能知道它的作⽤。
     */
    public abstract int getAmount() ;
}
