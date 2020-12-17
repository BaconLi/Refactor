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
 * @Date: 2020/12/17 下午5:39
 */
public class Statement1 {
    /**
     * 实现功能逻辑
     */
    public static String statement(JSONObject invoice, Map<String, Play> playMap) {
        String customer = invoice.getString("customer");
        StringBuilder result = new StringBuilder("Statement for ").append(customer).append(Contants.LINE_SEPARATOR);

        DecimalFormat format = new DecimalFormat("#.00");

        List<Performances> performances = JSON.parseArray(invoice.getString("performances"), Performances.class);

        int totalAmount = 0;
        int volumeCredits = 0;

        for (Performances perf : performances) {
            Play play = playMap.get(perf.getPlayID());
            int thisAmount;
            switch (play.getType()) {
                case "tragedy":
                    thisAmount = 40000;
                    if (perf.getAudience() > 30) {
                        thisAmount += 1000 * (perf.getAudience() - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000;
                    if (perf.getAudience() > 20) {
                        thisAmount += 10000 + 500 * (perf.getAudience() - 20);
                    }
                    thisAmount += 300 * perf.getAudience();
                    break;
                default:
                    throw new RuntimeException("unknown type: " + play.getType() + "");
            }
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
}
