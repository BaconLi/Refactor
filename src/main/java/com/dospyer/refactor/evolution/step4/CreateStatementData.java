package com.dospyer.refactor.evolution.step4;

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
        List<PerformanceDto> performanceDtos = getPerformanceDtos(invoice, playMap);

        StatementData statementData = new StatementData(customer, performanceDtos);
        statementData.setTotalAmount(getTotalAmount(performanceDtos));
        statementData.setTotalVolumeCredits(getTotalVolumeCredits(performanceDtos));
        return statementData;
    }

    private static List<PerformanceDto> getPerformanceDtos(JSONObject invoice, Map<String, Play> playMap) {
        List<Performance> performances = JSON.parseArray(invoice.getString("performances"), Performance.class);
        List<PerformanceDto> performanceDtos = new ArrayList<>(performances.size());
        for (Performance perf : performances) {
            PerformanceCalculator performanceCalculator = createPerformanceCalculator(perf,getPlay(playMap, perf));

            PerformanceDto dto = new PerformanceDto();
            dto.setAudience(perf.getAudience());
            dto.setPlayID(perf.getPlayID());
            dto.setPlay(performanceCalculator.getPlay());
            dto.setAmount(performanceCalculator.getAmount());
            dto.setVolumeCredits(performanceCalculator.getVolumeCredits());
            performanceDtos.add(dto);
        }
        return performanceDtos;
    }

    private static PerformanceCalculator createPerformanceCalculator(Performance perf, Play play) {
        switch (play.getType()) {
            case "tragedy":
                return new TragedyCalculator(perf, play);
            case "comedy":
                return new ComedyCalculator(perf, play);
            default:
                throw new RuntimeException("unknown type: " + play.getType());
        }
    }

    private static Play getPlay(Map<String, Play> playMap, Performance perf) {
        return playMap.get(perf.getPlayID());
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
