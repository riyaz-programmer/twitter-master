package com.tbb.data.twitter.core.utils;



import com.tbb.data.twitter.core.model.Configuration;
import com.tbb.data.twitter.core.model.TwitterHandler;
import com.tbb.data.twitter.core.model.TwitterObject;
import twitter4j.Query;
import twitter4j.Twitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ApplicationUtil {

    private static Configuration config = Configuration.getInstance();
    private static org.slf4j.Logger appLog = org.slf4j.LoggerFactory.getLogger("applog");
    private static List<Long> idTracker = new ArrayList<>();


    public static List<Twitter> getHandlers(List<TwitterHandler> twitterHandlers) {
        List<Twitter> handlers = new ArrayList<>();
        for (TwitterHandler twitterHandler: twitterHandlers) {
            String consumerKey = twitterHandler.getConsumerKey();
            String consumerSecret = twitterHandler.getConsumerSecret();
            String appId = twitterHandler.getAppId();
            Twitter twitter = new TwitterObject(appId, consumerKey, consumerSecret).getTwitter();
            handlers.add(twitter);
        }
        return handlers;
    }

    public static Query getQuery(long sinceId) {
        Query query = new Query(config.getLang());
        query.setCount(100);
        query.resultType(Query.RECENT);
        if (sinceId > 0) {
            query.sinceId(sinceId);
        }
        return query;
    }
    public static Query getQuery(long sinceId, long maxId) {
        Query query = getQuery(sinceId);
        if (maxId > 0 && maxId < Long.MAX_VALUE) {
            query.maxId(maxId);
        }
        return query;
    }

    public static synchronized String formattedDate(String pattern, Date date, String timeZone) {
        DateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
        return formatter.format(date);
    }

    public static void createJobLocation() {
        try {
            Files.createDirectories(Paths.get(config.getJobINlocation()));
            Files.createDirectories(Paths.get(config.getJobOUTlocation()));
        } catch (IOException e) {
            appLog.error("Error in creating job directory. Service will exit now. Fix the problem and rerun the service.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public synchronized static void createJobFile(long sinceId, long maxId) {
        StringBuilder jobFileName = new StringBuilder(sinceId+"");
        if (maxId > 0) {
            jobFileName.append("_");
            jobFileName.append(maxId);
        }
        jobFileName.append(".job");
        String fileLocation = config.getJobINlocation()+ File.separator+jobFileName;
        try {
            Files.createFile(Paths.get(fileLocation));
        } catch (FileAlreadyExistsException x) {
            appLog.error("file named " +fileLocation+" already exist");
        } catch (IOException e) {
            appLog.error("Error in creating job file:"+ jobFileName+ " at location: "+config.getJobINlocation()+". Exception: "+e.getMessage());
        }
    }

}
