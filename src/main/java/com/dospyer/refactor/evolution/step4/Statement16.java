package com.dospyer.refactor.evolution.step4;

import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.contants.Contants;
import com.dospyer.refactor.dto.PerformanceDto;
import com.dospyer.refactor.dto.StatementData;
import com.dospyer.refactor.evolution.step3.Statement15;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午10:45
 */
public class Statement16 {
    /**
     * 重构第十五步：{@link Statement15}
     * 重构第十六步
     * <p>
     * 我已将全部计算逻辑搬移到⼀个类中，是时候将它多态化了。
     * 第⼀步是应⽤以⼦类取代类型码（362）引⼊⼦类，弃⽤类型代码。
     * 为此，我需要为演出计算器创建⼦类，并在 createStatementData 中获取对应的⼦类。
     * 要得到正确的⼦类，创建⼯⼚函数取代构造函数（334）。
     */
    public static String statement(JSONObject invoice, Map<String, Play> playMap) {
        StatementData statementData = CreateStatementData.getStatementData(invoice, playMap);
        return getPlainText(statementData);
    }

    private static String getPlainText(StatementData statementData) {
        StringBuilder result = new StringBuilder("Statement for ").append(statementData.getCustomer()).append(Contants.LINE_SEPARATOR);

        for (PerformanceDto perf : statementData.getPerformances()) {
            // print line for this order
            result.append(perf.getPlay().getName()).append(": ").append(usd(perf.getAmount())).append(" (").append(perf.getAudience()).append(" seats)").append(Contants.LINE_SEPARATOR);
        }

        result.append("Amount owed is ").append(usd(statementData.getTotalAmount())).append(Contants.LINE_SEPARATOR);
        result.append("You earned ").append(statementData.getTotalVolumeCredits()).append(" credits");
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
     * <p>
     * 因此对于重构过程的性能问题，我总体的建议是：⼤多数情况下可以忽略它。如果重构引⼊了性能损耗，先完成重构，再做性能优化。
     * <p>
     * 我们移除 volumeCredits 的过程是多么⼩步。
     * 整个过程⼀共有4步，每⼀步都伴随着⼀次编译、测试以及向本地代码库的提交：
     * 1.使⽤拆分循环（227）分离出累加过程；
     * 2.使⽤移动语句（223）将累加变ᰁ的声明与累加过程集中到⼀起；
     * 3.使⽤提炼函数（106）提炼出计算总数的函数；
     * 4.使⽤内联变量（123）完全移除中间变量。
     */
    private static String usd(int d) {
        DecimalFormat format = new DecimalFormat("#.00");
        return format.format(d / 100);
    }

}
