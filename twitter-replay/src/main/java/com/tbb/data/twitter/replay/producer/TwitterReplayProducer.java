package com.tbb.data.twitter.replay.producer;

import com.tbb.data.twitter.core.model.Configuration;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import twitter4j.*;

import java.util.List;
import java.util.Map;

public class TwitterReplayProducer {

    private static org.slf4j.Logger replayLog = org.slf4j.LoggerFactory.getLogger("replaylog");

    private Twitter twitter;
    private RateLimitStatus searchTweetsRateLimit = null;
    private Configuration config = Configuration.getInstance();

    public TwitterReplayProducer(Twitter twitter) throws TwitterException {
        this.twitter = twitter;
        Map<String, RateLimitStatus> rateLimitStatus = this.twitter.getRateLimitStatus("search");
        searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
    }

    public long startRetrospectiveProducer(long sinceId, long maxId){
        replayLog.debug("Running Replay Retrospective Producer for Twitter Object: " +twitter.getConfiguration().getOAuthConsumerKey());
        if (searchTweetsRateLimit == null || searchTweetsRateLimit.getRemaining() == 0) {
            return maxId;
        }
        int remaining = searchTweetsRateLimit.getRemaining();
        for (int hits = 1; hits <= remaining; hits++) {
            Query query = ApplicationUtil.getQuery(sinceId, maxId);
            QueryResult queryResult;
            try {
                queryResult = twitter.search(query);
            } catch (TwitterException e) {
                replayLog.error("Exception is thrown while executing search. Returning Maxid:"+maxId);
                if (maxId != Long.MAX_VALUE)
                    ApplicationUtil.createJobFile(sinceId,maxId);
                return maxId;
            }
            List<Status> tweets = queryResult.getTweets();
            for (Status status : tweets) {
                if (status.isRetweet()) {
                    config.getIdQueue().add(status.getId() + " " + ApplicationUtil.formattedDate("MM/dd/yyyy HH:mm:ss", status.getCreatedAt(), "UTC") + " Replay Retweet");
                } else {
                    config.getIdQueue().add(status.getId() + " " + ApplicationUtil.formattedDate("MM/dd/yyyy HH:mm:ss", status.getCreatedAt(), "UTC") + " Replay Tweet");
                }
                config.getDataQueue().add(TwitterObjectFactory.getRawJSON(status));
                maxId = Math.min(maxId, status.getId());
            }
            maxId = maxId - 1;
            if (tweets.size() ==0 ) {
                replayLog.debug("No more tweets left for given sinceid and maxid. Lets us return back to calling method with maxid");
                return maxId;
            }
            searchTweetsRateLimit = queryResult.getRateLimitStatus();
            if (searchTweetsRateLimit != null) {
                remaining = searchTweetsRateLimit.getRemaining();
            } else {
                replayLog.debug("Creating job file with since id: "+sinceId+" max id: "+maxId);
                ApplicationUtil.createJobFile(sinceId,maxId);
                break;
            }
            if (remaining <= 0 && tweets.size() >0) {
                replayLog.debug("It seems more tweets are available. As Remaining is "+remaining+" but tweet count is " +tweets.size());
                replayLog.debug("Creating job file with since id: "+sinceId+" max id: "+maxId);
                ApplicationUtil.createJobFile(sinceId,maxId);
                break;
            }
        }
        replayLog.debug("Returning Max ID : " +maxId);
        return maxId;
    }
}
