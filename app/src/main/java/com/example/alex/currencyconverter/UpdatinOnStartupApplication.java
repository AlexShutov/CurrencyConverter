package com.example.alex.currencyconverter;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import com.example.alex.currencyconverter.BuildConfig;
import com.example.alex.currencyconverter.web.WebFetcher;

import java.io.IOException;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * This custom application class attempt to update saved table of excange rates on every
 * start of application. User can request update manually.
 */
public class UpdatinOnStartupApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        attemptSync();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public void attemptSync(){

        UpdateTableTask task = new UpdateTableTask();
        task.execute();
    }

    /**
     * Unfortunately, test assignment forbids using third- party libraries, so we can't use RxJava
     * library so have to stick to obsolete AsyncTask for convenience, because we need to
     * show popup after table sync is complete.
     */
    private static class UpdateTableTask extends AsyncTask<Void, Void, String> {

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

        @Override
        protected void onPostExecute(String table) {

        }
    }

}
