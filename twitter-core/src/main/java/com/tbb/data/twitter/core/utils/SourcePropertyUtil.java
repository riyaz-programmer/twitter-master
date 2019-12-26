package com.tbb.data.twitter.core.utils;

import com.google.common.collect.Lists;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.util.List;

/**
 * Created by rshaikh3145 on 11/12/2017.
 */
public final class SourcePropertyUtil {

    public static synchronized twitter4j.JSONObject tranformSourceProperty(final JSONObject json) throws JSONException {
        List<String> jsonKeys = Lists.newArrayList(json.keys());
        for (String key : jsonKeys) {
            Object object = json.get(key);
            if (object instanceof twitter4j.JSONObject) {
                tranformSourceProperty((twitter4j.JSONObject) object);
            }else {
                if (key.equalsIgnoreCase("source")) {
                    String sourceValue = json.getString("source");
                    HTMLLinkElement linkElement = HTMLLinkExtraction.extractHTMLLinks(sourceValue);
                    String sourceText = linkElement.getLinkElement();
                    json.put("source", sourceText);
                }
            }
        }
        return json;
    }
}
