package com.dospyer.refactor.dto;

import com.dospyer.refactor.bean.Performances;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午11:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementData {
    private String customer;
    private List<Performances> Performances;
}
