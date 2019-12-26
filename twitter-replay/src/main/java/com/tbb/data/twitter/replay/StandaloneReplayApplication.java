package com.tbb.data.twitter.replay;

import com.tbb.data.twitter.core.consumer.TwitterConsumer;
import com.tbb.data.twitter.core.consumer.TwitterIDConsumer;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import com.tbb.data.twitter.replay.handler.ReplayHandler;

public class StandaloneReplayApplication {

    public static void main(String[] args) {

        System.setProperty("stream.conf.file","/opt/replay/config/stream.yml");
        ApplicationUtil.createJobLocation();

        TwitterIDConsumer idConsumer = new TwitterIDConsumer();
        new Thread(idConsumer).start();

        TwitterConsumer consumer = new TwitterConsumer(null);
        new Thread(consumer).start();

        ReplayHandler replayHandler = new ReplayHandler();
        replayHandler.startReplayProducer();
    }
}
