package com.example.alex.currencyconverter.model;

import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.model.web.CurrencyTable;

import java.util.List;

/**
 * Created by Alex on 4/9/2017.
 */

public interface WebToAppModelConverter {
    List<Currency> webToAppModel(CurrencyTable currencyTable);
}
