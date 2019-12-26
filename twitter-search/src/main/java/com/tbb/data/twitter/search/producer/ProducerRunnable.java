package com.tbb.data.twitter.search.producer;

import com.tbb.data.twitter.core.model.Configuration;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import twitter4j.*;

import java.util.List;

class ProducerRunnable implements Runnable{

    private static org.slf4j.Logger appLog = org.slf4j.LoggerFactory.getLogger("applog");
    private Twitter twitter;
    private long sinceId;
    private long maxId;
    private final Configuration config = Configuration.getInstance();

    ProducerRunnable(final Twitter twitter, final long sinceId, final long maxId) {
        this.twitter = twitter;
        this.sinceId = sinceId;
        this.maxId = maxId;
    }

    @Override
    public void run() {
        QueryResult queryResult;
        RateLimitStatus searchTweetsRateLimit;
        int remaining, tweetCount = 0;
        List<Status> statuses;
        boolean hasNext = false;
        do {
            maxId = maxId - 1;
            Query query = ApplicationUtil.getQuery(sinceId, maxId);
            try {
                queryResult = twitter.search(query);
            }catch (TwitterException e) {
                appLog.error(e.getErrorMessage()+". "+e.getMessage());
                appLog.debug("Creating job file with since id: "+sinceId+" max id: "+maxId);
                ApplicationUtil.createJobFile(sinceId,maxId);
                break;
            }
            statuses = queryResult.getTweets();
            tweetCount = tweetCount + statuses.size();
            for (Status status : statuses) {
                    String tweet = TwitterObjectFactory.getRawJSON(status);
                    if (status.isRetweet()) {
                        config.getIdQueue().add(status.getId() + " " + ApplicationUtil.formattedDate("MM/dd/yyyy HH:mm:ss", status.getCreatedAt(), "UTC") + " Search Retweet " +twitter.getConfiguration().getOAuthConsumerKey() );
                    } else {
                        config.getIdQueue().add(status.getId() + " " + ApplicationUtil.formattedDate("MM/dd/yyyy HH:mm:ss", status.getCreatedAt(), "UTC") + " Search TweetDirect " +twitter.getConfiguration().getOAuthConsumerKey());
                    }
                    config.getDataQueue().add(tweet);
                    maxId = Math.min(maxId, status.getId());
            }
            searchTweetsRateLimit = queryResult.getRateLimitStatus();
            if (searchTweetsRateLimit != null) {
                remaining = searchTweetsRateLimit.getRemaining();
            } else {
                appLog.debug("Creating job file with since id: "+sinceId+" max id: "+maxId);
                ApplicationUtil.createJobFile(sinceId,maxId);
                break;
            }
        }while(remaining > 0 && statuses.size() > 0);
        appLog.debug("Total Tweets downloaded by Thread: " +tweetCount);
        appLog.info("Ending Producer Runnable Twitter Object: " +twitter.getConfiguration().getOAuthConsumerKey());
    }
}
