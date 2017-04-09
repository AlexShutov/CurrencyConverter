package com.example.alex.currencyconverter;

import com.example.alex.currencyconverter.model.WebToAppModelConverter;
import com.example.alex.currencyconverter.model.WebToAppModelConverterImpl;
import com.example.alex.currencyconverter.model.app.Currency;
import com.example.alex.currencyconverter.model.web.CurrencyTable;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alex on 4/9/2017.
 */

/**
 * Tests web-app model converter.
 */
public class ModelConverterTest {

    private WebToAppModelConverter converter;
    // test currency table in format of web model
    private CurrencyTable table;

    @Before
    public void initConverter(){
        converter = new WebToAppModelConverterImpl();
        table = new CurrencyTable();
        com.example.alex.currencyconverter.model.web.Currency c =
                new com.example.alex.currencyconverter.model.web.Currency();
        c.setName("Roubles");
        c.setCharCode("123");
        c.setExcangeValueInRoubles("123");
        c.setNominal(123);
        c.setNumberCode(123);
        table.getCurrencyRecords().add(c);
    }

    @Test
    public void testNullValue(){
        CurrencyTable table = new CurrencyTable();
        List<Currency> currencies = converter.webToAppModel(table);
        assertNotNull(currencies);
        assertTrue(currencies.isEmpty());
    }

    @Test
    public void testEmptyName(){
        com.example.alex.currencyconverter.model.web.Currency c =
                table.getCurrencyRecords().get(0);
        c.setName(null);
        c.setCharCode(null);
        List<Currency> currencies = converter.webToAppModel(table);
        assertEquals(1, currencies.size());
        assertNull(currencies.get(0).getName());
        assertNull(currencies.get(0).getCharCode());
    }

    @Test(expected = NullPointerException.class)
    public void testExchangeRateCannotBeNull(){
        com.example.alex.currencyconverter.model.web.Currency c =
                table.getCurrencyRecords().get(0);
        c.setExcangeValueInRoubles(null);
        List<Currency> currencies = converter.webToAppModel(table);
    }

    @Test
    public void testCurrencyRateCanBeWrittenWithDot(){
        com.example.alex.currencyconverter.model.web.Currency c =
                table.getCurrencyRecords().get(0);
        double rate = 9.999;
        c.setExcangeValueInRoubles(String.valueOf(rate));
        List<Currency> currencies = converter.webToAppModel(table);
        double parsedRate =  currencies.get(0).getExchangeValueInRoubles();
        assertEquals(rate, parsedRate, 1e-6);
    }

}
