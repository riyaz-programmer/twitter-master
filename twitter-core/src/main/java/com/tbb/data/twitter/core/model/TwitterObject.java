package com.tbb.data.twitter.core.model;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterObject {

    private static org.slf4j.Logger appLog = org.slf4j.LoggerFactory.getLogger("applog");

    private String appId;
    private String consumerKey;
    private String consumerSecret;

    public TwitterObject(String appId, String consumerKey, String consumerSecret) {
        this.appId = appId;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public Twitter getTwitter()  {
        OAuth2Token token = getOAuth2Token();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setJSONStoreEnabled(true);
        cb.setMBeanEnabled(true);
        cb.setOAuth2TokenType(token.getTokenType());
        cb.setOAuth2AccessToken(token.getAccessToken());
        return new TwitterFactory(cb.build()).getInstance();
    }

    private OAuth2Token getOAuth2Token() {
        int retryCount = 1, statusCode =0;
        OAuth2Token token = null;
        do{
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setApplicationOnlyAuthEnabled(true);
            cb.setJSONStoreEnabled(true);
            cb.setOAuthConsumerKey(consumerKey);
            cb.setOAuthConsumerSecret(consumerSecret);

            try {
                token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
            } catch (TwitterException e) {
                appLog.error("Exception is thrown while getting OAuth2Token. Message: "+e.toString());
                statusCode = e.getStatusCode();
            }
            if ((statusCode >=500 && statusCode <= 504) || (statusCode == -1)) {
                try {
                    long sleepTime = (1000*60)*retryCount;
                    appLog.error("Twitter has encountered some problem. Thread will sleep for "+sleepTime+" seconds.");
                    Thread.currentThread().sleep(sleepTime);
                    retryCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }while((retryCount > 1 && retryCount <=50) && (token == null));
        return token;
    }
}
