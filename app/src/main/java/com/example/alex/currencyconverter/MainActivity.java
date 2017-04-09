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
import static com.example.alex.currencyconverter.converter.ConverterModel.ConvertorUserActionEnum.*;

public class MainActivity extends AppCompatActivity implements
        UpdatableView<ConverterModel, ConverterModel.ConvertorQueryEnum, ConverterModel.ConvertorUserActionEnum> {

    private UserActionListener userActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMvp();

        Bundle args = new Bundle();
        userActionListener.onUserAction(LOAD_LIST_OF_CURRENCIES_FROM, args);
        userActionListener.onUserAction(LOAD_LIST_OF_CURRENCIES_TO, new Bundle());
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
    public void displayUserActionResult(ConverterModel model, ConvertorUserActionEnum userAction,
                                        boolean success) {
        switch (userAction) {
            case LOAD_LIST_OF_CURRENCIES_FROM:
                if (success){
                    int n = 20;
                } else {
                    int n = 100;
                }
                break;
            case LOAD_LIST_OF_CURRENCIES_TO:
                if (success){
                    int n = 20;
                } else {
                    int n = 100;
                }
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
}
