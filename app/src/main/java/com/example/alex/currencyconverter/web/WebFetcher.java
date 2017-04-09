package com.example.alex.currencyconverter.web;

import java.io.IOException;

/**
 * Created by Alex on 4/9/2017.
 */

public interface WebFetcher {
    String loadData(String url) throws IOException;
}
