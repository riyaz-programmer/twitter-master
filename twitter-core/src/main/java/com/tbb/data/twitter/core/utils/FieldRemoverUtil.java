package com.tbb.data.twitter.core.utils;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by rshaikh3145 on 11/12/2017.
 */
public final class FieldRemoverUtil {

    private static Set<String> getRemovableProperties() {
        Set<String> items = new TreeSet<>();
        items.add("contributors");
        items.add("truncated");
        items.add("timestamp_ms");
        items.add("listed_count");
        items.add("profile_background_image_url");
        items.add("default_profile_image");
        items.add("is_translator");
        items.add("profile_background_image_url_https");
        items.add("protected");
        items.add("profile_link_color");
        items.add("id");
        items.add("profile_background_color");
        items.add("profile_sidebar_border_color");
        items.add("profile_text_color");
        items.add("verified");
        items.add("contributors_enabled");
        items.add("profile_background_tile");
        items.add("profile_banner_url");
        items.add("follow_request_sent");
        items.add("default_profile");
        items.add("following");
        items.add("profile_sidebar_fill_color");
        items.add("notifications");
        items.add("profile_use_background_image");
        items.add("indices");
        items.add("sizes");
        items.add("source_status_id");
        items.add("in_reply_to_status_id");
        items.add("in_reply_to_user_id");
        items.add("filter_level");
        return items;
    }

    public static synchronized JSONObject removeProperties(final JSONObject json) throws JSONException {
        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            if (getRemovableProperties().contains(key)) {
                json.remove(key);
                removeProperties(json);
                break;
            } else {
                Object obj = json.get(key);
                if (obj instanceof JSONObject) {
                    removeProperties((JSONObject)obj);
                }if (obj instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray)obj;
                    for (int index=0; index<jsonArray.length(); index++) {
                        if (jsonArray.get(index) instanceof JSONObject ) {
                            removeProperties((JSONObject)jsonArray.get(index));
                        }
                    }
                }
            }
        }
        return json;
    }
}
