package com.dospyer.refactor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.evolution.step1.Statement1;
import com.dospyer.refactor.evolution.step2.Statement14;
import com.dospyer.refactor.util.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午3:47
 */
public class App {
    /**
     * 设想有⼀个戏剧演出团，演员们经常要去各种场合表演戏剧。
     * 通常客户（customer）会指定⼏出剧⽬，⽽剧团则根据观众（audience）⼈数及剧⽬类型来向客户收费。
     * <p>
     * 该团⽬前出演两种戏剧：悲剧（tragedy）和喜剧（comedy）。
     * 给客户发出账单时，剧团还会根据到场观众的数量给出“观众量积分”（volume credit）优惠，
     * 下次客户再请剧团表演时可以使⽤积分获得折扣，你可以把它看作⼀种提升客户忠诚度的⽅式。
     * <p>
     * 该剧团将剧⽬的数据存储在plays.json⽂件
     * 客户账单存储在invoices.json文件中
     * <p>
     * <p>
     * 程序执行需要打印如下信息：
     * Statement for BigCo
     * Hamlet: $650.00 (55 seats)
     * As You Like It: $580.00 (35 seats)
     * Othello: $500.00 (40 seats)
     * Amount owed is $1,730.00
     * You earned 47 credits
     */

    public static void main(String[] args) throws IOException {
        Map<String, Play> playMap = new HashMap<>();
        String plays = Resource.getFileContent("json/plays.json");
        JSONObject playsJson = JSONObject.parseObject(plays);
        Set<String> playsKeySet = playsJson.keySet();
        for (String key : playsKeySet) {
            Play play = playsJson.getObject(key, Play.class);
            playMap.put(key, play);
        }

        String invoices = Resource.getFileContent("json/invoices.json");
        JSONArray invoiceArray = JSONObject.parseArray(invoices);
        for (int i = 0; i < invoiceArray.size(); i++) {
            JSONObject invoice = invoiceArray.getJSONObject(i);
            String statement = Statement14.statementHtml(invoice, playMap);
            System.out.println(statement);
        }
    }
}
