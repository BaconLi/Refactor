package com.dospyer.refactor.evolution.step1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Performances;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.contants.Contants;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午10:02
 */
public class Statement5 {
    /**
     * 重构第四步：{@link Statement4}
     * 重构第五步
     * ⽬标是 volumeCredits 。处理这个变量更加微妙，因为它是在循环的迭代过程中累加得到的。
     * 第⼀步，就是应⽤拆分循环（227）将 volumeCredits 的累加过程分离出来。
     * 第二步，⽤移动语句（223）⼿法将变量volumeCredits声明挪动到紧邻循环的位置
     * 第三步，以查询取代临时变量（178）⼿法的施展。第⼀步同样是先对变量的计算过程应⽤提炼函数（106）⼿法。
     * 第四部，再应⽤内联变量（123）⼿法内联 getTotalVolumeCredits 函数
     * <p>
     * <p>
     * 思考：
     * 我知道有些读者会再次对此修改可能带来的性能问题感到担忧。
     * 我知道很多⼈本能地警惕重复的循环。但⼤多数时候，重复⼀次这样的循环对性能的影响都可忽略不计。
     * 如果你在重构前后进⾏计时，很可能甚⾄都注意不到运⾏速度的变化——通常也确实没什么变化。
     * <p>
     * 许多程序员对代码实际的运⾏路径都所知不⾜，甚⾄经验丰富的程序员有时也未能避免。
     * 在聪明的编译器、现代的缓存技术⾯前，我们很多直觉都是不准确的。软件的性能通常只与代码的⼀⼩部分相关，改变其他的部分往往对总体性能贡献甚微。
     * <p>
     * 当然，“⼤多数时候”不等同于“所有时候”。有时，⼀些重构⼿法也会显著地影响性能。
     * 但即便如此，我通常也不去管它，继续重构，因为有了⼀份结构良好的代码，回头调优其性能也容易得多。
     * 如果我在重构时引⼊了明显的性能损耗，我后⾯会花时间进⾏性能调优。
     * 进⾏调优时，可能会回退我早先做的⼀些重构——但更多时候，因为重构我可以使⽤更⾼效的调优⽅案。最后我得到的是既整洁⼜⾼效的代码。
     */
    public static String statement(JSONObject invoice, Map<String, Play> playMap) {
        String customer = invoice.getString("customer");
        List<Performances> performances = JSON.parseArray(invoice.getString("performances"), Performances.class);

        StringBuilder result = new StringBuilder("Statement for ").append(customer).append(Contants.LINE_SEPARATOR);

        int totalAmount = 0;
        for (Performances perf : performances) {
            // print line for this order
            result.append(getPlay(playMap, perf).getName()).append(": ").append(usd(amountFor(playMap, perf))).append(" (").append(perf.getAudience()).append(" seats)").append(Contants.LINE_SEPARATOR);
            totalAmount += amountFor(playMap, perf);
        }
        //对应第四部volumeCredits只在45行中使用，应⽤内联变量取消局部变量
//        int volumeCredits = getTotalVolumeCredits(playMap, performances);

        result.append("Amount owed is ").append(usd(totalAmount)).append(Contants.LINE_SEPARATOR);
        result.append("You earned ").append(getTotalVolumeCredits(playMap, performances)).append(" credits");
        return result.toString();
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
