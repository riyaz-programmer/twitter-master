package com.tbb.data.twitter.search.handler;


import com.tbb.data.twitter.core.model.Configuration;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import com.tbb.data.twitter.search.producer.TwitterProducer;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SearchHandler implements Runnable{

    private static org.slf4j.Logger appLog = org.slf4j.LoggerFactory.getLogger("applog");

    private ConcurrentLinkedQueue<Twitter> twitterHandlers;

    public SearchHandler() {
        Configuration config = Configuration.getInstance();
        this.twitterHandlers = new ConcurrentLinkedQueue(ApplicationUtil.getHandlers(config.getTwitterHandlers()));
    }

    public void run() {
        long sinceId = 0; int retryCount = 1;
        while (!twitterHandlers.isEmpty()) {
            Twitter twitter = twitterHandlers.poll();
            try {
                Map<String, RateLimitStatus> rateLimitStatus  =  twitter.getRateLimitStatus();
                if(rateLimitStatus != null) {
                    RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
                    if (searchTweetsRateLimit != null){
                        int remaining = searchTweetsRateLimit.getRemaining();
                        if (remaining > 50) {
                            TwitterProducer twitterProducer = new TwitterProducer(twitter);
                            if (sinceId <= 0) {
                                sinceId = twitterProducer.startProducer();
                            }
//                            Thread.sleep(1000 * 5);
                            if (sinceId > 0) {
                                sinceId = twitterProducer.startRetrospectiveProducer(sinceId);
                            }
                        }
                     }
                }
            } catch (TwitterException e) {
                int statusCode = e.getStatusCode();
                if (e.exceededRateLimitation()) {
                    appLog.error("Rate Limit has been exceeded for Twitter Consumer Key: "+twitter.getConfiguration().getOAuthConsumerKey());
                } else  if ((statusCode >=500 && statusCode <= 504) || statusCode == -1) {
                    try {
                        if(sinceId > 0) {
                            ApplicationUtil.createJobFile(sinceId, Long.MAX_VALUE);
                        }
                        Thread.sleep((1000*60)*retryCount);
                        retryCount++;
                    } catch (InterruptedException ie) {
                       appLog.error(ie.getMessage());
                    }
                }else {
                    retryCount = 1;
                }
            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            twitterHandlers.add(twitter);
        }
    }
}
