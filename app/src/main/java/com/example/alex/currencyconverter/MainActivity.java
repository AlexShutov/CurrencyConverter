package com.example.alex.currencyconverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

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

    private static final String KEY_CURRENCY_SAVED_ID_FROM = "currency_from";
    private static final String KEY_CURRENCY_SAVED_ID_TO = "currency_to";

    // Link to Presenter
    private UserActionListener userActionListener;
    /**
     * SharedPrefernces is used for storing Id of last selected currency. App will attempt
     * to load in on startup
     */
    SharedPreferences prefs;
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
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        initMvp();
        setupWidgets();
    }

    /**
     * Save Id of selected currencies into SharedPreferences
     */
    @Override
    protected void onPause() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Currency from = fromViewModel.getCurrency();
        Currency to = toViewModel.getCurrency();
        SharedPreferences.Editor edit = prefs.edit();
        if (null != from) {
            edit.putString(KEY_CURRENCY_SAVED_ID_FROM, from.getCurrencyId());
        } else {
            edit.remove(KEY_CURRENCY_SAVED_ID_FROM);
        }
        if (null != to) {
            edit.putString(KEY_CURRENCY_SAVED_ID_TO, to.getCurrencyId());
        } else {
            edit.remove(KEY_CURRENCY_SAVED_ID_TO);
        }
        // save in background
        edit.apply();
        super.onPause();
    }

    /**
     * Load ids of selected currencies from SharedPreferences and try to reload info
     * on those currencies.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // reload 'from' currency after device rotation
        if (prefs.contains(KEY_CURRENCY_SAVED_ID_FROM)){
            String idFrom = prefs.getString(KEY_CURRENCY_SAVED_ID_FROM, "");
            // load info on this currency
            Bundle args = new Bundle();
            args.putString(KEY_LOADING_CURRENCY_BY_ID_REQUEST_TYPE, KEY_CURRENCY_SAVED_ID_FROM);
            args.putString(KEY_LOADING_CURRENCY_BY_ID_CURRENCY_ID, idFrom);
            userActionListener.onUserAction(LOAD_CURRENCY_BY_ID, args);
        }
        // reload 'to' currency after device rotation
        if (prefs.contains(KEY_CURRENCY_SAVED_ID_TO)){
            String idFrom = prefs.getString(KEY_CURRENCY_SAVED_ID_TO, "");
            // load info on this currency
            Bundle args = new Bundle();
            args.putString(KEY_LOADING_CURRENCY_BY_ID_REQUEST_TYPE, KEY_CURRENCY_SAVED_ID_TO);
            args.putString(KEY_LOADING_CURRENCY_BY_ID_CURRENCY_ID, idFrom);
            userActionListener.onUserAction(LOAD_CURRENCY_BY_ID, args);
        }
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
            case LOAD_CURRENCY_BY_ID:
                // when user picked currency from list
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
                // when currency is saved after device rotation
                currencyFrom = model.getCurrencyForRequest(KEY_CURRENCY_SAVED_ID_FROM);
                if (null != currencyFrom){
                    fromViewModel.setCurrency(currencyFrom);
                    fromViewModel.setCurrencyPicked(true);
                }
                currencyTo = model.getCurrencyForRequest(KEY_CURRENCY_SAVED_ID_TO);
                if (null != currencyTo){
                    toViewModel.setCurrency(currencyTo);
                    toViewModel.setCurrencyPicked(true);
                }

                chechInputTextReadiness();
                break;
            case CONVERT_CURRENCIES:
                double converted = model.getConvertedValue();
                String formattedResult = String.format("%.2f", converted);
                viewBinding.conversionResult.setText(formattedResult);
                // conversion is successful, we now know char code of destination
                // currency
                viewBinding.toCurrencyCode.setText(toViewModel.getCurrency().getCharCode());
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
        initEntryFieldAndConvertButton();
        chechInputTextReadiness();
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
                args.putString(KEY_OTHER_CURRENCY_ID, another.getCurrencyId());
            }
            userActionListener.onUserAction(LOAD_LIST_OF_CURRENCIES_TO, args);
        }
    }

    /**
     * Verifies that both currencies is selected by user and shows field for entering text
     * if it is.
     */
    private void chechInputTextReadiness(){
        boolean fromPicked = fromViewModel.isCurrencyPicked();
        boolean toPicked = toViewModel.isCurrencyPicked();
        if (fromPicked && toPicked){
            // set currency code in input area
            viewBinding.fromCurrencyCode.setText(fromViewModel.getCurrency().getCharCode());
            viewBinding.amountEntryContainer.setVisibility(View.VISIBLE);
        } else {
            viewBinding.amountEntryContainer.setVisibility(View.GONE);
        }
        // clear result fields
        clearResultFields();
        checkIfConvertButtonIsActive();
    }

    private void clearResultFields(){
        viewBinding.conversionResult.setText("");
        viewBinding.toCurrencyCode.setText("");
    }

    private void checkIfConvertButtonIsActive(){
        boolean isBothCurrenciesPicked =
                viewBinding.amountEntryContainer.getVisibility() == View.VISIBLE;
        // container with amoun input field is hidden - user did not pick some currency,
        // aborting.
        if (!isBothCurrenciesPicked){
            viewBinding.buttonConvert.setVisibility(View.GONE);
            return;
        }
        viewBinding.buttonConvert.setVisibility(View.VISIBLE);
    }

    private void initEntryFieldAndConvertButton(){
        EditText entryField = viewBinding.amountEntry;
        entryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearResultFields();
                handleEnteredValue(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        viewBinding.buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get text, entered by user
                String text = viewBinding.amountEntry.getText().toString();
                // there will be no Exception, because button would not be visible
                // in that case
                double amount = parseValue(text);
                // request currency conversion from model
                Bundle args = new Bundle();
                args.putString(KEY_CURRENCY_ID_FROM, fromViewModel.getCurrency().getCurrencyId());
                args.putString(KEY_CURRENCY_ID_TO, toViewModel.getCurrency().getCurrencyId());
                args.putDouble(KEY_ENTERED_AMOUNT, amount);
                userActionListener.onUserAction(CONVERT_CURRENCIES, args);
            }
        });
    }

    /**
     * Verify if entered value is valid and change visibility of 'convert' button
     * accordingly.
     * @param input
     */
    private void handleEnteredValue(String input){
        TextInputLayout entryLayout = viewBinding.amountEntryLayout;
        double val = 0;
        try {
            val = parseValue(input);
        } catch (IllegalArgumentException e){
            entryLayout.setError(getString(R.string.text_enter_valid_number));
            // hide convert button
            viewBinding.buttonConvert.setVisibility(View.GONE);
            return;
        }
        if (val < 0){
            entryLayout.setError(getString(R.string.text_enter_negative));
            viewBinding.buttonConvert.setVisibility(View.GONE);
            return;
        }
        // user input is correct
        entryLayout.setError("");
        viewBinding.buttonConvert.setVisibility(View.VISIBLE);
    }

    /**
     * Parses values, entered by user;
     * @param text
     * @return
     */
    double parseValue(String text) throws IllegalArgumentException{
        text = text.replace(',', '.');
        double val = Double.valueOf(text);
        return val;
    }
}
