package com.tbb.data.twitter.core.model;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.String.format;

public final class Configuration implements Serializable, Cloneable{

    private static org.slf4j.Logger applog = org.slf4j.LoggerFactory.getLogger("applog");

    private String version;
    private String released;
    private List<TwitterHandler> twitterHandlers;
    private String lang;
    private static Configuration configuration;

    private String jobINlocation;
    private String jobOUTlocation;

    private final BlockingQueue<String>  dataQueue = new LinkedBlockingQueue<>(IConstant.TWITTER_CONSUMER_SIZE);;
    private final BlockingQueue<String> idQueue = new LinkedBlockingQueue<>(IConstant.TWITTER_ID_CONSUMER_SIZE);

    private Configuration(){

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public List<TwitterHandler> getTwitterHandlers() {
        return twitterHandlers;
    }

    public void setTwitterHandlers(List<TwitterHandler> twitterHandlers) {
        this.twitterHandlers = twitterHandlers;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getJobINlocation() {
        return jobINlocation;
    }

    public void setJobINlocation(String jobINlocation) {
        this.jobINlocation = jobINlocation;
    }

    public String getJobOUTlocation() {
        return jobOUTlocation;
    }

    public void setJobOUTlocation(String jobOUTlocation) {
        this.jobOUTlocation = jobOUTlocation;
    }

    public BlockingQueue<String> getDataQueue() {
        return dataQueue;
    }

    public BlockingQueue<String> getIdQueue() {
        return idQueue;
    }

    public static Configuration getInstance(){

        if (configuration == null) {
            synchronized (Configuration.class) {
                if (configuration == null) {
                    Yaml yaml = new Yaml();
                    Path configPath = Paths.get(System.getProperty("stream.conf.file"));
                    applog.debug("Config Path: " +configPath.toString());
                    applog.debug("Is path Exist: " +configPath.toFile().exists());
                    applog.debug("File Path: " +configPath.toFile().getPath());

                    try (InputStream in = Files.newInputStream(configPath)) {
                        configuration = yaml.loadAs(in, Configuration.class);
                        applog.debug(configuration.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return configuration;
    }

    protected Object readResolve() {
        return getInstance();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString(){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append( format( "Version: %s\n", version ) )
                .append( format( "Released: %s\n", released ) )
                .append( format( "Number of Replay Handlers: %s\n", twitterHandlers.size() ))
                .append(format("Job Input Location: %s\n", jobINlocation))
                .append(format("Job Output Location: %s\n", jobOUTlocation));
        for (TwitterHandler twitterHandler : twitterHandlers) {
            strBuilder.append( format( "Replay App ID: %s\n", twitterHandler.getAppId() ) )
                    .append(format( "Replay consumer Key: %s\n", twitterHandler.getConsumerKey() ))
                    .append( format( "Replay consumer Secret: %s\n", twitterHandler.getConsumerSecret() ) );
        }
        return strBuilder.toString();
    }
}
