package com.example.alex.currencyconverter;

import android.os.AsyncTask;

import com.example.alex.currencyconverter.web.WebFetcher;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * Unfortunately, test assignment forbids using third- party libraries, so we can't use RxJava
 * library so have to stick to obsolete AsyncTask, because we need to
 * show popup after table sync is complete.
 */
public class SyncTableTask extends AsyncTask<Void, Void, String> {

    /**
     * We need to avoid memory leaks from within AsyncTask. Wrap this interface
     * implementation into WeakReference and notify it after update is complete.
     */
    public interface SyncCallback {
        void handleUpdateResultOnUiThread(String result);
        void onSyncFailed();
    }

    private WeakReference<SyncCallback> uiCallback;

    /**
     * Save reference to callback.
     * @param resultAction
     */
    public SyncTableTask(SyncCallback resultAction){
        uiCallback = new WeakReference<>(resultAction);
    }

    /**
     * Attempt to fetch table from a web and parse it into application model
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(Void... params) {
        String url = BuildConfig.EXCANGE_RATE_TABLE_URL;
        WebFetcher fetcher = new WebFetcher();
        String ratesTable;
        try {
            ratesTable = fetcher.loadData(url);
        } catch (IOException e) {
            ratesTable = null;
            e.printStackTrace();
        }
        return ratesTable;
    }

    /**
     * Inform callback of sync success or failure
     * @param table
     */
    @Override
    protected void onPostExecute(String table) {
        SyncCallback callback = uiCallback.get();
        if (null != callback){
            if (null != table){
                callback.handleUpdateResultOnUiThread(table);
            } else {
                callback.onSyncFailed();
            }
        }
    }
}
