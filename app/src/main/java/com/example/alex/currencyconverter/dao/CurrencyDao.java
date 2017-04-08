package com.example.alex.currencyconverter.dao;

/**
 * Created by Alex on 4/8/2017.
 */

import com.example.alex.currencyconverter.model.app.Currency;

import java.util.List;

/**
 * Dao for accessing currency exchange rate table.
 */
public interface CurrencyDao {
    /**
     * Remove all records from database
     */
    void dropTable();

    /**
     * Return all currencies stored in database sorted in ascending order by name
     * @return
     */
    List<Currency> getAllCurrencyRates();

    /**
     * Get currency by its id.
     * @param id String value of numberId, coming from server.
     * @return
     * @throws IllegalArgumentException is thrown if database has no record under that id.
     */
    Currency getCurrencyById(String id) throws IllegalArgumentException;

    /**
     * Save currency to database
     * @param currency
     */
    void saveCurrency(Currency currency);

    /**
     * Bulk insert method.
     * @param currencies
     */
    void saveCurrencies(List<Currency> currencies);


}
