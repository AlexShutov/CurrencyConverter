package com.example.alex.currencyconverter.model.app;

/**
 * Created by Alex on 4/8/2017.
 */

import java.math.BigDecimal;

/**
 * Application model for currency excange rate.
 * We don't need extra information such coming from server, such as
 * numberCode, nominal, sync date
 */
public class Currency {

    /**
     * We use integer Id, coming from a server assuming all ids are unique.
     * On the other hand, charCode is unique for every currency too, but numberId make more sense.
     */
    private String currencyId;
    private String name;
    private String charCode;
    private double exchangeValueInRoubles;

    public Currency(){}

    public Currency(Currency other){
        currencyId = other.currencyId;
        name = other.name;
        charCode = other.charCode;
        exchangeValueInRoubles = other.exchangeValueInRoubles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public double getExchangeValueInRoubles() {
        return exchangeValueInRoubles;
    }

    public void setExchangeValueInRoubles(double exchangeValueInRoubles) {
        this.exchangeValueInRoubles = exchangeValueInRoubles;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
}
