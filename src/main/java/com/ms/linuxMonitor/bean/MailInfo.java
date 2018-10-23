package com.ms.linuxMonitor.bean;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mail")
public class MailInfo {

    public String sendto;

    public String mailHost;

    public String sendFrom;

    public String sendFromPass;

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public String getSendFromPass() {
        return sendFromPass;
    }

    public void setSendFromPass(String sendFromPass) {
        this.sendFromPass = sendFromPass;
    }

    public String getSendto() {
        return sendto;
    }

    public void setSendto(String sendto) {
        this.sendto = sendto;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }
}
