package com.tbb.data.twitter.core.utils;

import twitter4j.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by rshaikh3145 on 11/12/2017.
 */
public final class URLConverterUtil {

    public static String expandUrl(String shortenedUrl) throws IOException {
        URL url = new URL(shortenedUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        httpURLConnection.setInstanceFollowRedirects(false);
        String expandedURL = httpURLConnection.getHeaderField("Location");
        httpURLConnection.disconnect();
        return expandedURL;
    }

    public static JSONObject originalURL(JSONObject jsonObject) {

        return null;
    }
}
