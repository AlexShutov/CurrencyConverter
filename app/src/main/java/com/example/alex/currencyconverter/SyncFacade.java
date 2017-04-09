package com.example.alex.currencyconverter;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.WebToAppModelConverter;
import com.example.alex.currencyconverter.model.WebToAppModelConverterImpl;
import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.model.web.CurrencyTable;
import com.example.alex.currencyconverter.parsing.WebResponseParser;
import com.example.alex.currencyconverter.web.WebFetcher;

import java.io.IOException;
import java.util.List;

/**
 * Created by Alex on 4/8/2017.
 */

public class SyncFacade {

    public static class Builder {
        // web-related entities
        private WebResponseParser parser;
        private WebFetcher webFetcherImpl;
        private WebToAppModelConverter modelConverter;
        private String webAddressUrl;
        // database
        private CurrencyDao dao;

        public Builder setParser(WebResponseParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder setWebFetcher(WebFetcher webFetcherImpl) {
            this.webFetcherImpl = webFetcherImpl;
            return this;
        }

        public Builder setWebAddressUrl(String webAddressUrl) {
            this.webAddressUrl = webAddressUrl;
            return this;
        }

        public Builder setDao(CurrencyDao dao) {
            this.dao = dao;
            return this;
        }

        public Builder setModelConverter(WebToAppModelConverter converter){
            this.modelConverter = converter;
            return this;
        }

        public SyncFacade build() throws RuntimeException {
            if (null == parser || null == webFetcherImpl || null == webAddressUrl ||
                    null == dao || null == modelConverter){
                throw new RuntimeException("Sync algorithm builder has not initialized " +
                        "fields");
            }

            SyncFacade algorithm = new SyncFacade();
            algorithm.parser = parser;
            algorithm.webFetcherImpl = webFetcherImpl;
            algorithm.webAddressUrl = webAddressUrl;
            algorithm.dao = dao;
            algorithm.modelConverter = modelConverter;
            return algorithm;
        }
    }

    // web-related entities
    private String webAddressUrl;
    private WebResponseParser parser;
    private WebFetcher webFetcherImpl;
    private WebToAppModelConverter modelConverter;
    // database
    private CurrencyDao dao;

    /**
     * Synchronize saved list of currency exchange rates
     * @return List of refreshed rates
     * @throws IOException  is thrown if something went wrong
     */
    public List<Currency> performSync() throws IOException, IllegalArgumentException {
        String webResponse;
        if (webAddressUrl.isEmpty()){
            throw new IllegalArgumentException("Response URL cannot be empty");
        }
        // attempt loading table from web
        webResponse = webFetcherImpl.loadData(webAddressUrl);
        // parse response to web model
        CurrencyTable webModel = parser.fromApiResponse(webResponse);
        List<Currency> refreshedExchangeRates = modelConverter.webToAppModel(webModel);
        // update database
        dao.dropTable();
        dao.saveCurrencies(refreshedExchangeRates);
        return refreshedExchangeRates;
    }

    private SyncFacade(){}



}
