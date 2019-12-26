package com.tbb.data.twitter.replay;

import com.tbb.data.twitter.core.consumer.TwitterConsumer;
import com.tbb.data.twitter.replay.handler.ReplayHandler;
import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.source.AbstractSource;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterReplaySource extends AbstractSource implements EventDrivenSource, Configurable {

    @Override
    public void configure(Context context) {

    }

    @Override
    public void start() {
        final ChannelProcessor channel = getChannelProcessor();
        TwitterConsumer twitterConsumer = new TwitterConsumer(channel);
        new Thread(twitterConsumer).start();

        ReplayHandler replayHandler = new ReplayHandler();
        replayHandler.startReplayProducer();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }
}
