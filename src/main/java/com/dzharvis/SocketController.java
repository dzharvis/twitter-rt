package com.dzharvis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

@Controller
public class SocketController {

    private static final Logger log = Logger.getLogger(SocketController.class);

    @Autowired
    private TwitterDaemon td;

    @Autowired
    private StatusDispatcher sd;

    @PostConstruct
    public void dispatch() throws InterruptedException {
        td.run();
        sd.run();
    }

    @RequestMapping("/")
    public String home(HttpServletRequest request) {
        final String userIpAddress = request.getRemoteAddr();
        final String userAgent = request.getHeader("user-agent");
        log.info("Connection from ip: " + userIpAddress);
        log.info("Connection from agent: " + userAgent);
        return "/canvas";
    }

    @PreDestroy
    public void destroy() {
        td.getTwitterStream().shutdown();
    }
}
