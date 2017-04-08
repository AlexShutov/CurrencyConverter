package com.example.alex.currencyconverter.model.web;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * Web model for currency exchange rate. This class represents response from server.
 */
@Root(name = "Valute")
public class Currency {

    private int numberCode;
    private int nominal;
    private String charCode;
    private String name;
    private String excangeValueInRoubles;

    @Element(name = "NumCode")
    public int getNumberCode() {
        return numberCode;
    }

    @Element(name = "NumCode")
    public void setNumberCode(int numberCode) {
        this.numberCode = numberCode;
    }

    @Element(name = "Nominal")
    public int getNominal() {
        return nominal;
    }

    @Element(name = "Nominal")
    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    @Element(name = "CharCode")
    public String getCharCode() {
        return charCode;
    }

    @Element(name = "CharCode")
    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    @Element(name = "Name")
    public String getName() {
        return name;
    }

    @Element(name = "Name")
    public void setName(String name) {
        this.name = name;
    }

    @Element(name = "Value")
    public String getExcangeValueInRoubles() {
        return excangeValueInRoubles;
    }

    @Element(name = "Value")
    public void setExcangeValueInRoubles(String excangeValueInRoubles) {
        this.excangeValueInRoubles = excangeValueInRoubles;
    }
}
