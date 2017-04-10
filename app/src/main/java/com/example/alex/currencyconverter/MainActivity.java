package com.example.alex.currencyconverter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
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
        UpdatableView<ConverterModel, ConverterModel.ConvertorQueryEnum, ConverterUserActionEnum>,
        CurrencyDialogFragment.CurrencySelectionListener{

    private static final String PICK_FROM = "pick_from";
    private static final String PICK_TO = "pick_to";

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

                List<Currency> from = model.getOriginCurrencies();
                model.setOriginCurrencies(null);
                CurrencyDialogFragment df = CurrencyDialogFragment.newInstance(from, PICK_FROM);
                df.show(getSupportFragmentManager(), "pick_from_currencies");

                break;
            case LOAD_LIST_OF_CURRENCIES_TO:
                List<Currency> to = model.getDestinationCurrencies();
                model.setDestinationCurrencies(null);
                CurrencyDialogFragment df1 = CurrencyDialogFragment.newInstance(to, PICK_TO);
                df1.show(getSupportFragmentManager(), "pick_to_currencies");
                break;
            case CONVERT_CURRENCIES:

                break;
            case LOAD_CURRENCY_BY_ID:
                Currency currencyFrom = model.getCurrencyForRequest(PICK_FROM);
                if (null != currencyFrom){
                    fromViewModel.setCurrency(currencyFrom);
                    fromViewModel.setCurrencyPicked(true);
                }
                Currency currencyTo = model.getCurrencyForRequest(PICK_TO);
                if (null != currencyTo){
                    toViewModel.setCurrency(currencyTo);
                    toViewModel.setCurrencyPicked(true);
                }
                chechInputTextReadiness();
                break;

            default:
        }
    }

    @Override
    public void addListener(UserActionListener listener) {
        userActionListener = listener;
    }

    /**
     * Callback, called when user select currency from dialog.
     * @param currencyId
     * @param currencyType
     */
    @Override
    public void onCurrencySelected(String currencyId, String currencyType) {
        // User selected currency, now load it by given Id
        Bundle args = new Bundle();
        args.putString(KEY_LOADING_CURRENCY_BY_ID_CURRENCY_ID, currencyId);
        args.putString(KEY_LOADING_CURRENCY_BY_ID_REQUEST_TYPE, currencyType);
        userActionListener.onUserAction(LOAD_CURRENCY_BY_ID, args);
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

        fromViewModel.setSelectPrompt(getString(R.string.select_currency_from));
        fromViewModel.setRemoveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromViewModel.setCurrency(null);
                fromViewModel.setCurrencyPicked(false);
                chechInputTextReadiness();
            }
        });
        fromViewModel.setPickCurrencyListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectCurrencies(PICK_FROM);
            }
        });

        toViewModel = new CurrencyPickerModel();
        viewBinding.currencyPickerTo.setModel(toViewModel);
        toViewModel.setSelectPrompt(getString(R.string.select_currency_to));
        toViewModel.setRemoveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toViewModel.setCurrency(null);
                toViewModel.setCurrencyPicked(false);
                // update other widgets
                chechInputTextReadiness();
            }
        });
        toViewModel.setPickCurrencyListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCurrencies(PICK_TO);
            }
        });
        chechInputTextReadiness();
    }

    /**
     * Verifies that both currencies is selected by user and shows field for entering text
     * if it is.
     */
    private void chechInputTextReadiness(){
        boolean fromPicked = fromViewModel.isCurrencyPicked();
        boolean toPicked = toViewModel.isCurrencyPicked();
        if (fromPicked && toPicked){
            Toast.makeText(MainActivity.this, "Both currencies is selected",
                    Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Select currency from table, stored in database. Ignore currency, selected by another
     * picker, e.g. if we're picking 'to' currency, ignore 'from' currency
     * @param keyCurrencyKind String key, specifying currency kind
     */
    private void selectCurrencies(String keyCurrencyKind){
        Bundle args = new Bundle();
        Currency another;
        if (keyCurrencyKind.equals(PICK_FROM)){
            // loading list of original currencies
            another = toViewModel.getCurrency();
            if (null != another && null != another.getCurrencyId()){
                args.putString(KEY_OTHER_CURRENCY_ID, another.getCurrencyId());
            }
            userActionListener.onUserAction(LOAD_LIST_OF_CURRENCIES_FROM, args);
        } else {
            another = fromViewModel.getCurrency();
            if (null != another && null != another.getCurrencyId()) {
                args.putString(KEY_OTHER_CURRENCY_ID, another.getCharCode());
            }
            userActionListener.onUserAction(LOAD_LIST_OF_CURRENCIES_TO, args);
        }
    }

}
