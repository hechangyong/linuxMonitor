package com.ms.linuxMonitor;

import com.jcraft.jsch.Session;
import com.ms.linuxMonitor.bean.ConnectUserInfo;
import com.ms.linuxMonitor.bean.ConnectUserInfoList;
import com.ms.linuxMonitor.common.LinuxOperation;
import com.ms.linuxMonitor.common.MailUtils;
import com.ms.linuxMonitor.task.AlarmTask;
import com.ms.linuxMonitor.task.FilesSystemTask;
import com.ms.linuxMonitor.task.TopInfoTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: hecy
 * @Date: 2018/10/23 15:45
 * @Version 1.0
 */

@Component
public class SchedulingConfig {
    private static Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    @Autowired
    ConnectUserInfoList connectUserInfoList;

    @Autowired
    TopInfoTask topInfoTask;

    @Autowired
    FilesSystemTask filesSystemTask;

    @Autowired
    LinuxOperation linuxOperation;

    @Autowired
    AlarmTask alarmTask;

    @Autowired
    MailUtils mailUtils;

    /**
     * 根据cron表达式格式触发定时任务
     * cron表达式格式:
     * 1.Seconds Minutes Hours DayofMonth Month DayofWeek Year
     * 2.Seconds Minutes Hours DayofMonth Month DayofWeek
     * 顺序:
     * 秒（0~59）
     * 分钟（0~59）
     * 小时（0~23）
     * 天（月）（0~31，但是你需要考虑你月的天数）
     * 月（0~11）
     * 天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
     * 年份（1970－2099）
     * <p>
     * 注:其中每个元素可以是一个值(如6),一个连续区间(9-12),一个间隔时间(8-18/4)(/表示每隔4小时),一个列表(1,3,5),通配符。
     * 由于"月份中的日期"和"星期中的日期"这两个元素互斥的,必须要对其中一个设置?.
     * <p>
     * 每天凌晨 1 点 开始执行任务
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void cronScheduled() {

        Map<String, ConnectUserInfo> map = connectUserInfoList.getMapConnectUserInfo();
        for (String host : map.keySet()) {
            logger.info("获取{}相关信息开始", host);
            taskManager(map.get(host));
            logger.info("监控{}结束", host);
        }
    }

    public void taskManager(ConnectUserInfo connectUserInfo) {
        Session session = null;
        try {
            // todo 可以添加配置， 此机器是否需要监控
            session = linuxOperation.connect(connectUserInfo.getName(), connectUserInfo.getPassword(), connectUserInfo.getHost());
            Map<String, BigDecimal> topResult = topInfoTask.exec(getCommandResult(session, TopInfoTask.COMMANDS));
            Map<String, String> filesSystemResult = filesSystemTask.exec(getCommandResult(session, FilesSystemTask.COMMANDS));

            alarmTask.checkValue4diskusage(connectUserInfo.getHost(), filesSystemResult);
            alarmTask.checkValue4cpuResult(connectUserInfo.getHost(), topResult);
        } catch (Exception e) {
            logger.error("e:{}", e.getMessage(), e);
        } finally {
            linuxOperation.closeSession(session);
        }


    }


    private Map<String, String> getCommandResult(Session session, String[] commands) {
        try {
            return linuxOperation.runDistanceShell(session, commands);
        } catch (Exception e) {
            return null;
        }
    }

}