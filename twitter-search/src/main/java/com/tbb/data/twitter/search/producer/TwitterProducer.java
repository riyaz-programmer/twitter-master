package com.tbb.data.twitter.search.producer;


import com.tbb.data.twitter.core.model.Configuration;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import twitter4j.*;

import java.util.List;
import java.util.Map;

public class TwitterProducer {

    private static org.slf4j.Logger appLog = org.slf4j.LoggerFactory.getLogger("applog");
    private Twitter twitter;
    private RateLimitStatus searchTweetsRateLimit = null;
    private final Configuration config = Configuration.getInstance();

    public TwitterProducer(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        Map<String, RateLimitStatus> rateLimitStatus = this.twitter.getRateLimitStatus("search");
        searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

    }

   public synchronized long startProducer() throws TwitterException {
        long sinceId = 0L;
        if (searchTweetsRateLimit == null || searchTweetsRateLimit.getRemaining() == 0) {
           throw new TwitterException("Search Rate Limit is null. It seems rate limit is over");
        }
        Query query = ApplicationUtil.getQuery(sinceId);
        QueryResult queryResult = twitter.search(query);
        List<Status> statuses = queryResult.getTweets();
        int tweetCount = statuses.size();
        for (Status status : statuses) {
                processPost(status);
                config.getDataQueue().add(TwitterObjectFactory.getRawJSON(status));
                sinceId = Math.max(sinceId, status.getId());
        }
        appLog.debug("First Request # Tweets downloaded: "+tweetCount);
        searchTweetsRateLimit = queryResult.getRateLimitStatus();
        return sinceId;
    }

    public long startRetrospectiveProducer(long sinceId) throws TwitterException {
        long maxId = Long.MAX_VALUE; long sinceIdForNextBatch = 0;
        appLog.debug("Running Retrospective Producer for Twitter Object: " +twitter.getConfiguration().getOAuthConsumerKey());
        if (searchTweetsRateLimit == null || searchTweetsRateLimit.getRemaining() == 0) {
            throw new TwitterException("Search Rate Limit is null. It seems rate limit is over");
        }
        Query query = ApplicationUtil.getQuery(sinceId);
        QueryResult queryResult = twitter.search(query);
        List<Status> statuses = queryResult.getTweets();
        int tweetCount = statuses.size();
        for (Status status : statuses) {
            processPost(status);
            config.getDataQueue().add(TwitterObjectFactory.getRawJSON(status));
            sinceIdForNextBatch = Math.max(sinceIdForNextBatch, status.getId());
            maxId = Math.min(maxId, status.getId());
        }
//        searchTweetsRateLimit = queryResult.getRateLimitStatus();
        appLog.debug("Second Request # Tweets downloaded: "+tweetCount);
        if (sinceId != 0 && (sinceId < maxId) && (maxId < Long.MAX_VALUE)) {
            new Thread(new ProducerRunnable(twitter, sinceId, maxId)).start();
        }
        return sinceIdForNextBatch;
    }

    private synchronized void processPost(Status status) {
        if (status.isRetweet()) {
            config.getIdQueue().add(status.getId() + " " + ApplicationUtil.formattedDate("MM/dd/yyyy HH:mm:ss", status.getCreatedAt(), "UTC") + " Search Retweet " +twitter.getConfiguration().getOAuthConsumerKey());
        } else {
            config.getIdQueue().add(status.getId() + " " + ApplicationUtil.formattedDate("MM/dd/yyyy HH:mm:ss", status.getCreatedAt(), "UTC") + " Search TweetDirect " +twitter.getConfiguration().getOAuthConsumerKey());
        }
    }
}
