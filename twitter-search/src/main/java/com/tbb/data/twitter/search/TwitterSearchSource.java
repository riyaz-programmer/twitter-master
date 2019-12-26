package com.tbb.data.twitter.search;

import com.tbb.data.twitter.core.consumer.TwitterConsumer;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import com.tbb.data.twitter.search.handler.SearchHandler;
import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.source.AbstractSource;

public class TwitterSearchSource extends AbstractSource implements EventDrivenSource, Configurable {

    @Override
    public void configure(Context context) {
        ApplicationUtil.createJobLocation();
    }

    @Override
    public void start() {
        final ChannelProcessor channel = getChannelProcessor();
        TwitterConsumer consumer = new TwitterConsumer(channel);
        new Thread(consumer).start();
        SearchHandler  searchHandler = new SearchHandler();
        new Thread(searchHandler).start();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }
}
