package com.example.alex.currencyconverter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.alex.currencyconverter.archframework.PresenterImpl;
import com.example.alex.currencyconverter.archframework.UpdatableView;
import com.example.alex.currencyconverter.converter.ConverterModel;
import com.example.alex.currencyconverter.model.app.Currency;

import java.util.List;

import static com.example.alex.currencyconverter.converter.ConverterModel.*;
import static com.example.alex.currencyconverter.converter.ConverterModel.ConverterUserActionEnum.*;

public class MainActivity extends AppCompatActivity implements
        UpdatableView<ConverterModel, ConverterModel.ConvertorQueryEnum, ConverterUserActionEnum> {

    private static final String KEY_CURRENCY_REQUEST_BY_ID_FROM = "currency_from";
    private static final String KEY_CURRENCY_REQUEST_BY_ID_TO = "currency_to";

    private UserActionListener userActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMvp();

        Bundle args = new Bundle();
        userActionListener.onUserAction(LOAD_LIST_OF_CURRENCIES_FROM, args);
    }

    @Override
    public Uri getDataUri(ConvertorQueryEnum query) {
        return null;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void displayData(ConverterModel model, ConvertorQueryEnum query) {

    }

    @Override
    public void displayErrorMessage(ConvertorQueryEnum query) {

    }

    @Override
    public void displayUserActionResult(ConverterModel model, ConverterUserActionEnum userAction,
                                        boolean success) {
        switch (userAction) {
            case LOAD_LIST_OF_CURRENCIES_FROM:
                Currency from = model.getOriginCurrencies().get(0);
                Currency to = model.getOriginCurrencies().get(1);
                double amount = 999.9;
                Bundle args = new Bundle();
                args.putString(KEY_CURRENCY_ID_FROM, from.getCurrencyId());
                args.putString(KEY_CURRENCY_ID_TO, to.getCurrencyId());
                args.putDouble(KEY_ENTERED_AMOUNT, amount);
                userActionListener.onUserAction(CONVERT_CURRENCIES, args);

                // request currency by Id
                String id = from.getCurrencyId();
                Bundle arg2 = new Bundle();
                arg2.putString(KEY_LOADING_CURRENCY_BY_ID_CURRENCY_ID, id);
                arg2.putString(KEY_LOADING_CURRENCY_BY_ID_REQUEST_TYPE,
                        KEY_CURRENCY_REQUEST_BY_ID_FROM);
                userActionListener.onUserAction(LOAD_CURRENCY_BY_ID, arg2);

                break;
            case LOAD_LIST_OF_CURRENCIES_TO:

                break;
            case CONVERT_CURRENCIES:
                double value = model.getConvertedValue();
                Toast.makeText(MainActivity.this, "Converted value: " + value, Toast.LENGTH_SHORT)
                        .show();
                break;
            case LOAD_CURRENCY_BY_ID:
                Currency fr = model.getCurrencyForRequest(KEY_CURRENCY_REQUEST_BY_ID_FROM);
                if (null != fr){

                }

            default:
        }
    }

    @Override
    public void addListener(UserActionListener listener) {
        userActionListener = listener;
    }

    private void sync(){
        UpdatingOnStartupApplication app = (UpdatingOnStartupApplication) getApplication();
        try {
            app.attemptSync(new SyncTableTask.SyncCallback() {
                @Override
                public void handleUpdateResultOnUiThread(List<Currency> syncedModel) {
                    Toast.makeText(MainActivity.this, "Received " + syncedModel.size() + "items",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSyncFailed() {
                    Toast.makeText(MainActivity.this, "Sync failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IllegalStateException e){
            Toast.makeText(MainActivity.this, "Syncing already", Toast.LENGTH_SHORT).show();
        }
    }

    private void initMvp(){
        UpdatingOnStartupApplication app = (UpdatingOnStartupApplication) getApplication();
        ConverterModel model = app.getConverterModel();
        PresenterImpl presenter = new PresenterImpl(model, this, values(),
                ConvertorQueryEnum.values());
        addListener(presenter);
    }
}
