package com.example.alex.currencyconverter;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.WebToAppModelConverter;
import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.model.web.CurrencyTable;
import com.example.alex.currencyconverter.parsing.WebResponseParser;
import com.example.alex.currencyconverter.web.WebFetcher;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by Alex on 4/9/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class SyncFacadeTest {

    static Currency testValue;

    @Mock
    WebResponseParser webResponseParser;
    @Mock
    WebFetcher webFetcher;
    @Mock
    CurrencyDao dao;
    @Mock
    WebToAppModelConverter webToAppModelConverter;

    String webAddressUrl;
    SyncFacade facade;

    @BeforeClass
    public static void initTestValues(){
        testValue = new Currency();
        testValue.setCurrencyId("123");
        testValue.setCharCode("RUR");
        testValue.setName("Russian Roubles");
        testValue.setExchangeValueInRoubles(30);
    }

    /**
     * init default mocks
     * @throws IOException
     */
    @Before
    public void initMocks() throws IOException {
        // mock response parser
        when(webResponseParser.fromApiResponse(anyString()))
                .thenReturn(new CurrencyTable());
        when(webResponseParser.toApiResponse(any(CurrencyTable.class))).thenReturn("");
        // mock model converter
        when(webToAppModelConverter.webToAppModel(any(CurrencyTable.class)))
                .thenReturn(new ArrayList<Currency>());
        // mock web fetcher
        when(webFetcher.loadData(anyString())).thenReturn("");
        webAddressUrl = "123";
        // mock dao
        when(dao.getAllCurrencyRates()).thenReturn(new ArrayList<Currency>());
        when(dao.getCurrencyById(anyString())).thenReturn(new Currency());
        doNothing().when(dao).saveCurrency(any(Currency.class));
        doNothing().when(dao).saveCurrencies(anyList());
    }


    @Test(expected = RuntimeException.class)
    public void testExceptionIfResponseParserIsNull(){
        webResponseParser = null;
        createSyncFacade();
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionIfUrlIsNull(){
        webAddressUrl = null;
        createSyncFacade();
    }

    @Test(expected = IllegalArgumentException.class)
    public void ExceptionIfWebAddressUrlIsEmpty() throws IOException{
        webAddressUrl = "";
        createSyncFacade();
        facade.performSync();
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionIfParserNull(){
        webResponseParser = null;
        createSyncFacade();
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionIfDaoNull(){
        dao = null;
        createSyncFacade();
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionIfWebModelConverterIsNull(){
        webToAppModelConverter = null;
        createSyncFacade();
    }

    @Test
    public void testMockedObjectIsOk(){
        createSyncFacade();
        try {
            facade.performSync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = IOException.class)
    public void testExceptionIsThrownWhenWebFetcherThrowsException() throws IOException{
        webFetcher = Mockito.mock(WebFetcher.class);
        doThrow(new IOException("")).when(webFetcher).loadData(anyString());
        createSyncFacade();
        facade.performSync();
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        assertNotNull(webResponseParser);
    }

    private void createSyncFacade(){
        facade = new SyncFacade.Builder()
                .setParser(webResponseParser)
                .setWebAddressUrl(webAddressUrl)
                .setDao(dao)
                .setWebFetcher(webFetcher)
                .setModelConverter(webToAppModelConverter)
                .build();
    }
}
