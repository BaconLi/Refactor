package com.dospyer.refactor.evolution.step3;

import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.contants.Contants;
import com.dospyer.refactor.dto.PerformanceDto;
import com.dospyer.refactor.dto.StatementData;
import com.dospyer.refactor.evolution.step2.Statement14;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午10:45
 */
public class Statement15 {
    /**
     * 重构第十四步：{@link Statement14}
     * 重构第十五步
     * <p>
     * 1.8 按类型重组计算过程
     * 接下来我将注意⼒集中到下⼀个特性改动：⽀持更多类型的戏剧，以及⽀持它们各⾃的价格计算和观众量积分计算。
     * <p>
     * 对于现在的结构，我只需要在计算函数⾥添加分⽀逻辑即可。
     * amountFor 函数清楚地体现了，戏剧类型在计算分⽀的选择上起着关键的作⽤——但这样的分⽀逻辑很容易随代码堆积⽽腐坏。
     * <p>
     * 要为程序引⼊结构、显式地表达出“计算逻辑的差异是由类型代码确定”有许多途径，不过最⾃然的解决
     * 办法还是使⽤⾯向对象世界⾥的⼀个经典特性——类型多态。
     * <p>
     * 设想是先建⽴⼀个继承体系，它有“喜剧”（ comedy ）和“悲剧”（ tragedy ）两个⼦类，⼦类各⾃包含独立的计算逻辑。
     * 调⽤者通过调⽤⼀个多态的 amount 函数，让语⾔帮你分发到不同的⼦类的计算过程中。 volumeCredits 函数的处理也是如法炮制。
     * <p>
     * 为此我需要⽤到多种重构⽅法，其中最核⼼的⼀招是以多态取代条件表达式（272），将多个同样的类型码分⽀⽤多态取代。
     * 但在施展以多态取代条件表达式（272）之前，我得先创建⼀个基本的继承结构。
     * 我需要先创建⼀个类，并将价格计算函数和观众量积分计算函数放进去。
     *
     *
     * <p>
     * CreateStatementData的getStatementData 函数是关键所在，因为正是它⽤每场演出的数据来填充中转数据结构。
     * ⽬前它直接调⽤了计算价格和观众量积分的函数，我需要创建⼀个类，通过这个类来调⽤这些函数。
     * 由于这个类存放了与每场演出相关数据的计算函数，于是我把它称为演出计算器（performance calculator）。
     * <p>
     * 为了方便回顾重构过程，复制CreateStatementData类到step3包中，并对step3包下的CreateStatementData类进行修改。
     * <p>
     * 创建PerformanceCalculator类
     * 将Performance和Play传递进去。
     * 在PerformanceCalculator内部完成getAmount(原：amountFor)和getVolumeCredits计算逻辑
     *
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
