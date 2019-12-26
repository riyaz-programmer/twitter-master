package com.tbb.data.twitter.core.consumer;

import com.tbb.data.twitter.core.model.Configuration;
import org.apache.flume.Event;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.event.EventBuilder;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

public class TwitterConsumer implements Runnable {

    private static org.slf4j.Logger tweetLog = LoggerFactory.getLogger("tweet");
    private static org.slf4j.Logger idLog = LoggerFactory.getLogger("idlog");

    private Configuration config = Configuration.getInstance();

    private BlockingQueue<String> queue;
    private ChannelProcessor channel;

    public TwitterConsumer(ChannelProcessor channel) {
        this.queue = config.getDataQueue();
        this.channel = channel;
    }

    @Override
    public void run() {
        while (true) {
            if(!queue.isEmpty()) {
                try {
                    String tweet = queue.take();
                    if (tweet != null) {
                        if (this.channel != null) {
                            Event event = EventBuilder.withBody(tweet, Charset.forName("UTF-8"));
                            channel.processEvent(event);
                        } else {
//                            try {
//                                Status status = TwitterObjectFactory.createStatus(tweet);
//                                tweetLog.debug(status.getText());
//                                String rawTweet = TwitterObjectFactory.getRawJSON(status);
                                tweetLog.debug(tweet);
//                                tweetLog.debug(status.getText());
//                            } catch (TwitterException e) {
//                                e.printStackTrace();
//                            }

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
