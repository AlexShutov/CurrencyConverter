package com.example.alex.currencyconverter.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.alex.currencyconverter.model.app.Currency;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * Know how to convert Currency model into SQLite Cursor.
 * This logic is abstracted out to avoid changes in dao caused by changes in
 * Currency app model
 */
public interface CurrencyCursorParser {
    Currency fromCursor(Cursor cursor) throws IllegalArgumentException;
    ContentValues toCursor(Currency currency);
}
