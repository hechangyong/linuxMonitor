package com.ms.linuxMonitor.task;

import com.ms.linuxMonitor.SchedulingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: hecy
 * @Date: 2018/10/23 15:27
 * @Version 1.0
 */
@Component
public class FilesSystemTask {
    private static Logger logger = LoggerFactory.getLogger(FilesSystemTask.class);

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILES_SHELL = "df -hl";
    public static final String[] COMMANDS = {FILES_SHELL};

    public Map<String, String> exec(Map<String, String> result) {
        String commandResult = result.get(FILES_SHELL);

        try {
           return disposeFilesSystem(commandResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //处理系统磁盘状态

    /**
     * Filesystem            Size  Used Avail Use% Mounted on
     * /dev/sda3             442G  327G   93G  78% /
     * tmpfs                  32G     0   32G   0% /dev/shm
     * /dev/sda1             788M   60M  689M   8% /boot
     * /dev/md0              1.9T  483G  1.4T  26% /ezsonar
     *
     * @param commandResult 处理系统磁盘状态shell执行结果
     * @return 处理后的结果
     */
    private static Map<String, String> disposeFilesSystem(String commandResult) {
        String[] strings = commandResult.split(LINE_SEPARATOR);
        Map<String, String> mountSizeMap = new HashMap<>();
        // final String PATTERN_TEMPLATE = "([a-zA-Z0-9%_/]*)\\s";
        int size = 0;
        int used = 0;
        for (int i = 0; i < strings.length - 1; i++) {
            if (i == 0) continue;

            int temp = 0;
//            String[] tempString = strings[i].split("\\b");
            String[] tempString = strings[i].split("\\s+");
            String useRateTemp = null;
            for (String s : tempString) {
                if (temp == 0) {
                    temp++;
                    continue;
                }
                if (!s.trim().isEmpty()) {
                    if (temp == 1) {
                        size += disposeUnit(s);
                        temp++;
                    } else if (temp == 2) {
                        used += disposeUnit(s);
                        temp++;
                    } else if (temp == 3) {
                        temp++;
                    } else if (temp == 4) {
                        useRateTemp = s;
                        temp++;
                    } else {
                        mountSizeMap.put(s, useRateTemp);
                        temp = 0;
                        useRateTemp = null;
                    }
                }
            }
        }

        logger.info("系统磁盘状态: " + new StringBuilder().append("大小 ").append(size).append("G , 已使用").append(used).append("G ,空闲")
                .append(size - used).append("G").toString());
        return mountSizeMap;
    }


    /**
     * 处理单位转换
     * K/KB/M/T 最终转换为G 处理
     *
     * @param s 带单位的数据字符串
     * @return 以G 为单位处理后的数值
     */
    private static int disposeUnit(String s) {

        try {
            s = s.toUpperCase();
            String lastIndex = s.substring(s.length() - 1);
            String num = s.substring(0, s.length() - 1);
            int parseInt = Integer.parseInt(num);
            if (lastIndex.equals("G")) {
                return parseInt;
            } else if (lastIndex.equals("T")) {
                return parseInt * 1024;
            } else if (lastIndex.equals("M")) {
                return parseInt / 1024;
            } else if (lastIndex.equals("K") || lastIndex.equals("KB")) {
                return parseInt / (1024 * 1024);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
}
