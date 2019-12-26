package com.tbb.data.twitter.core.consumer;

import com.tbb.data.twitter.core.model.Configuration;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class TwitterIDConsumer implements Runnable {

    private static org.slf4j.Logger idLog = LoggerFactory.getLogger("idlog");

    private Configuration config = Configuration.getInstance();

    private BlockingQueue<String> idQueue;

    public TwitterIDConsumer() {
        this.idQueue = config.getIdQueue();
    }
        @Override
        public void run() {
            while (true) {
                if(!idQueue.isEmpty()) {
                    try {
                        String idLogMsg = idQueue.take();
                        if (idLogMsg != null) {
                            idLog.debug(idLogMsg);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
