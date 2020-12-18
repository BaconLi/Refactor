package com.dospyer.refactor.evolution.step1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Performance;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.contants.Contants;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午9:44
 */
public class Statement3 {
    /**
     * 重构第二步：{@link Statement2}
     * 重构第三步：
     * play 变量是由 performances 变量计算得到的，因此根本没必要将它作为参数传⼊
     * <p>
     * 我分解⼀个⻓函数时，将 play 这样的变量移除掉。
     * 创建了很多具有局部作⽤域的临时变量，这会使提炼函数更加复杂。
     * 这⾥我要使⽤的重构⼿法是以查询取代临时变量（178）。
     * <p>
     * 思考：重构前，查找 play 变ᰁ的代码在每次循环中只执⾏了1次，⽽重构后却执⾏了3次。
     * 我会在后⾯探讨重构与性能之间的关系，但现在，我认为这次改动还不太可能对性能有严重影响，
     * 即便真的有所影响，后续再对⼀段结构良好的代码进⾏性能调优，也容易得多。
     * <p>
     * 移除局部变量的好处就是做提炼时会简单得多，因为需要操⼼的局部作⽤域变少了。
     * 实际上，在做任何提炼前，我⼀般都会先移除局部变量。
     */
    public static String statement(JSONObject invoice, Map<String, Play> playMap) {
        String customer = invoice.getString("customer");
        StringBuilder result = new StringBuilder("Statement for ").append(customer).append(Contants.LINE_SEPARATOR);

        DecimalFormat format = new DecimalFormat("#.00");
        List<Performance> performances = JSON.parseArray(invoice.getString("performances"), Performance.class);

        int totalAmount = 0;
        int volumeCredits = 0;

        for (Performance perf : performances) {
            // add volume credits
            volumeCredits += Math.max(perf.getAudience() - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(getPlay(playMap, perf).getType())) {
                volumeCredits += Math.floor(perf.getAudience() / 5);
            }
            // print line for this order
            result.append(getPlay(playMap, perf).getName()).append(": ").append(format.format(amountFor(playMap, perf) / 100)).append(" (").append(perf.getAudience()).append(" seats)").append(Contants.LINE_SEPARATOR);
            totalAmount += amountFor(playMap, perf);
        }

        result.append("Amount owed is ").append(format.format(totalAmount / 100)).append(Contants.LINE_SEPARATOR);
        result.append("You earned ").append(volumeCredits).append(" credits");
        return result.toString();
    }

    private static Play getPlay(Map<String, Play> playMap, Performance perf) {
        return playMap.get(perf.getPlayID());
    }

    /**
     * 完成提炼函数后，我会看看提炼出来的函数，看是否能进⼀步提升其表达能⼒。
     * ⼀般我做的第⼀件事就是给⼀些变量改名，使它们更简洁，⽐如将 thisAmount重命名为 result。
     * <p>
     * 编码⻛格：永远将函数的返回值命名为“result”，这样我⼀眼就能知道它的作⽤。
     */
    private static int amountFor(Map<String, Play> playMap, Performance perf) {
        int result;
        switch (getPlay(playMap, perf).getType()) {
            case "tragedy":
                result = 40000;
                if (perf.getAudience() > 30) {
                    result += 1000 * (perf.getAudience() - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (perf.getAudience() > 20) {
                    result += 10000 + 500 * (perf.getAudience() - 20);
                }
                result += 300 * perf.getAudience();
                break;
            default:
                throw new RuntimeException("unknown type: " + getPlay(playMap, perf).getType() + "");
        }
        return result;
    }
}
