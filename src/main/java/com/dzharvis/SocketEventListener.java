package com.dzharvis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.locks.ReentrantLock;

@Component
public class SocketEventListener implements ApplicationListener {

    private static final Logger log = Logger.getLogger(SocketEventListener.class);
    private int counter;
    private boolean isStreamDestroyed;
    private ReentrantLock mutex = new ReentrantLock();

    @Autowired
    private TwitterDaemon td;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionDisconnectEvent) {
            mutex.lock();
            try {
                counter = counter <= 0 ? 0 : counter - 1;
                if (counter == 0) {
                    td.getTwitterStream().shutdown();
                    log.info("Stream stopped");
                    isStreamDestroyed = true;
                }
                return;
            } finally {
                mutex.unlock();
            }
        }
        if (event instanceof SessionConnectedEvent) {
            mutex.lock();
            try {
                counter++;
                if (isStreamDestroyed) {
                    td.getTwitterStream().sample();
                    log.info("Stream started");
                    isStreamDestroyed = false;
                }
                return;
            } finally {
                mutex.unlock();
            }
        }
    }
}
