package com.tbb.data.twitter.search;


import com.tbb.data.twitter.core.consumer.TwitterConsumer;
import com.tbb.data.twitter.core.consumer.TwitterIDConsumer;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import com.tbb.data.twitter.search.handler.SearchHandler;


public class StandaloneSearchApplication {


    public static void main(String[] args) {

        System.setProperty("stream.conf.file","/opt/search/config/stream.yml");
        ApplicationUtil.createJobLocation();
        // Consumer to push data to sink

        TwitterIDConsumer idConsumer = new TwitterIDConsumer();
        new Thread(idConsumer).start();

        TwitterConsumer consumer = new TwitterConsumer(null);
        new Thread(consumer).start();

        SearchHandler searchHandler = new SearchHandler();
        new Thread(searchHandler).start();
    }
}
