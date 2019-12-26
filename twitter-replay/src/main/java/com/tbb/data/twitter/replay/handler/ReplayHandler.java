package com.tbb.data.twitter.replay.handler;

import com.tbb.data.twitter.core.model.Configuration;
import com.tbb.data.twitter.core.utils.ApplicationUtil;
import com.tbb.data.twitter.replay.producer.TwitterReplayProducer;
import org.apache.commons.io.FilenameUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReplayHandler {

    private static org.slf4j.Logger replayLog = org.slf4j.LoggerFactory.getLogger("replaylog");

    private final Configuration config = Configuration.getInstance();
    private ConcurrentLinkedQueue<Twitter> replayHandlers;

    public ReplayHandler() {
        List<Twitter> replayHandlerList= ApplicationUtil.getHandlers(config.getTwitterHandlers());
        this.replayHandlers = new ConcurrentLinkedQueue(replayHandlerList);
    }

    public void startReplayProducer()  {
        Path jobINLocation = Paths.get(config.getJobINlocation());
        Path jobOUTLocation = Paths.get(config.getJobOUTlocation());
        int retryCount = 1; long sinceId = 0, maxId = 0;
        while(true) {
            try (DirectoryStream<Path> jobFiles = Files.newDirectoryStream(jobINLocation, "*.job")) {
                for (Path jobFile : jobFiles) {
                    Twitter twitter = replayHandlers.poll();
                    try {
                        replayLog.debug("Processing file name: " + jobFile.getFileName());
                        String fName = FilenameUtils.getBaseName(jobFile.getFileName().toString());
                        sinceId = Long.valueOf(fName.split("_")[0]);
                        maxId = Long.valueOf(fName.split("_")[1]);
                        TwitterReplayProducer twitterReplayProducer = new TwitterReplayProducer(twitter);
                        long returnMaxId = twitterReplayProducer.startRetrospectiveProducer(sinceId, maxId);
                        if (returnMaxId != maxId) {
                            replayLog.debug("Moving file: " + jobFile + " to: " + Paths.get(config.getJobOUTlocation()));
                            Files.move(jobFile, jobOUTLocation.resolve(jobFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (TwitterException e) {
                        int statusCode = e.getStatusCode();
                        if (e.exceededRateLimitation()) {
                            replayLog.error("Rate Limit has been exceeded for Twitter Consumer Key: " + twitter.getConfiguration().getOAuthConsumerKey());
                        } else if ((statusCode >= 500 && statusCode <= 504) || statusCode == -1) {
                            try {
                                if (sinceId > 0) {
                                    ApplicationUtil.createJobFile(sinceId, Long.MAX_VALUE);
                                }
                                Thread.currentThread().sleep((1000 * 30) * retryCount);
                                retryCount++;
                            } catch (InterruptedException ie) {
                                replayLog.error(ie.getMessage());
                            }
                        } else {
                            retryCount = 1;
                        }
                    }
                    replayHandlers.add(twitter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
