package com.example.alex.currencyconverter;

import android.os.AsyncTask;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.app.Currency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alex on 4/9/2017.
 */

/**
 * Loads list of currencies from database on background thread and informs UI on a
 * main thread.
 */
public class LoadCurrenciesFromDatabaseTask extends AsyncTask<Void, Void, List<Currency>> {

    public interface DbFetchCallback {
        void onCurrenciesLoaded(List<Currency> currencies);
    }

    private CurrencyDao database;
    private Set<String> skipIds;
    private DbFetchCallback callback;

    public LoadCurrenciesFromDatabaseTask(CurrencyDao database,
                                          List<String> skipIds,
                                          DbFetchCallback callback){
        this.database = database;
        this.callback = callback;
        this.skipIds = new HashSet<>();
        if (null != skipIds && !skipIds.isEmpty()) {
            this.skipIds.addAll(skipIds);
        }
    }


    @Override
    protected List<Currency> doInBackground(Void... params) {
        List<Currency> resFrom = database.getAllCurrencyRates();
        if (!skipIds.isEmpty()){
            List<Currency> filtered = new ArrayList<>();
            for (Currency c : resFrom){
                if (!skipIds.contains(c.getCurrencyId())){
                    filtered.add(c);
                }
            }
            resFrom = filtered;
        }

        return resFrom;
    }

    @Override
    protected void onPostExecute(List<Currency> currencies) {
        if (null != callback) {
            callback.onCurrenciesLoaded(currencies);
        }
    }
}
