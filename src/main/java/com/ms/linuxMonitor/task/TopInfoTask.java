package com.ms.linuxMonitor.task;

import com.jcraft.jsch.Session;
import com.ms.linuxMonitor.bean.ConnectUserInfo;
import com.ms.linuxMonitor.common.CommonUtils;
import com.ms.linuxMonitor.common.LinuxOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: hecy
 * @Date: 2018/10/23 15:39
 * @Version 1.0
 */
@Component
public class TopInfoTask {

    private static Logger logger = LoggerFactory.getLogger(TopInfoTask.class);

    public static final String CPU_MEM_SHELL = "top -b -n 1";

    public static final String[] COMMANDS = {CPU_MEM_SHELL};


    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public Map<String, BigDecimal> exec(Map<String, String> param) {

        Map<String, BigDecimal> result = new HashMap<>();
        StringBuilder buffer = new StringBuilder();
        String commandResult = param.get(CPU_MEM_SHELL);
        String[] strings = commandResult.split(LINE_SEPARATOR);
        //将返回结果按换行符分割
        for (String line : strings) {
            line = line.toUpperCase();//转大写处理

            //处理CPU Cpu(s): 10.8%us,  0.9%sy,  0.0%ni, 87.6%id,  0.7%wa,  0.0%hi,  0.0%si,  0.0%st
            if (line.contains("CPU(S):")) {
                String cpuStr = "CPU 用户使用占有率:";
                try {
                    String cpuusagerate = line.split(":")[1].split(",")[0].replace("US", "");
                    cpuStr += cpuusagerate;
                    result.put("cpuusagerate", new BigDecimal(cpuusagerate.trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                    cpuStr += "计算过程出错";
                }
                buffer.append(cpuStr).append(LINE_SEPARATOR);

                logger.info(buffer.toString());
                //处理内存 Mem:  66100704k total, 65323404k used,   777300k free,    89940k buffers
                //KiB Mem :  5946284 total,   116496 free,  5777988 used,    51800 buff/cache
            } else if (line.startsWith("KIB MEM")) {
                String memStr = "内存使用情况:";
                try {
                    memStr += line.split(":")[1]
                            .replace("TOTAL", "总计")
                            .replace("USED", "已使用")
                            .replace("FREE", "空闲")
                            .replace("BUFFERS", "缓存");
                    String data[] = line.split(":")[1].split("\\s+");
                    result.put("memusagerate", CommonUtils.toBigDecimal(data[1]).divide(CommonUtils.toBigDecimal(data[3]) , 2, RoundingMode.HALF_UP));//.divideToIntegralValue(CommonUtils.toBigDecimal(data[2])));

                } catch (Exception e) {
                    e.printStackTrace();
                    memStr += "计算过程出错";
                    buffer.append(memStr).append(LINE_SEPARATOR);
                    continue;
                }
                buffer.append(memStr).append(LINE_SEPARATOR);

            }

            logger.info("result : {}", buffer.toString());
        }
        return result;
    }
}
