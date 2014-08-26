package com.dzharvis;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.BlockingDeque;


@Component
public class TwitterDaemon {

    private static final Logger log = Logger.getLogger(SocketEventListener.class);

    @Value("${CONSUMER_KEY}")
    private String CONSUMER_KEY;

    @Value("${CONSUMER_SECRET}")
    private String CONSUMER_SECRET;

    @Value("${token}")
    private String token;

    @Value("${tokenSecret}")
    private String tokenSecret;

    private TwitterStream twitterStream;

    @Resource
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
                log.error(ex.getMessage());
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

    @Async
    public void run() {
        twitterStream.sample();
    }

    public TwitterStream getTwitterStream() {
        return twitterStream;
    }

}
