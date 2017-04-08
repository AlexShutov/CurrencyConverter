package com.example.alex.currencyconverter.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.alex.currencyconverter.model.app.Currency;

import static com.example.alex.currencyconverter.dao.impl.CurrencyExchangeRateContract.*;
import static com.example.alex.currencyconverter.dao.impl.CurrencyExchangeRateContract.COLUMN_CURRENCY_ID;

/**
 * Created by Alex on 4/8/2017.
 */

public class CurrencyParserImpl implements CurrencyCursorParser {

    @Override
    public Currency fromCursor(Cursor cursor) throws IllegalArgumentException {
        Currency currency = new Currency();
        String currencyId = cursor.getString(cursor.getColumnIndex(
                COLUMN_CURRENCY_ID));
        if (null == currencyId || currencyId.isEmpty()){
            throw new IllegalArgumentException("Currency Id must not be empty");
        }
        currency.setCurrencyId(currencyId);
        String currencyName = cursor.getString(cursor.getColumnIndex(
                COLUMN_NAME));
        currency.setName(currencyName);
        String charCode = cursor.getString(cursor.getColumnIndex(
                COLUMN_CHAR_CODE
        ));
        currency.setCharCode(charCode);
        double exchangeRate = cursor.getDouble(cursor.getColumnIndex(
                COLUMN_EXCHANGE_RATE_IN_ROUBLES
        ));
        if (0 == exchangeRate){
            throw new IllegalArgumentException("Exchange rate must not be equal 0");
        }
        currency.setExchangeValueInRoubles(exchangeRate);
        return currency;
    }

    @Override
    public ContentValues toCursor(Currency currency) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CURRENCY_ID, currency.getCurrencyId());
        contentValues.put(COLUMN_NAME, currency.getName());
        contentValues.put(COLUMN_CHAR_CODE,
                currency.getCharCode());
        contentValues.put(COLUMN_EXCHANGE_RATE_IN_ROUBLES,
                currency.getExchangeValueInRoubles());
        return contentValues;
    }
}
