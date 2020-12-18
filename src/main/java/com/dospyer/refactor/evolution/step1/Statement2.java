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
 * @Date: 2020/12/17 下午5:39
 */
public class Statement2 {
    /**
     * 重构第一步创建junit测试类：StatementTest
     * 重构第二步：分解statement函数
     * 从整个函数中分离出不同的关注点
     * 分离switch语句，先将这块代码抽取成⼀个独立的函数，按它所⼲的事情给它命名，比如叫 amountFor(performance)
     * <p>
     * 1.修改完后运行测试，无论每次重构多么简单，养成重构后立即测试的习惯非常重要。
     * 犯错误是很容易的，做完⼀次修改就运⾏测试，这样在我真的犯了错时，只需要考虑⼀个很⼩的改动范围，这使得查错与修复问题易如反掌。
     * 这就是重构过程的精髓所在：⼩步修改，每次修改后就运⾏测试。
     * <p>
     * 2.测试通过后把代码提交到本地的版本控制系统。
     * 如果果待会不⼩⼼搞砸了，我便能轻松回滚到上⼀个可⼯作的状态。
     * 把代码推送（push）到远端仓库前，会把零碎的修改压缩成⼀个更有意义的提交（commit）。
     */
    public static String statement(JSONObject invoice, Map<String, Play> playMap) {
        String customer = invoice.getString("customer");
        StringBuilder result = new StringBuilder("Statement for ").append(customer).append(Contants.LINE_SEPARATOR);

        DecimalFormat format = new DecimalFormat("#.00");
        List<Performance> performances = JSON.parseArray(invoice.getString("performances"), Performance.class);

        int totalAmount = 0;
        int volumeCredits = 0;

        for (Performance perf : performances) {
            Play play = playMap.get(perf.getPlayID());
            int thisAmount = amountFor(perf, play);
            // add volume credits
            volumeCredits += Math.max(perf.getAudience() - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(play.getType())) {
                volumeCredits += Math.floor(perf.getAudience() / 5);
            }
            // print line for this order
            result.append(play.getName()).append(": ").append(format.format(thisAmount / 100)).append(" (").append(perf.getAudience()).append(" seats)").append(Contants.LINE_SEPARATOR);
            totalAmount += thisAmount;
        }

        result.append("Amount owed is ").append(format.format(totalAmount / 100)).append(Contants.LINE_SEPARATOR);
        result.append("You earned ").append(volumeCredits).append(" credits");
        return result.toString();
    }

    /**
     * 完成提炼函数后，我会看看提炼出来的函数，看是否能进⼀步提升其表达能⼒。
     * ⼀般我做的第⼀件事就是给⼀些变量改名，使它们更简洁，⽐如将 thisAmount重命名为 result。
     * <p>
     * 编码⻛格：永远将函数的返回值命名为“result”，这样我⼀眼就能知道它的作⽤。
     */
    private static int amountFor(Performance perf, Play play) {
        int result;
        switch (play.getType()) {
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
                throw new RuntimeException("unknown type: " + play.getType() + "");
        }
        return result;
    }
}
