package com.example.alex.currencyconverter;

import android.app.Application;
import android.widget.Toast;

import com.example.alex.currencyconverter.archframework.Model;
import com.example.alex.currencyconverter.archframework.QueryEnum;
import com.example.alex.currencyconverter.converter.ConverterModel;
import com.example.alex.currencyconverter.dao.impl.CurrencyDaoImpl;
import com.example.alex.currencyconverter.model.app.Currency;

import java.util.List;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * This custom application class attempts to update saved table of exchange rates on every
 * start of application. User can request update manually.
 */
public class UpdatingOnStartupApplication extends Application {

    private SyncTableTask syncTask;
    private CurrencyDaoImpl dao;

    private ConverterModel converterModel;

    private SyncTableTask.SyncCallback syncCallback = new SyncTableTask.SyncCallback() {
        @Override
        public void handleUpdateResultOnUiThread(List<Currency> syncedModel) {

        }

        @Override
        public void onSyncFailed() {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new CurrencyDaoImpl(this);
        converterModel = new ConverterModel(dao);
        converterModel.requestData(ConverterModel.ConvertorQueryEnum.SYNC_DATABASE,
                new Model.DataQueryCallback() {
                    @Override
                    public void onModelUpdated(Model model, QueryEnum query) {
                        Toast.makeText(UpdatingOnStartupApplication.this,
                                getString(R.string.database_synced), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QueryEnum query) {
                        Toast.makeText(UpdatingOnStartupApplication.this,
                                "Database update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (null != syncTask) {
            syncTask.cancel(true);
        }
        dao.onDestroy();
    }


    public void attemptSync(final SyncTableTask.SyncCallback action) throws
            IllegalStateException {
        // sync is active
        if (null != syncTask){
            throw new IllegalStateException("App undergo sync right now");
        }
        // create new sync task
        syncTask = new SyncTableTask(dao, new SyncTableTask.SyncCallback() {
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

    private void attemptSync() {
        attemptSync(syncCallback);
    }

    public ConverterModel getConverterModel() {
        return converterModel;
    }
}
