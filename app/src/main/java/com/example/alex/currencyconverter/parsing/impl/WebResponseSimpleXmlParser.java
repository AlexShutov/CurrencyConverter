package com.example.alex.currencyconverter.parsing.impl;

import com.example.alex.currencyconverter.model.web.CurrencyTable;
import com.example.alex.currencyconverter.parsing.WebResponseParser;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by Alex on 4/8/2017.
 */

/**
 * Uses SimpleXml library for serializing api model and parsing web response in
 * xml format.
 */
public class WebResponseSimpleXmlParser implements WebResponseParser {

    @Override
    public CurrencyTable fromApiResponse(String xmlResponse) throws IllegalArgumentException {
        Reader reader = new StringReader(xmlResponse);
        Persister serialzer = new Persister();
        CurrencyTable table;
        try {
            table = serialzer.read(CurrencyTable.class, reader, false);
        } catch (Exception e) {
            table = null;
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return  table;
    }

    @Override
    public String toApiResponse(CurrencyTable currencyTable) {
        Writer writer = new StringWriter();
        Serializer serializer = new Persister();
        try {
            serializer.write(currencyTable, writer);
            String xml = writer.toString();
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
