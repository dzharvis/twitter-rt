package com.dzharvis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import twitter4j.Status;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Controller
public class SocketController {

    @Autowired
    private TwitterDaemon td;

    @Autowired
    private StatusDispatcher sd;

    private final BlockingDeque<Status> queue = new LinkedBlockingDeque<>(10000);

    @PostConstruct
    public void dispatch() throws InterruptedException {
        td.setQueue(queue);
        sd.setQueue(queue);

        new Thread(td).start();
        new Thread(sd).start();
    }

    @PreDestroy
    public void destroy() {
        System.out.println("destroyed");
        td.getTwitterStream().shutdown();
    }
}
