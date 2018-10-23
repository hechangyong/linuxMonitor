package com.ms.linuxMonitor.common;

import com.ms.linuxMonitor.bean.MailInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.File;
import java.util.Date;
import java.util.Properties;

@Component
public class MailUtils {
    private static Logger logger = LoggerFactory.getLogger(MailUtils.class);

    @Autowired
    MailInfo mailInfo;

    /**
     * 发送邮件
     *
     * @param subString 邮件主题
     * @param context   邮件
     */
    public void sendEmail(String subString, String context) {
        logger.info("准备发送邮件，邮件主题：{}", subString);
        long start = System.currentTimeMillis();
        String mailHost = mailInfo.mailHost;
        String sendFrom = mailInfo.sendFrom;
        String password = mailInfo.sendFromPass;
        String to = mailInfo.sendto;
        logger.info("发送给：{} ",to);
        //获取参数配置
        Properties props = new Properties();
        // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.transport.protocol", "smtp");
        // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.host", mailHost);
        // 需要请求认证
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log
        session.setDebug(false);
        if (to == null || to.length() == 0) {
            logger.error("获取邮件接收人失败");
            return;
        }
        Transport transport = null;
        try {
            transport = session.getTransport();
            transport.connect(sendFrom, password);
            MimeMessage message = null;
            message = createMimeMessage(session, sendFrom, to, null, subString, context);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送邮件提醒失败！", e);
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        long end = System.currentTimeMillis();
        logger.info("发送邮件总耗时：" + (end - start) + "ms");
    }


    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,
                                                File[] attachments, String subString, String content) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, subString, "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        //多个时参数形式  ：  "xxx@xxx.com,xxx@xxx.com,xxx@xxx.com"
        InternetAddress[] internetAddressTo = InternetAddress.parse(receiveMail);
        message.setRecipients(MimeMessage.RecipientType.TO, internetAddressTo);
        // 4. Subject: 邮件主题
        message.setSubject(subString, "UTF-8");

        MimeBodyPart text = new MimeBodyPart();
        //text.setContent(content, "text/html;charset=UTF-8");
        //修改为使用纯文本方式发送
        text.setContent(content, "text/plain;charset=UTF-8");

        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text);

        mm.setSubType("mixed");

        if (attachments != null) {
            for (File file : attachments) {
                MimeBodyPart attachment = new MimeBodyPart();
                DataHandler dh2 = new DataHandler(new FileDataSource(file));  // 读取本地文件
                attachment.setDataHandler(dh2);                                             // 将附件数据添加到“节点”
                attachment.setFileName(MimeUtility.encodeText(dh2.getName()));              // 设置附件的文件名（需要编码）
                mm.addBodyPart(attachment);
            }
        }
        // 5. Content: 邮件内容
        message.setContent(mm);
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }


}
