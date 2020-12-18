package com.dospyer.refactor.evolution.step2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Performance;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.dto.PerformanceDto;
import com.dospyer.refactor.dto.StatementData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: peigen
 * @Date: 2020/12/18 上午10:13
 */
public class CreateStatementData {

    public static StatementData getStatementData(JSONObject invoice, Map<String, Play> playMap) {
        String customer = invoice.getString("customer");
        List<Performance> performances = JSON.parseArray(invoice.getString("performances"), Performance.class);
        List<PerformanceDto> performanceDtos = new ArrayList<>(performances.size());
        for (Performance perf : performances) {
            PerformanceDto dto = new PerformanceDto();
            dto.setAudience(perf.getAudience());
            dto.setPlayID(perf.getPlayID());
            dto.setPlay(getPlay(playMap, perf));
            dto.setAmount(amountFor(dto));
            dto.setVolumeCredits(getVolumeCredits(dto));
            performanceDtos.add(dto);
        }

        StatementData statementData = new StatementData(customer, performanceDtos);
        statementData.setTotalAmount(getTotalAmount(performanceDtos));
        statementData.setTotalVolumeCredits(getTotalVolumeCredits(performanceDtos));
        return statementData;
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
    private static int amountFor(PerformanceDto perf) {
        int result;
        switch (perf.getPlay().getType()) {
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
                throw new RuntimeException("unknown type: " + perf.getPlay().getType());
        }
        return result;
    }

    private static int getVolumeCredits(PerformanceDto perf) {
        int result = 0;
        result += Math.max(perf.getAudience() - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(perf.getPlay().getType())) {
            result += Math.floor(perf.getAudience() / 5);
        }
        return result;
    }

    /**
     * 提炼函数后记得要修改函数内部的变量名，以便保持⼀贯的编码⻛格。
     */
    private static int getTotalAmount(List<PerformanceDto> performances) {
        int result = performances.stream().mapToInt(PerformanceDto::getAmount).sum();
        return result;
    }

    private static int getTotalVolumeCredits(List<PerformanceDto> performances) {
        int result = performances.stream().mapToInt(PerformanceDto::getVolumeCredits).sum();
        return result;
    }
}
