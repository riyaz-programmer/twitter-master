package com.tbb.data.twitter.core.utils;

import com.google.common.collect.Lists;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.util.List;

/**
 * Created by rshaikh3145 on 11/13/2017.
 */
public class UserURLExpanderUtil {


    public static twitter4j.JSONObject expandedURL(final twitter4j.JSONObject json) throws JSONException {
        List<String> jsonKeys = Lists.newArrayList(json.keys());
        for (String key : jsonKeys) {
            Object object = json.get(key);
            if (object instanceof twitter4j.JSONObject) {
                if (key.equalsIgnoreCase("user")) {
                    twitter4j.JSONObject userObject = (twitter4j.JSONObject) object;
                    if (userObject.has("entities")) {
                        twitter4j.JSONObject entitiesObj = userObject.getJSONObject("entities");
                        if (entitiesObj.has("url")) {
                            twitter4j.JSONObject urlObj = entitiesObj.getJSONObject("url");
                            if (urlObj.has("urls")) {
                                JSONArray urlsArray = urlObj.getJSONArray("urls");
                                for (int i =0; i < urlsArray.length(); i++) {
                                    twitter4j.JSONObject urlsObj = urlsArray.getJSONObject(i);
                                    if (urlsObj != null) {
                                        userObject.append("expanded_url", urlsObj.getString("expanded_url"));
                                    }
                                }
                            }
                        }  if (entitiesObj.has("description")) {
                            twitter4j.JSONObject descriptionObj = entitiesObj.getJSONObject("description");
                            if (descriptionObj.has("urls")) {
                                JSONArray urlsArray = descriptionObj.getJSONArray("urls");
                                for (int i =0; i < urlsArray.length(); i++) {
                                    twitter4j.JSONObject urlsObj = urlsArray.getJSONObject(i);
                                    if (urlsObj != null) {
                                        userObject.append("description_expanded_url", urlsObj.getString("expanded_url"));
                                    }
                                }
                            }
                        }
                        userObject.remove("entities");
                    }
                    json.put("user", userObject);
                } else {
                    expandedURL((twitter4j.JSONObject) object);
                }
            }
        }
        return json;
    }
}
