package com.dospyer.refactor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dospyer.refactor.bean.Play;
import com.dospyer.refactor.evolution.step1.*;
import com.dospyer.refactor.evolution.step2.*;
import com.dospyer.refactor.util.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午5:53
 * 重构第一步：确保即将修改的代码拥有⼀组可靠的测试
 */
public class StatementTest {


    private String message = "Statement for BigCo\n" +
            "Hamlet: 650.00 (55 seats)\n" +
            "As You Like It: 580.00 (35 seats)\n" +
            "Othello: 500.00 (40 seats)\n" +
            "Amount owed is 1730.00\n" +
            "You earned 47 credits";

    private String playPath = "json/plays.json";
    private String invoicesPath = "json/invoices.json";

    Map<String, Play> playMap = null;
    JSONObject invoice = null;

    @Before
    public void getPlayMap() throws IOException {
        playMap = new HashMap<>();
        String plays = Resource.getFileContent(playPath);
        JSONObject playsJson = JSONObject.parseObject(plays);
        Set<String> playsKeySet = playsJson.keySet();
        for (String key : playsKeySet) {
            Play play = playsJson.getObject(key, Play.class);
            playMap.put(key, play);
        }
    }

    @Before
    public void getInvoice() throws IOException {
        String invoices = Resource.getFileContent(invoicesPath);
        JSONArray invoiceArray = JSONObject.parseArray(invoices);
        invoice = invoiceArray.getJSONObject(0);
    }

    @Test
    public void test() {
        Assert.assertEquals(message, Statement1.statement(invoice, playMap));
        Assert.assertEquals(message, Statement2.statement(invoice, playMap));
        Assert.assertEquals(message, Statement3.statement(invoice, playMap));
        Assert.assertEquals(message, Statement4.statement(invoice, playMap));
        Assert.assertEquals(message, Statement5.statement(invoice, playMap));
        Assert.assertEquals(message, Statement6.statement(invoice, playMap));
        Assert.assertEquals(message, Statement7.statement(invoice, playMap));
        Assert.assertEquals(message, Statement8.statement(invoice, playMap));
        Assert.assertEquals(message, Statement9.statement(invoice, playMap));
        Assert.assertEquals(message, Statement10.statement(invoice, playMap));
        Assert.assertEquals(message, Statement11.statement(invoice, playMap));
        Assert.assertEquals(message, Statement12.statement(invoice, playMap));
        Assert.assertEquals(message, Statement13.statement(invoice, playMap));
        Assert.assertEquals(message, Statement14.statement(invoice, playMap));
    }
}
