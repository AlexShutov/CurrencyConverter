package com.example.alex.currencyconverter;

import android.content.Context;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.dao.impl.CurrencyDaoImpl;

/**
 * Created by Alex on 4/9/2017.
 */

public class SQLiteDaoTest extends DatabaseTest {
    @Override
    public CurrencyDao createDao(Context context) {
        CurrencyDao dao = new CurrencyDaoImpl(context);
        return dao;
    }
}
