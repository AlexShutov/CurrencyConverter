package com.example.alex.currencyconverter;

import com.example.alex.currencyconverter.model.WebToAppModelConverter;
import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.model.web.CurrencyTable;
import com.example.alex.currencyconverter.parsing.WebResponseParser;
import com.example.alex.currencyconverter.web.WebFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alex on 4/8/2017.
 */

public class SyncFacade {

    public static class Builder {
        private WebResponseParser parser;
        private WebFetcher webFetcher;
        private String webAddressUrl;

        public Builder setParser(WebResponseParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder setWebFetcher(WebFetcher webFetcher) {
            this.webFetcher = webFetcher;
            return this;
        }

        public Builder setWebAddressUrl(String webAddressUrl) {
            this.webAddressUrl = webAddressUrl;
            return this;
        }

        public SyncFacade build() throws RuntimeException {
            if (null == parser || null == webFetcher || null == webAddressUrl){
                throw new RuntimeException("Sync algorithm builder has not initialized " +
                        "fields");
            }
            SyncFacade algorithm = new SyncFacade();
            algorithm.parser = parser;
            algorithm.webFetcher = webFetcher;
            algorithm.modelConverter = new WebToAppModelConverter();
            return algorithm;
        }
    }

    private String webAddressUrl;
    private WebResponseParser parser;
    private WebFetcher webFetcher;
    private WebToAppModelConverter modelConverter;

    /**
     * Synchronize saved list of currency exchange rates
     * @return List of refreshed rates
     * @throws IOException  is thrown if something went wrong
     */
    public List<Currency> performSync() throws IOException, IllegalArgumentException {
        String url = BuildConfig.EXCANGE_RATE_TABLE_URL;
        String webResponse;
        // attempt loading table from web
        webResponse = webFetcher.loadData(url);
        // parse response to web model
        CurrencyTable webModel = parser.fromApiResponse(webResponse);
        List<Currency> refreshedExchangeRates = modelConverter.webToAppModel(webModel);

        return refreshedExchangeRates;
    }

    private SyncFacade(){}



}
