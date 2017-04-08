package com.example.alex.currencyconverter.model;

import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.model.web.CurrencyTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * transforms web to application model
 */
public class WebToAppModelConverter {

    public List<Currency> webToAppModel(CurrencyTable currencyTable){
        List<Currency> currencies = new ArrayList<>();
        for (com.example.alex.currencyconverter.model.web.Currency c :
                currencyTable.getCurrencyRecords()){
            Currency converted = new Currency();
            converted.setCharCode(c.getCharCode());
            converted.setName(c.getName());
            String strRate = c.getExcangeValueInRoubles();
            // replace Russian floating point format format: '.' instead of ','
            strRate = strRate.replace(',', '.');
            double roublesRate = Double.parseDouble(strRate);
            converted.setExchangeValueInRoubles(roublesRate);
            currencies.add(converted);
        }
        return currencies;
    }
}
