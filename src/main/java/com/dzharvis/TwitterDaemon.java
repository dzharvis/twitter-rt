package com.dzharvis;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingDeque;


@Component
public class TwitterDaemon implements Runnable {
    @Value("${CONSUMER_KEY}")
    private String CONSUMER_KEY;

    @Value("${CONSUMER_SECRET}")
    private String CONSUMER_SECRET;

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
