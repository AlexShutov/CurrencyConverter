package com.example.alex.currencyconverter;

import android.os.AsyncTask;

import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.parsing.WebResponseParser;
import com.example.alex.currencyconverter.parsing.impl.WebResponseSimpleXmlParser;
import com.example.alex.currencyconverter.web.WebFetcher;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * Unfortunately, test assignment forbids using third- party libraries, so we can't use RxJava
 * library so have to stick to obsolete AsyncTask, because we need to
 * show popup after table sync is complete.
 */
public class SyncTableTask extends AsyncTask<Void, Void, List<Currency>> {

    /**
     * We need to avoid memory leaks from within AsyncTask. Wrap this interface
     * implementation into WeakReference and notify it after update is complete.
     */
    public interface SyncCallback {
        void handleUpdateResultOnUiThread(List<Currency> syncedModel);
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
    protected List<Currency> doInBackground(Void... params) {
        String url = BuildConfig.EXCANGE_RATE_TABLE_URL;
        SyncFacade.Builder builder = new SyncFacade.Builder();
        // use SimpleXml library for parsing web response
        WebResponseParser parser = new WebResponseSimpleXmlParser();
        // uses out-of-the-box HttpUrlConnection
        WebFetcher webFetcher = new WebFetcher();

        builder.setWebAddressUrl(url);
        builder.setParser(parser);
        builder.setWebFetcher(webFetcher);
        SyncFacade syncFacade = builder.build();
        try {
            List<Currency> syncedResult = syncFacade.performSync();

            return syncedResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inform callback of sync success or failure
     * @param syncedModel
     */
    @Override
    protected void onPostExecute(List<Currency> syncedModel) {
        SyncCallback callback = uiCallback.get();
        if (null != callback){
            if (null != syncedModel){
                callback.handleUpdateResultOnUiThread(syncedModel);
            } else {
                callback.onSyncFailed();
            }
        }
    }
}
