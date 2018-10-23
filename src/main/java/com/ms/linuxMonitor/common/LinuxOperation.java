package com.ms.linuxMonitor.common;

import com.jcraft.jsch.*;
import com.ms.linuxMonitor.SchedulingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: hecy
 * @Date: 2018/10/23 15:34
 * @Version 1.0
 * 公共类
 * linux 系统 连接，关闭， 发送命令， 获取返回等
 */
@Component
public class LinuxOperation {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    /**
     * 连接到指定的HOST
     *
     * @return isConnect
     * @throws JSchException JSchException
     */
    public  Session connect(String user, String passwd, String host) {
        JSch jsch = new JSch();
        Session session;
        logger.info("connect user: {}, host: {}", user, host);
        try {
            session = jsch.getSession(user, host, 22);
            session.setPassword(passwd);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            logger.error("{} connect error !", host);
            return null;
        }
        return session;
    }

    /**
     * 关闭session
     * @param session
     */
    public void closeSession(Session session){
        logger.info("closeSession : {}", session);
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * 远程连接Linux 服务器 执行相关的命令
     * session 没有关闭， 调用方 调用完成必须要手动关闭
     * @param commands 执行的脚本
     * @return 最终命令返回信息
     */
    public  Map<String, String> runDistanceShell(Session session, String[] commands) {
        if (session == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        StringBuilder stringBuffer;

        BufferedReader reader = null;
        Channel channel = null;
        try {
            for (String command : commands) {
                stringBuffer = new StringBuilder();
                channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);

                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);

                channel.connect();
                InputStream in = channel.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String buf;
                while ((buf = reader.readLine()) != null) {

                    //舍弃PID 进程信息
                    if (buf.contains("PID")) {
                        break;
                    }
                    stringBuffer.append(buf.trim()).append(LINE_SEPARATOR);
                }
                //每个命令存储自己返回数据-用于后续对返回数据进行处理
                map.put(command, stringBuffer.toString());
            }
        } catch (IOException | JSchException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
        return map;
    }


}
