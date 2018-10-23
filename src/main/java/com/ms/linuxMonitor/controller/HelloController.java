package com.ms.linuxMonitor.controller;



import com.ms.linuxMonitor.bean.ConnectUserInfoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class HelloController {

    private final Logger logger = Logger.getLogger(String.valueOf(getClass()));

    @Autowired
    ConnectUserInfoList connectUserInfoList;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String index() {
        logger.info("helloserver hello: "+ connectUserInfoList.getListmap());
        return "connectUserInfoList"+connectUserInfoList.getListmap();
    }

}
