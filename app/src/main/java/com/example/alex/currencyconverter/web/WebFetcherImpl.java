package com.example.alex.currencyconverter.web;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Loads some data from given URL
 */

public class WebFetcherImpl implements WebFetcher {

    private static final String LOG_TAG = WebFetcherImpl.class.getSimpleName();

    /**
     * Attempt to load data from given Url and return result as a plain string.
     * @param url
     * @return
     * @throws IOException In this app we need to know no details about network error.
     *                      IOException carry enough information to understand that
     *                      update failed.
     */
    @Override
    public String loadData(String url) throws IOException{
        int responseCode;
        String response = null;
        try {
            URL namesListUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) namesListUrl
                    .openConnection();
            connection.connect();
            responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader streamReader = new BufferedReader(
                        new InputStreamReader(inputStream, "CP1251"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                response = responseStrBuilder.toString();
            } else {
                Log.i(LOG_TAG, "Failed HttpResponseCode: " + responseCode);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "URL Exception: ", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "HttpURLConnection Exception: ", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "General Exception: ", e);
        }
        if (null == response){
            throw new IOException("error loading data ");
        }
        return response;
    }
}
