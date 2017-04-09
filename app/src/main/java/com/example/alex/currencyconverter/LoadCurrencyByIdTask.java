package com.example.alex.currencyconverter;

import android.os.AsyncTask;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.app.Currency;

/**
 * Created by Alex on 4/9/2017.
 */

public class LoadCurrencyByIdTask extends AsyncTask<Void, Void, Currency> {

    public interface Callback {
        void onResult(Currency currency);
    }

    private CurrencyDao database;
    private Callback callback;
    private String currencyId;

    public LoadCurrencyByIdTask(CurrencyDao database, Callback callback,
                                          String currencyId){
        this.database = database;
        this.callback = callback;
        this.currencyId = currencyId;
    }

    @Override
    protected Currency doInBackground(Void... params) {
        Currency currency = null;
        try {
            currency = database.getCurrencyById(currencyId);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return currency;
    }

    @Override
    protected void onPostExecute(Currency currency) {
        if (null != callback){
            callback.onResult(currency);
        }
    }
}
