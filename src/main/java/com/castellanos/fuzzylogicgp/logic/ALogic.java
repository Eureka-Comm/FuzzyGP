/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.castellanos.fuzzylogicgp.logic;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author hp
 */
public interface ALogic {
    public BigDecimal not(BigDecimal v1);
    public BigDecimal imp(BigDecimal v1, BigDecimal v2);
    public BigDecimal eqv(BigDecimal v1, BigDecimal v2);
    public BigDecimal and(BigDecimal v1, BigDecimal v2);
    public BigDecimal or(BigDecimal v1, BigDecimal v2);
    public BigDecimal forAll(List<BigDecimal> values);
    public BigDecimal exist(List<BigDecimal> values);
}
