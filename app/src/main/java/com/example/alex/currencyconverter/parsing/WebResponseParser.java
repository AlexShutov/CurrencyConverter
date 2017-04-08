package com.example.alex.currencyconverter.parsing;

import com.example.alex.currencyconverter.model.web.CurrencyTable;

import java.util.IllegalFormatException;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * App hides algorithm for parsing web response behind this interface.
 * Someday it may become JSON instead of XML or SimpleXML library will no longer
 * be available.
 */
public interface WebResponseParser {

    /**
     * Convert string response from web into Api model
     * @param response
     * @return
     * @throws IllegalFormatException web response may have illegal format
     */
    CurrencyTable fromApiResponse(String response) throws IllegalArgumentException;

    /**
     * Serialize table into String (is added for symmetry and testing custom algorithms).
     * This method does not throw any exceptions, because we assume that app model is
     * always correct and it is up to the parser to process it.
     * @param currencyTable
     * @return
     */
    String toApiResponse(CurrencyTable currencyTable);

}
