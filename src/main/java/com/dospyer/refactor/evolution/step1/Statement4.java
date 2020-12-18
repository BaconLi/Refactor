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
 * @Date: 2020/12/17 下午10:02
 */
public class Statement4 {
    /**
     * 重构第三步：{@link Statement3}
     * 重构第四步：
     * volumeCredits 变量则有些棘⼿。它是⼀个累加变量，循环的每次迭代都会更新它的值。
     * 因此最简单的⽅式是，将整块逻辑提炼到新函数中，然后在新函数中直接返回 volumeCredits 。
     * <p>
     * 临时变量往往会带来麻烦。它们只在对其进⾏处理的代码块中有⽤，因此临时变量实质上是⿎励你写⻓⽽复杂的函数。
     * 因此，下⼀步我要替换掉⼀些临时变量，⽽最简单的莫过于从format 变量⼊⼿。
     * 这是典型的“将函数赋值给临时变量”的场景，我更愿意将其替换为⼀个明确声明的函数。
     */
    public static String statement(JSONObject invoice, Map<String, Play> playMap) {
        String customer = invoice.getString("customer");
        List<Performance> performances = JSON.parseArray(invoice.getString("performances"), Performance.class);

        StringBuilder result = new StringBuilder("Statement for ").append(customer).append(Contants.LINE_SEPARATOR);

        int totalAmount = 0;
        int volumeCredits = 0;

        for (Performance perf : performances) {
            // add volume credits
            volumeCredits += getVolumeCredits(playMap, perf);
            // print line for this order
            result.append(getPlay(playMap, perf).getName()).append(": ").append(usd(amountFor(playMap, perf))).append(" (").append(perf.getAudience()).append(" seats)").append(Contants.LINE_SEPARATOR);
            totalAmount += amountFor(playMap, perf);
        }

        result.append("Amount owed is ").append(usd(totalAmount)).append(Contants.LINE_SEPARATOR);
        result.append("You earned ").append(volumeCredits).append(" credits");
        return result.toString();
    }

    /**
     * format 未能清晰地描述其作⽤。
     * formatAsUSD 很表意，但⼜太⻓，特别它仅是⼩范围地被⽤在⼀个字符串模板中。
     * 我认为这⾥真正需要强调的是，它格式化的是⼀个货币数字，因此我选取了⼀个能体现此意图的命名，并应⽤了改变函数声明(124)
     * <p>
     * 好的命名⼗分重要，但往往并⾮唾⼿可得。只有恰如其分地命名，才能彰显出将⼤函数分解成⼩函数的价值。
     * 有了好的名称，我就不必通过阅读函数体来了解其⾏为。但要⼀次把名取好并不容易，因此我会使⽤当下能想到最好的那个。
     * 如果稍后想到更好的，我就会毫不犹豫地换掉它。通常你需要花⼏秒钟通读更多代码，才能发现最好的名称是什么。
     * <p>
     * 重命名的同时，我还将重复的除以100的⾏为也搬移到函数⾥。
     * 将钱以美分为单位作为正整数存储是⼀种常⻅的做法，可以避免使⽤浮点数来存储货币的⼩数部分，同时⼜不影响⽤数学运算符操作它。
     * 不过，对于这样⼀个以美分为单位的整数，我⼜需要以美元为单位进⾏展示，因此让格式化函数来处理整除的事宜再好不过。
     */
    private static String usd(int d) {
        DecimalFormat format = new DecimalFormat("#.00");
        return format.format(d / 100);
    }

    private static int getVolumeCredits(Map<String, Play> playMap, Performance perf) {
        int result = 0;
        result += Math.max(perf.getAudience() - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(getPlay(playMap, perf).getType())) {
            result += Math.floor(perf.getAudience() / 5);
        }
        return result;
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
