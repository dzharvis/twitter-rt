package com.dzharvis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.concurrent.BlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StatusDispatcher implements Runnable {

    private static class MyStatus {
        public String text;
        public double x;
        public double y;
    }

    @Value("${address}")
    private String address;

    @Autowired
    private MessageSendingOperations<String> messages;

    private  BlockingDeque<Status> queue;

    @Override
    public void run() {
        while (true) {
            Status status = null;
            try {
                status = queue.takeLast();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String text = status.getText();
            double x = status.getGeoLocation().getLongitude();
            double y = status.getGeoLocation().getLatitude();
            text = pasteLinks(text);
            MyStatus s = new MyStatus();
            s.text = text;
            s.x = x;
            s.y = y;
            messages.convertAndSend(address, s);
        }
    }

    private String pasteLinks(String text) {
        String regex = "http[s]?://[\\w\\./]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String link = matcher.group();
            link = "<a href='" + link + "'>" + link + "</a>";
            matcher.appendReplacement(sb, link);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public BlockingDeque<Status> getQueue() {
        return queue;
    }

    public void setQueue(BlockingDeque<Status> queue) {
        this.queue = queue;
    }
}
