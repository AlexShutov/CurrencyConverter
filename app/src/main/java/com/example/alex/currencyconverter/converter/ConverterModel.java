package com.example.alex.currencyconverter.converter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.alex.currencyconverter.LoadCurrenciesFromDatabaseTask;
import com.example.alex.currencyconverter.SyncTableTask;
import com.example.alex.currencyconverter.archframework.Model;
import com.example.alex.currencyconverter.archframework.QueryEnum;
import com.example.alex.currencyconverter.archframework.UserActionEnum;
import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.app.Currency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 4/9/2017.
 */

public class ConverterModel implements Model<ConverterModel.ConvertorQueryEnum,
        ConverterModel.ConvertorUserActionEnum> {


    /**
     * User should not be able to select the same currency. For example: USD -> RUR
     * If RUR is selected as destination currency, it should not be available as original
     * currency.
     * Model filters request result by currency under that Id.
     */
    public static final String KEY_OTHER_CURRENCY_ID = "other_currency";


    // database, storing all currency rates.
    private CurrencyDao currencyDao;

    // AsyncTask, used for database synchronization.
    private SyncTableTask syncTask;

    private List<Currency> originCurrencies;
    private List<Currency> destinationCurrencies;


    public ConverterModel(CurrencyDao currencyDao){
        this.currencyDao = currencyDao;
    }

    @Override
    public ConvertorQueryEnum[] getQueries() {
        return ConvertorQueryEnum.values();
    }

    @Override
    public ConvertorUserActionEnum[] getUserActions() {
        return ConvertorUserActionEnum.values();
    }

    private void attemptSync(final SyncTableTask.SyncCallback action) throws
            IllegalStateException {
        // sync is active
        if (null != syncTask){
            throw new IllegalStateException("App undergo sync right now");
        }
        // create new sync task
        syncTask = new SyncTableTask(currencyDao, new SyncTableTask.SyncCallback() {
            @Override
            public void handleUpdateResultOnUiThread(List<Currency> syncedModel) {
                action.handleUpdateResultOnUiThread(syncedModel);
                syncTask = null;

            }

            @Override
            public void onSyncFailed() {
                action.onSyncFailed();
                syncTask = null;
            }
        });
        syncTask.execute();
    }

    @Override
    public void deliverUserAction(final ConvertorUserActionEnum action,
                                  @Nullable Bundle args, final UserActionCallback callback) {
        switch (action){
            case LOAD_LIST_OF_CURRENCIES_FROM:
                // load data from database

                String skipIdFrom = null;
                if (args.containsKey(KEY_OTHER_CURRENCY_ID)){
                    skipIdFrom = args.getString(KEY_OTHER_CURRENCY_ID);
                }
                // load list of currencies from database asynchronously
                LoadCurrenciesFromDatabaseTask loadTaskFrom = new LoadCurrenciesFromDatabaseTask(
                        currencyDao,
                        null == skipIdFrom ? null : Arrays.asList(skipIdFrom),
                        new LoadCurrenciesFromDatabaseTask.DbFetchCallback() {
                            @Override
                            public void onCurrenciesLoaded(List<Currency> currencies) {
                                // save loaded currencies in model
                                setOriginCurrencies(currencies);
                                callback.onModelUpdated(ConverterModel.this, action);
                            }
                        }
                );
                loadTaskFrom.execute();
                break;

            case LOAD_LIST_OF_CURRENCIES_TO:
                String skipIdTo = null;
                if (args.containsKey(KEY_OTHER_CURRENCY_ID)){
                    skipIdFrom = args.getString(KEY_OTHER_CURRENCY_ID);
                }
                // load list of currencies from database asynchronously
                LoadCurrenciesFromDatabaseTask loadTaskTo = new LoadCurrenciesFromDatabaseTask(
                        currencyDao,
                        null == skipIdTo ? null : Arrays.asList(skipIdTo),
                        new LoadCurrenciesFromDatabaseTask.DbFetchCallback() {
                            @Override
                            public void onCurrenciesLoaded(List<Currency> currencies) {
                                // save loaded currencies in model
                                setDestinationCurrencies(currencies);
                                callback.onModelUpdated(ConverterModel.this, action);
                            }
                        }
                );
                loadTaskTo.execute();
                break;


            default:
        }
    }


    // Accessors

    public List<Currency> getOriginCurrencies() {
        return originCurrencies;
    }

    public void setOriginCurrencies(List<Currency> originCurrencies) {
        this.originCurrencies = originCurrencies;
    }

    public List<Currency> getDestinationCurrencies() {
        return destinationCurrencies;
    }

    public void setDestinationCurrencies(List<Currency> destinationCurrencies) {
        this.destinationCurrencies = destinationCurrencies;
    }

    /**
     * Initializa this model. This method should be called when model is just
     * created and we need to perform database synchronization.
     * This method will be called not from View
     * @param query
     * @param callback
     */
    @Override
    public void requestData(final ConvertorQueryEnum query, final DataQueryCallback callback) {
        switch (query){
            case SYNC_DATABASE:
                try {
                    attemptSync(new SyncTableTask.SyncCallback() {
                        @Override
                        public void handleUpdateResultOnUiThread(List<Currency> syncedModel) {
                            callback.onModelUpdated(ConverterModel.this, query);
                        }

                        @Override
                        public void onSyncFailed() {
                            callback.onError(query);
                        }
                    });
                } catch (IllegalStateException e){
                    callback.onError(query);
                }
                break;
        }
//        TODO:
    }

    @Override
    public void cleanUp() {

    }

    public enum ConvertorQueryEnum implements QueryEnum {

        SYNC_DATABASE(0);

        int id;

        ConvertorQueryEnum(int id){
            this.id  = id;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return null;
        }
    }

    public enum ConvertorUserActionEnum implements UserActionEnum {

        /**
         * Action, passed to Model by View within Bundle to indicate type of user action.
         * That request is passed to model when user press a piece of UI for currerncy selection.
         * Model stores two lists of currencies (to and from).
         */
        LOAD_LIST_OF_CURRENCIES_FROM(0),
        LOAD_LIST_OF_CURRENCIES_TO(1);


        int id;

        ConvertorUserActionEnum(int id){
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }


}
