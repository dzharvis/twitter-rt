package com.dzharvis;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingDeque;


@Component
public class TwitterDaemon implements Runnable {

    public static final String CONSUMER_KEY = "OA8ufcmmu5LYVAFP8NstCQ";
    public static final String CONSUMER_SECRET = "DpkyRzEBvIPzDrYsk2KDF9SnRCt1c5PLn71R6lekU";

    @Value("${token}")
    private String token;

    @Value("${tokenSecret}")
    private String tokenSecret;

    private TwitterStream twitterStream;

    private BlockingDeque<Status> queue;

    @PostConstruct
    public void init() {

        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                if (status.getGeoLocation() == null) return;
                if (queue.remainingCapacity() == 0) {
                    queue.pollLast();
                }
                queue.add(status);
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onScrubGeo(long arg0, long arg1) {
            }

            @Override
            public void onStallWarning(StallWarning arg0) {
            }
        };

        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        twitterStream.setOAuthAccessToken(new AccessToken(token, tokenSecret));
        twitterStream.addListener(listener);

    }

    @Override
    public void run() {
        twitterStream.sample();
    }

    public void setQueue(BlockingDeque<Status> queue) {
        this.queue = queue;
    }

    public TwitterStream getTwitterStream() {
        return twitterStream;
    }

}
