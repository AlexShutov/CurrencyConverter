package com.example.alex.currencyconverter.converter;

import android.os.AsyncTask;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.app.Currency;

/**
 * Created by Alex on 4/9/2017.
 */

public class ConversionTask extends AsyncTask<Void, Void, Double> {

    public interface ConversionCallback {
        void onResult(double value);
        void onError();
    }

    public static class Builder {
        private CurrencyDao dao;
        private ConverterAlgorithm algorithm;
        private ConversionCallback callback;
        private String fromId;
        private String toId;
        private double amount;

        public Builder setDao(CurrencyDao dao) {
            this.dao = dao;
            return this;
        }

        public Builder setAlgorithm(ConverterAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder setCallback(ConversionCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setFromId(String fromId) {
            this.fromId = fromId;
            return this;
        }

        public Builder setToId(String toId) {
            this.toId = toId;
            return this;
        }

        public Builder setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public ConversionTask build(){
            ConversionTask task = new ConversionTask();
            task.dao = dao;
            task.algorithm = algorithm;
            task.callback = callback;
            task.fromId = fromId;
            task.toId = toId;
            task.amount = amount;
            return task;
        }
    }

    private CurrencyDao dao;
    private ConverterAlgorithm algorithm;
    private ConversionCallback callback;
    private String fromId;
    private String toId;
    private double amount;

    private ConversionTask(){}

    @Override
    protected Double doInBackground(Void... params) {
        Currency from = dao.getCurrencyById(fromId);
        Currency to = dao.getCurrencyById(toId);
        double result = 0;
        try {
            result = algorithm.convert(amount, from, to);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return -1.0;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Double v) {
        if (null != callback) {
            if (-1.0 == v){
                callback.onError();
            } else {
                callback.onResult(v);
            }
        }
    }
}
