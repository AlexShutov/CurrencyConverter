package com.example.alex.currencyconverter;

import android.app.Application;

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
        attemptSync();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (null != syncTask) {
            syncTask.cancel(true);
        }
    }


    public void attemptSync(final SyncTableTask.SyncCallback action) throws
            IllegalStateException {
        // sync is active
        if (null != syncTask){
            throw new IllegalStateException("App undergo sync right now");
        }
        // create new sync task
        syncTask = new SyncTableTask(new SyncTableTask.SyncCallback() {
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

}
