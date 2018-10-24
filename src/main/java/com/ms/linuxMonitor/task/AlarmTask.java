package com.ms.linuxMonitor.task;

import com.ms.linuxMonitor.common.MailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    @Autowired
    Environment environment;

    @Autowired
    MailUtils mailUtils;

    /**
     * 监控磁盘使用率， >= 阈值 时，告警。 发送邮件。
     *
     * @param host
     * @param filesSystemResult
     */
    public void checkValue4diskusage(String host, Map<String, String> filesSystemResult) {
        logger.info("filesSystemResult: {}", filesSystemResult);
        StringBuffer sb = new StringBuffer();
        String env = environment.getActiveProfiles()[0];
        sb.append("【" + env + "】").append("\r\n");
        for (String moMountedOnkey : filesSystemResult.keySet()) {
            BigDecimal currentData = getValue(filesSystemResult.get(moMountedOnkey));
            if (currentData.compareTo(new BigDecimal(diskusagerate)) >= 0) {
                logger.info("host: {} ，挂载盘路径:{}，使用率: {}, 大于告警之值: {}",
                        host, moMountedOnkey, filesSystemResult.get(moMountedOnkey), diskusagerate);
                sb.append("************************").append("\r\n");
                sb.append("    host: " + host).append("\r\n");
                sb.append("    挂载盘路径: " + moMountedOnkey).append("\r\n");
                sb.append("    使 用 率 : " + filesSystemResult.get(moMountedOnkey)).append("\r\n");
                sb.append("    告警值阈值 : " + diskusagerate).append("\r\n");
                sb.append("************************").append("\r\n");
            }
        }
        String mailContent = sb.toString();
        if (mailContent.contains("host")) {
            mailUtils.sendEmail("【" + env + "】磁盘使用率【告警】", mailContent);
        }

    }

    private static BigDecimal getValue(String s) {
        if (s != null) {
            String[] ss = s.split("%");
            return new BigDecimal(ss[0]).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(0);

    }


    public static void main(String[] args) {
        getValue("48%");
    }

    public void checkValue4cpuResult(String host, Map<String, BigDecimal> topResult) {
        logger.info("topResult: {}", topResult);
        StringBuffer sb = new StringBuffer();
        String env = environment.getActiveProfiles()[0];
        sb.append("【" + env + "】").append("\r\n");
        BigDecimal currentCPUData = topResult.get("cpuusagerate").divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
        if (currentCPUData.compareTo(new BigDecimal(cpuusagerate)) >= 0) {
            logger.info("host: {} ， CPU使用率: {}, 大于告警之值: {}",
                    host, currentCPUData, cpuusagerate);
            sb.append("************************").append("\r\n");
            sb.append("    host     : " + host).append("\r\n");
            sb.append("    CPU使用率 : " + currentCPUData).append("\r\n");
            sb.append("    告警值阈值 : " + cpuusagerate).append("\r\n");
            sb.append("************************").append("\r\n\r\n");
        }
        BigDecimal currentMEMData = topResult.get("memusagerate").divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
        if (currentCPUData.compareTo(new BigDecimal(memusagerate)) >= 0) {
            logger.info("host: {} ， MEM使用率: {}, 大于告警之值: {}",
                    host, currentMEMData, memusagerate);
            sb.append("************************").append("\r\n");
            sb.append("    host     : " + host).append("\r\n");
            sb.append("    MEM使用率 : " + currentMEMData).append("\r\n");
            sb.append("    告警值阈值 : " + memusagerate).append("\r\n");
            sb.append("************************").append("\r\n");
        }

        String mailContent = sb.toString();
        if (mailContent.contains("host")) {
            mailUtils.sendEmail("【" + env + "】CPU@MEM使用率【告警】", mailContent);
        }
    }
}
