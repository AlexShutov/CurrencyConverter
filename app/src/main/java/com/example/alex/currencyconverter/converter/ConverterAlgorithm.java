package com.example.alex.currencyconverter.converter;

import com.example.alex.currencyconverter.model.app.Currency;

/**
 * Created by Alex on 4/9/2017.
 */

public interface ConverterAlgorithm {

    /**
     * Convert amount of money from one currency into another
     * @param amount    amount of money
     * @param from      original currency
     * @param to        destination currency
     * @return          amount of money in destination currency
     */
    double convert(double amount, Currency from, Currency to) throws IllegalArgumentException;
}
