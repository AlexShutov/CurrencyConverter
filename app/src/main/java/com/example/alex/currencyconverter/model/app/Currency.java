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

    private String name;
    private String charCode;
    private double exchangeValueInRoubles;

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
}
