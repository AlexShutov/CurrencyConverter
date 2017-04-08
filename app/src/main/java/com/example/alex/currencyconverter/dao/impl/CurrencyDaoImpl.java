package com.example.alex.currencyconverter.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.app.Currency;

import java.util.ArrayList;
import java.util.List;

import static com.example.alex.currencyconverter.dao.impl.CurrencyExchangeRateContract.*;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * Implementation of Dao for currency rates table, which use out-of-the-box SQLite database.
 * Database write operations is not thread-safe by default. We can use ContentProvider, but it will
 * complicate implementation, even though we need to write database once during synchronization
 * process. Here we use synchronized write methods.
 */
public class CurrencyDaoImpl implements CurrencyDao {

    private CurrencyDbHelper dbHelper;
    CurrencyCursorParser cursorParser;

    public CurrencyDaoImpl(Context context){
        dbHelper = new CurrencyDbHelper(context);
        // may want to use constructor injection instead
        cursorParser = new CurrencyParserImpl();
    }

    @Override
    public synchronized void dropTable() {
        dbHelper.dropTable();
    }

    /**
     * Query all rows from exchange rates table
     * @return
     */
    @Override
    public List<Currency> getAllCurrencyRates() {
        List<Currency> currencies = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(selectQuery, null);
        // skip empty cursor
        if (null == c || c.isAfterLast()) {
            return currencies;
        }
        c.moveToFirst();
        do {
            try {
                Currency currency = cursorParser.fromCursor(c);
                currencies.add(currency);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            // parse currency from cursor
            c.moveToNext();
        } while (!c.isAfterLast());
        return currencies;
    }

    @Override
    public Currency getCurrencyById(String id) throws IllegalArgumentException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                + COLUMN_CURRENCY_ID + " = " + id;
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);
        } catch (SQLiteException e){
            throw new IllegalArgumentException(e);
        }
        if (null == c || c.isAfterLast()) {
            throw new IllegalArgumentException("Db has no currency with id: " + id);
        }
        // rewind cursor
        c.moveToFirst();
        Currency currency = cursorParser.fromCursor(c);
        return currency;
    }

    @Override
    public synchronized void saveCurrency(Currency currency) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues cv = cursorParser.toCursor(currency);
        long id = database.insert(TABLE_NAME, null, cv);
    }

    /**
     * Make bulk insert in one transaction
     * @param currencies
     */
    @Override
    public synchronized void saveCurrencies(List<Currency> currencies) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            for (Currency currency : currencies){
                ContentValues cv = cursorParser.toCursor(currency);
                database.insert(TABLE_NAME, null, cv);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Close connection to database when dao is no longer needed.
     * This method is not in Dao interface, because some databases have no need in
     * performing cleanup (Realm)
     */
    public void onDestroy(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.close();
    }

}
