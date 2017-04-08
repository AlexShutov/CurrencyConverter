package com.example.alex.currencyconverter.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.alex.currencyconverter.dao.impl.CurrencyExchangeRateContract.*;

/**
 * Created by Alex on 4/8/2017.
 */

public class CurrencyDbHelper extends SQLiteOpenHelper {

    /**
     * If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "weather.db";

    /**
     * This query string is used to create exchange rates table.
     * Use currencyId field as a primary key because we sure server provide unique id values.
     */
    private static final String CREATE_EXCHANGE_RATES_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_CURRENCY_ID + " TEXT PRIMARY KEY," +
            COLUMN_NAME + " TEXT," +
            COLUMN_CHAR_CODE + " TEXT," +
            COLUMN_EXCHANGE_RATE_IN_ROUBLES + " REAL" +
            " );";

    public CurrencyDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXCHANGE_RATES_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable();
    }

    public void dropTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CurrencyExchangeRateContract.TABLE_NAME);
        onCreate(db);
    }
}
