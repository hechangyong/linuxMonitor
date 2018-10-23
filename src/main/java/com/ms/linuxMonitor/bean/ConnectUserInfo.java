package com.ms.linuxMonitor.bean;

/**
 * @Author: hecy
 * @Date: 2018/10/23 15:51
 * @Version 1.0
 */
public class ConnectUserInfo {

    private String name;
    private String host;
    private String password;

    public ConnectUserInfo(String name, String host, String password) {
        this.name = name;
        this.host = host;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
