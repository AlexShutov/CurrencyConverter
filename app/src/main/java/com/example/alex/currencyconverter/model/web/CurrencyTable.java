package com.example.alex.currencyconverter.model.web;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Alex on 4/8/2017.
 */
@Root(name = "ValCurs")
public class CurrencyTable {

    private String name;
    private String date;
    private List<Currency> currencyRecords;

    @Attribute(name = "name")
    public String getName() {
        return name;
    }

    @Attribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    @Attribute(name = "Date")
    public String getDate() {
        return date;
    }

    @Attribute(name = "Date")
    public void setDate(String date) {
        this.date = date;
    }

    @ElementList(inline = true)
    public List<Currency> getCurrencyRecords() {
        return currencyRecords;
    }

    @ElementList(inline = true)
    public void setCurrencyRecords(List<Currency> currencyRecords) {
        this.currencyRecords = currencyRecords;
    }
}
