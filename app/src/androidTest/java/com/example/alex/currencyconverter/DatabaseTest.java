package com.example.alex.currencyconverter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.publicsuffix.PublicSuffixPatterns;
import android.support.test.runner.AndroidJUnit4;

import com.example.alex.currencyconverter.dao.CurrencyDao;
import com.example.alex.currencyconverter.model.app.Currency;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Created by Alex on 4/9/2017.
 */

@RunWith(AndroidJUnit4.class)
public abstract class DatabaseTest {

    private CurrencyDao dao;

    public abstract CurrencyDao createDao(Context context);

    private static Currency testRecord;
    private static List<Currency> testRecords;

    @BeforeClass
    public static void initTestValues(){
        testRecord = new Currency();
        testRecord.setCurrencyId(String.valueOf(1));
        testRecord.setCharCode("USD");
        testRecord.setName("US Dollar");
        testRecord.setExchangeValueInRoubles(30);

        testRecords = new ArrayList<>();
        int N = 20;
        for (int  i = 0; i < N; ++i){
            Currency c = new Currency(testRecord);
            c.setCurrencyId(c.getCurrencyId() + String.valueOf(i + 1));
            testRecords.add(c);
        }
    }

    @Before
    public void dropDatabaseBeforeEveryTest(){
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        dao = createDao(appContext);
        dao.dropTable();
    }

    @Test
    public void testQueryngEmptyDatabase(){
        List<Currency> currencies = dao.getAllCurrencyRates();
        assertNotNull(currencies);
        assertTrue(currencies.isEmpty());
    }

    @Test
    public void testSavingObjectToDatabase(){
        dao.saveCurrency(testRecord);
        List<Currency> currencies = dao.getAllCurrencyRates();
        assertNotNull(currencies);
        assertEquals(1, currencies.size());
    }

    @Test
    public void testIfSerializationToAndFromCursorIsCorrect(){
        dao.saveCurrency(testRecord);
        Currency c = dao.getAllCurrencyRates().get(0);
        assertNotNull(c);
        assertEquals(testRecord.getCurrencyId(), c.getCurrencyId());
        assertEquals(testRecord.getCharCode(), c.getCharCode());
        assertEquals(testRecord.getExchangeValueInRoubles(), c.getExchangeValueInRoubles(), 1e-6);
        assertEquals(testRecord.getName(), c.getName());
    }

    /**
     * Save the same instance many times and make sure only one value is being stored
     * in database
     */
    @Test
    public void testCurrenciesHasToHaveUniqueIds(){
        dao.saveCurrency(testRecord);
        dao.saveCurrency(testRecord);
        dao.saveCurrency(testRecord);
        List<Currency> currencies = dao.getAllCurrencyRates();
        assertNotNull(currencies);
        assertFalse(currencies.isEmpty());
        assertEquals(1, currencies.size());
    }

    @Test
    public void testCurrenciesWithDifferentIdsAreSaved(){
        for (Currency c : testRecords){
            dao.saveCurrency(c);
        }
        List<Currency> currencies = dao.getAllCurrencyRates();
        assertNotNull(currencies);
        assertFalse(currencies.isEmpty());
        checkAllCurrenciesTheSampe(testRecords, currencies);
    }

    @Test
    public void testBulkInsert(){
        dao.saveCurrencies(testRecords);
        List<Currency> loadedRecords = dao.getAllCurrencyRates();
        checkAllCurrenciesTheSampe(testRecords, loadedRecords);
    }

    @Test
    public void testQueryingObject(){
        dao.saveCurrencies(testRecords);
        for (Currency currency : testRecords){
            Currency dbItem = dao.getCurrencyById(currency.getCurrencyId());
            assertNotNull(dbItem);
            assertEquals(currency.getCurrencyId(), dbItem.getCurrencyId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotQueryItemByNullId(){
        dao.saveCurrencies(testRecords);
        Currency currency = dao.getCurrencyById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannoQueryItemWithEmptyId(){
        dao.saveCurrencies(testRecords);
        Currency c = dao.getCurrencyById("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotQueryNonExistingItem(){
        Currency c = new Currency(testRecord);
        c.setCurrencyId("1");
        dao.saveCurrency(c);
        Currency c2 = dao.getCurrencyById("2");
    }

    /**
     * Receives two arrays of Currency objects and verifies that symmetric difference
     * of sets of ID elements is empty.
     * @param source
     * @param received
     */
    private void checkAllCurrenciesTheSampe(List<Currency> source, List<Currency> received) {
        assertEquals(source.size(), received.size());
        Set<String> sourceIds = new HashSet<>();
        Set<String> receivedIds = new HashSet<>();
        for (int i = 0; i < source.size(); ++i){
            sourceIds.add(source.get(i).getCurrencyId());
            receivedIds.add(received.get(i).getCurrencyId());
        }
        assertTrue(sourceIds.containsAll(receivedIds));
        assertTrue(receivedIds.containsAll(sourceIds));
    }
}
