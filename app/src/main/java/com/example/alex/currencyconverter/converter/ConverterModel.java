package com.example.alex.currencyconverter.converter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.alex.currencyconverter.LoadCurrenciesFromDatabaseTask;
import com.example.alex.currencyconverter.LoadCurrencyByIdTask;
import com.example.alex.currencyconverter.SyncTableTask;
import com.example.alex.currencyconverter.archframework.Model;
import com.example.alex.currencyconverter.archframework.QueryEnum;
import com.example.alex.currencyconverter.archframework.UserActionEnum;
import com.example.alex.currencyconverter.converter.impl.ConvertAlgorithmImpl;
import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.app.Currency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 4/9/2017.
 */

public class ConverterModel implements Model<ConverterModel.ConvertorQueryEnum,
        ConverterModel.ConverterUserActionEnum> {


    /**
     * User should not be able to select the same currency. For example: USD -> RUR
     * If RUR is selected as destination currency, it should not be available as original
     * currency.
     * Model filters request result by currency under that Id.
     */
    public static final String KEY_OTHER_CURRENCY_ID = "other_currency";

    /**
     * Keys for storing data for currency conversion.
     */
    /**
     * Ids of original and destination currencies
     */
    public static final String KEY_CURRENCY_ID_FROM = "currency_id_from";
    public static final String KEY_CURRENCY_ID_TO = "currency_id_to";
    /**
     * UI need to load currency by Id (for example, when Activity is re-created after
     * We need to distinguish resutls for different kinds of requests - for example, load
     * currencies for 'from' and 'to' fields
     */
    public static final String KEY_LOADING_CURRENCY_BY_ID_REQUEST_TYPE = "request_type";
    public static final String KEY_LOADING_CURRENCY_BY_ID_CURRENCY_ID = "currency_ide";
    /**
     * Key of entered amount of money, stored in Bundle
     */
    public static final String KEY_ENTERED_AMOUNT = "key_entered_amount_of_money";


    // database, storing all currency rates.
    private CurrencyDao currencyDao;

    // AsyncTask, used for database synchronization.
    private SyncTableTask syncTask;

    private List<Currency> originCurrencies;
    private List<Currency> destinationCurrencies;

    private Map<String, Currency> currencyRequestResults = new HashMap<>();

    /**
     * Stores value, converted to selected currency
     */
    private double convertedValue;

    public ConverterModel(CurrencyDao currencyDao){
        this.currencyDao = currencyDao;
    }

    @Override
    public ConvertorQueryEnum[] getQueries() {
        return ConvertorQueryEnum.values();
    }

    @Override
    public ConverterUserActionEnum[] getUserActions() {
        return ConverterUserActionEnum.values();
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
                syncTask = null;
                action.handleUpdateResultOnUiThread(syncedModel);
            }

            @Override
            public void onSyncFailed() {
                syncTask = null;
                action.onSyncFailed();
            }
        });
        syncTask.execute();
    }

    @Override
    public void deliverUserAction(final ConverterUserActionEnum action,
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
                    skipIdTo = args.getString(KEY_OTHER_CURRENCY_ID);
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
            /**
             * Load currency from database by currency Id and notify View when task is complete.
             */
            case LOAD_CURRENCY_BY_ID:
                final String requestType = args.getString(KEY_LOADING_CURRENCY_BY_ID_REQUEST_TYPE);
                String currencyId = args.getString(KEY_LOADING_CURRENCY_BY_ID_CURRENCY_ID);
                LoadCurrencyByIdTask loadTask = new LoadCurrencyByIdTask(currencyDao,
                        new LoadCurrencyByIdTask.Callback() {
                            @Override
                            public void onResult(Currency currency) {
                                if (null != currency){
                                    setCurrencyForResult(requestType, currency);
                                    callback.onModelUpdated(ConverterModel.this, action);
                                } else {
                                    callback.onError(action);
                                }
                            }
                        },
                        currencyId);
                loadTask.execute();
                break;
            /**
             * Extract arguments from Bundle argument and convert value from one currency
             * to another. Notice, this is done in background, because both currencies is
             * being queried from database.
             */
            case CONVERT_CURRENCIES:
                String currencyIdFrom = args.getString(KEY_CURRENCY_ID_FROM);
                String currencyIdTo = args.getString(KEY_CURRENCY_ID_TO);
                double amount = args.getDouble(KEY_ENTERED_AMOUNT);
                // algorithm for converting currencies
                ConverterAlgorithm algorithm = new ConvertAlgorithmImpl();
                ConversionTask.Builder builder = new ConversionTask.Builder();
                builder.setAlgorithm(algorithm)
                        .setDao(currencyDao)
                        .setAmount(amount)
                        .setFromId(currencyIdFrom)
                        .setToId(currencyIdTo)
                        .setCallback(new ConversionTask.ConversionCallback() {
                            @Override
                            public void onResult(double value) {
                                setConvertedValue(value);
                                callback.onModelUpdated(ConverterModel.this, action);
                            }

                            @Override
                            public void onError() {
                                callback.onError(action);
                            }
                        });
                ConversionTask task = builder.build();
                task.execute();
                break;


            default:
        }
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

    public double getConvertedValue() {
        return convertedValue;
    }

    public void setConvertedValue(double convertedValue) {
        this.convertedValue = convertedValue;
    }

    public synchronized Currency getCurrencyForRequest(String requestType){
        Currency c = currencyRequestResults.get(requestType);
        currencyRequestResults.remove(requestType);
        return c;
    }

    public synchronized void setCurrencyForResult(String requestType, Currency result){
        currencyRequestResults.put(requestType, result);
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

    public enum ConverterUserActionEnum implements UserActionEnum {

        /**
         * Action, passed to Model by View within Bundle to indicate type of user action.
         * That request is passed to model when user press a piece of UI for currency selection.
         * Model stores two lists of currencies (to and from).
         */
        LOAD_LIST_OF_CURRENCIES_FROM(0),
        LOAD_LIST_OF_CURRENCIES_TO(1),
        LOAD_CURRENCY_BY_ID(2),
        /**
         * Action, triggering currency conversion
         */
        CONVERT_CURRENCIES(3);

        int id;

        ConverterUserActionEnum(int id){
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }


}
