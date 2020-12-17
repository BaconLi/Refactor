package com.dospyer.refactor.evolution.step2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Performances;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.dto.StatementData;
import com.dospyer.refactor.contants.Contants;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午10:02
 */
public class Statement8 {
    /**
     * 重构第七步：{@link Statement7}
     * 重构第八步
     * <p>
     * 接着创建⼀个对象，作为在两个阶段间传递的中转数据结构，然后将它作为第⼀个参数传递给 renderPlainText （然后编译、测试、提交）。
     * getPlainText(invoice, playMap); --> getPlainText(data,invoice, playMap);
     *
     * <p>
     * 现在我要检查⼀下 renderPlainText ⽤到的其他参数。
     * 我希望将它们挪到这个中转数据结构⾥，这样所有计算代码都可以被挪到 statement 函数中,
     * 让 renderPlainText 只操作通过 data 参数传进来的数据。
     */
    public static String statement(JSONObject invoice, Map<String, Play> playMap) {
        String customer = invoice.getString("customer");
        List<Performances> performances = JSON.parseArray(invoice.getString("performances"), Performances.class);
        StatementData statementData = new StatementData(customer, performances);
        return getPlainText(statementData, playMap);
    }

    private static String getPlainText(StatementData statementData, Map<String, Play> playMap) {
        StringBuilder result = new StringBuilder("Statement for ").append(statementData.getCustomer()).append(Contants.LINE_SEPARATOR);

        for (Performances perf : statementData.getPerformances()) {
            // print line for this order
            result.append(getPlay(playMap, perf).getName()).append(": ").append(usd(amountFor(playMap, perf))).append(" (").append(perf.getAudience()).append(" seats)").append(Contants.LINE_SEPARATOR);
        }

        result.append("Amount owed is ").append(usd(getTotalAmount(playMap, statementData.getPerformances()))).append(Contants.LINE_SEPARATOR);
        result.append("You earned ").append(getTotalVolumeCredits(playMap, statementData.getPerformances())).append(" credits");
        return result.toString();
    }

    /**
     * 提炼函数后记得要修改函数内部的变量名，以便保持⼀贯的编码⻛格。
     */
    private static int getTotalAmount(Map<String, Play> playMap, List<Performances> performances) {
        int totalAmount = 0;
        for (Performances perf : performances) {
            // print line for this order
            totalAmount += amountFor(playMap, perf);
        }
        return totalAmount;
    }

    private static int getTotalVolumeCredits(Map<String, Play> playMap, List<Performances> performances) {
        int volumeCredits = 0;
        for (Performances perf : performances) {
            // add volume credits
            volumeCredits += getVolumeCredits(playMap, perf);
        }
        return volumeCredits;
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
     * <p>
     * 因此对于重构过程的性能问题，我总体的建议是：⼤多数情况下可以忽略它。如果重构引⼊了性能损耗，先完成重构，再做性能优化。
     * <p>
     * 我们移除 volumeCredits 的过程是多么⼩步。
     * 整个过程⼀共有4步，每⼀步都伴随着⼀次编译、测试以及向本地代码库的提交：
     * 1.使⽤拆分循环（227）分离出累加过程；
     * 2.使⽤移动语句（223）将累加变ᰁ的声明与累加过程集中到⼀起；
     * 3.使⽤提炼函数（106）提炼出计算总数的函数；
     * 4.使⽤内联变ᰁ（123）完全移除中间变量。
     */
    private static String usd(int d) {
        DecimalFormat format = new DecimalFormat("#.00");
        return format.format(d / 100);
    }

    private static int getVolumeCredits(Map<String, Play> playMap, Performances perf) {
        int result = 0;
        result += Math.max(perf.getAudience() - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(getPlay(playMap, perf).getType())) {
            result += Math.floor(perf.getAudience() / 5);
        }
        return result;
    }

    private static Play getPlay(Map<String, Play> playMap, Performances perf) {
        return playMap.get(perf.getPlayID());
    }

    /**
     * 完成提炼函数后，我会看看提炼出来的函数，看是否能进⼀步提升其表达能⼒。
     * ⼀般我做的第⼀件事就是给⼀些变量改名，使它们更简洁，⽐如将 thisAmount重命名为 result。
     * <p>
     * 编码⻛格：永远将函数的返回值命名为“result”，这样我⼀眼就能知道它的作⽤。
     */
    private static int amountFor(Map<String, Play> playMap, Performances perf) {
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
