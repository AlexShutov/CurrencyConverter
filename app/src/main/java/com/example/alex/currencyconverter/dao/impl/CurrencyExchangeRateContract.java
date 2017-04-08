package com.example.alex.currencyconverter.dao.impl;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * Database table name and column names, used in database for exchange rates
 */
public class CurrencyExchangeRateContract {

    public static final String TABLE_NAME = "currency_table";
    public static final String KEY_ID = "id";

    public static final String COLUMN_CURRENCY_ID = "currency_id";
    public static final String COLUMN_NAME = "currency_name";
    public static final String COLUMN_CHAR_CODE = "currency_char_code";
    public static final String COLUMN_EXCHANGE_RATE_IN_ROUBLES = "rate_in_roubles";


}
