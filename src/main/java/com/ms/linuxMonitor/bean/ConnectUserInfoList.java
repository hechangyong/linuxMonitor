package com.ms.linuxMonitor.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: hecy
 * @Date: 2018/10/23 14:19
 * @Version 1.0
 */
@Component
@ConfigurationProperties(prefix = "connectuserinfolist")
public class ConnectUserInfoList {

    private List<Map<String, String>> listmap;

    private Map<String, ConnectUserInfo> mapConnectUserInfo;

    public Map<String, ConnectUserInfo> getMapConnectUserInfo() {
        return mapConnectUserInfo;
    }


    public List<Map<String, String>> getListmap() {
        return listmap;
    }

    public void setListmap(List<Map<String, String>> listmap) {
        Map<String, ConnectUserInfo> mapCon = new HashMap<>();
        for (Map<String, String> map : listmap) {
            mapCon.put(map.get("host"), new ConnectUserInfo(map.get("name"), map.get("host"), map.get("password")));
        }
        this.mapConnectUserInfo = mapCon;
        this.listmap = listmap;
    }

}

