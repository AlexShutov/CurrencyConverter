package com.example.alex.currencyconverter.model.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.example.alex.currencyconverter.BR;
import com.example.alex.currencyconverter.model.app.Currency;

/**
 * Created by Alex on 4/10/2017.
 */

public class CurrencyPickerModel extends BaseObservable {

    private boolean isCurrencyPicked;

    private String selectPrompt;

    private Currency currency;

    private View.OnClickListener removeClickListener;

    private View.OnClickListener pickCurrencyListener;

    @Bindable
    public boolean isCurrencyPicked() {
        return isCurrencyPicked;
    }

    public void setCurrencyPicked(boolean currencyPicked) {
        isCurrencyPicked = currencyPicked;
        notifyPropertyChanged(BR.currencyPicked);
    }

    @Bindable
    public String getSelectPrompt() {
        return selectPrompt;
    }

    public void setSelectPrompt(String selectPrompt) {
        this.selectPrompt = selectPrompt;
        notifyPropertyChanged(BR.selectPrompt);
    }

    @Bindable
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        notifyPropertyChanged(BR.currency);
    }

    @Bindable
    public View.OnClickListener getRemoveClickListener() {
        return removeClickListener;
    }

    public void setRemoveClickListener(View.OnClickListener removeClickListener) {
        this.removeClickListener = removeClickListener;
        notifyPropertyChanged(BR.removeClickListener);
    }

    @Bindable
    public View.OnClickListener getPickCurrencyListener() {
        return pickCurrencyListener;
    }

    public void setPickCurrencyListener(View.OnClickListener pickCurrencyListener) {
        this.pickCurrencyListener = pickCurrencyListener;
        notifyPropertyChanged(BR.pickCurrencyListener);
    }
}
