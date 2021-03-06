package com.tbb.data.twitter.core.interceptors;

import com.tbb.data.twitter.core.utils.UserURLExpanderUtil;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by rshaikh3145 on 11/13/2017.
 */
public class UserURLExpanderInterceptor implements Interceptor {
    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        byte[] eventBody = event.getBody();
        try {
            JSONObject jsonObject = new JSONObject(new String(eventBody, "UTF-8"));
//            jsonObject = UserURLExpanderUtil.expendedURL(jsonObject);
            event.setBody(jsonObject.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        events.stream().forEach(
                event -> intercept(event)
        );
        return events;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder
    {
        @Override
        public void configure(Context context) {
            // TODO Auto-generated method stub
        }

        @Override
        public Interceptor build() {
            return new UserURLExpanderInterceptor();
        }
    }
}
