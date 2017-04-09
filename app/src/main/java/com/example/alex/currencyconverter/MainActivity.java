package com.example.alex.currencyconverter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.alex.currencyconverter.archframework.PresenterImpl;
import com.example.alex.currencyconverter.archframework.UpdatableView;
import com.example.alex.currencyconverter.converter.ConverterModel;
import com.example.alex.currencyconverter.databinding.ConverterLayoutBinding;
import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.model.ui.CurrencyPickerModel;

import java.util.List;

import static com.example.alex.currencyconverter.converter.ConverterModel.*;
import static com.example.alex.currencyconverter.converter.ConverterModel.ConverterUserActionEnum.*;

public class MainActivity extends AppCompatActivity implements
        UpdatableView<ConverterModel, ConverterModel.ConvertorQueryEnum, ConverterUserActionEnum> {

    private static final String KEY_CURRENCY_REQUEST_BY_ID_FROM = "currency_from";
    private static final String KEY_CURRENCY_REQUEST_BY_ID_TO = "currency_to";

    // Link to Presenter
    private UserActionListener userActionListener;

    // Databinding refrence
    private ConverterLayoutBinding viewBinding;
    // View models
    // View model for original currency
    private CurrencyPickerModel fromViewModel;
    // View model for desirable currency
    private CurrencyPickerModel toViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, R.layout.converter_layout);
        initMvp();
        setupWidgets();
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


                break;
            case LOAD_LIST_OF_CURRENCIES_TO:

                break;
            case CONVERT_CURRENCIES:

                break;
            case LOAD_CURRENCY_BY_ID:

                break;

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

    private void setupWidgets() {
        fromViewModel = new CurrencyPickerModel();
        viewBinding.currencyPickerFrom.setModel(fromViewModel);

        final Currency currency = new Currency();
        currency.setName("Some currency");
        currency.setExchangeValueInRoubles(123);
        fromViewModel.setCurrency(currency);
        fromViewModel.setSelectPrompt(getString(R.string.select_currency_from));
        fromViewModel.setCurrencyPicked(true);
        fromViewModel.setRemoveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromViewModel.setCurrency(null);
                fromViewModel.setCurrencyPicked(false);
                Toast.makeText(MainActivity.this, "clear", Toast.LENGTH_SHORT).show();
            }
        });
        fromViewModel.setPickCurrencyListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Picking currency", Toast.LENGTH_SHORT).show();
            }
        });

        toViewModel = new CurrencyPickerModel();
        viewBinding.currencyPickerTo.setModel(toViewModel);
        toViewModel.setCurrency(currency);
        toViewModel.setSelectPrompt(getString(R.string.select_currency_to));
        toViewModel.setCurrencyPicked(true);
        toViewModel.setRemoveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toViewModel.setCurrency(null);
                toViewModel.setCurrencyPicked(false);
            }
        });
        toViewModel.setPickCurrencyListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Picking currency", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
