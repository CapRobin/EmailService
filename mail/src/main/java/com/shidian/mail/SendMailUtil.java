package com.shidian.mail;


import androidx.annotation.NonNull;

import java.io.File;

/**
 * Created by Administrator on 2017/4/10.
 */

public class SendMailUtil {

    //腾讯邮箱(qq)
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "587";
    private static final String FROM_ADD = "1542519456@qq.com";
    private static final String FROM_PSW = "blrbaceyonctiibb";

    //网易邮箱(163)
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "25"; //或者465  994
//    private static final String FROM_ADD = "caprobin@163.com";
//    private static final String FROM_PSW = "HXFOVFWVTDJSVYBE";

    //网易邮箱(yeah待调试)
//    private static final String HOST = "smtp.yeah.net";
//    private static final String PORT = "25";
//    private static final String FROM_ADD = "caprobin@yeah.net";
//    private static final String FROM_PSW = "DKCGKDCIKZPQUTQH";


    public static void send(final File file,String toAdd){
        final MailInfo mailInfo = creatMail(toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo,file);
            }
        }).start();
    }


    public static void send(String toAdd){
        final MailInfo mailInfo = creatMail(toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }

    @NonNull
    private static MailInfo creatMail(String toAdd) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD);         // 你的邮箱地址
        mailInfo.setPassword(FROM_PSW);         // 您的邮箱密码
        mailInfo.setFromAddress(FROM_ADD);      // 发送的邮箱
        mailInfo.setToAddress(toAdd);           // 发到哪个邮件去
        mailInfo.setSubject("系統消息");         // 邮件主题
        mailInfo.setContent("系统异常数据");      // 邮件文本
        return mailInfo;
    }

}
