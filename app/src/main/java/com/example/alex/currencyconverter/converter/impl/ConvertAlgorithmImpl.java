package com.example.alex.currencyconverter.converter.impl;

import com.example.alex.currencyconverter.converter.ConverterAlgorithm;
import com.example.alex.currencyconverter.model.app.Currency;

/**
 * Created by Alex on 4/9/2017.
 */

public class ConvertAlgorithmImpl implements ConverterAlgorithm {

    /**
     * Every currency has a field, indicating exchange rate in Russian rubles, so
     * we need to convert that amount to roubles first, and then re-calculate it in
     * desirable currency.
     * @param amount    amount of money
     * @param from      original currency
     * @param to        destination currency
     * @return
     */
    @Override
    public double convert(double amount, Currency from, Currency to) throws
            IllegalArgumentException {
        if (from.getCurrencyId().equals(to.getCurrencyId())){
            // we want to convert to the same currency
            return amount;
        }
        // there is no point in going any further
        if (0 == amount) {
            return 0;
        }
        // check that exchange rate is not null (money has to worth something)
        if (0 == from.getExchangeValueInRoubles()){
            throw new IllegalArgumentException("Exchange rate of original currency is 0");
        }
        if (0 == to.getExchangeValueInRoubles()){
            throw new IllegalArgumentException("Exchange rate of destination currency is 0");
        }
        double valueInRoubles = amount * from.getExchangeValueInRoubles();
        double valueInDestinationCurrency = valueInRoubles / to.getExchangeValueInRoubles();
        return valueInDestinationCurrency;
    }
}
