package com.ms.linuxMonitor.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @Author: hecy
 * @Date: 2018/10/23 17:45
 * @Version 1.0
 * 告警 任务类
 */
@Component
public class AlarmTask {
    private static Logger logger = LoggerFactory.getLogger(FilesSystemTask.class);

    @Value("${threshold.diskusagerate}")
    private String diskusagerate;
    @Value("${threshold.cpuusagerate}")
    private String cpuusagerate;
    @Value("${threshold.memusagerate}")
    private String memusagerate;


    public void checkValue4diskusage(String host, Map<String, String> filesSystemResult) {
        logger.info("filesSystemResult: {}", filesSystemResult);
        for (String moMountedOnkey : filesSystemResult.keySet()) {
            BigDecimal currentData = getValue(filesSystemResult.get(moMountedOnkey));
            if (currentData.compareTo(new BigDecimal(diskusagerate)) >= 0) {
                logger.info("host: {} ，挂载盘路径:{}，使用率: {}, 大于告警之值: {}", host, moMountedOnkey, filesSystemResult.get(moMountedOnkey), diskusagerate);

            }
        }


    }

    private static BigDecimal getValue(String s) {
        if (s != null) {
            String[] ss = s.split("%");
            return new BigDecimal(ss[0]).divide(new BigDecimal(100) , 2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(0);

    }


    public static void main(String[] args) {
        getValue("48%");
    }
}
