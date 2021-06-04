package com.mail.filemonitor;

import com.mail.interfaces.MyInterface;
import com.mail.service.MyService;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * Copyright © CapRobin
 * <p>
 * Name：ReYoFileListener
 * Describe：文件夹及其文件操作监听类
 * Date：2021-05-26 10:45:55
 * Author: YuFarong CapRobin@yeah.net
 */
public class FileListener implements FileAlterationListener, MyInterface.FragmentCallBack {
    FileMonitor monitor = null;
    public MyInterface.ActivityCallBack listenerCallBack = new MyService();

    @Override
    public void onStart(FileAlterationObserver observer) {
//        listenerCallBack = (MyInterface.ActivityCallBack) this;
        //System.out.println("onStart");
    }

    @Override
    public void onDirectoryCreate(File directory) {
        //System.out.println("新建文件夹------>>" +"名称："+ directory.getName()+"-----路径："+directory.getPath()+"\n\t");
    }

    @Override
    public void onDirectoryChange(File directory) {
        //System.out.println("影响文件夹------>>" +"名称："+ directory.getName()+"-----路径："+directory.getPath()+"\n\t");
    }

    @Override
    public void onDirectoryDelete(File directory) {
        //System.out.println("删除文件夹------>>" +"名称："+ directory.getName()+"-----路径："+directory.getPath()+"\n\t");
    }

    @Override
    public void onFileCreate(File file) {
//        System.out.println("新建文件------>>" + "名称：" + file.getName() + "-----路径：" + file.getPath() + "\n\t");
        listenerCallBack.listenerToServiceMsg(1,file);
    }

    @Override
    public void onFileChange(File file) {
//        System.out.println("修改文件------>>" + "名称：" + file.getName() + "-----路径：" + file.getPath() + "\n\t");
        listenerCallBack.listenerToServiceMsg(2,file);
    }

    @Override
    public void onFileDelete(File file) {
//        System.out.println("删除文件------>>" + "名称：" + file.getName() + "-----路径：" + file.getPath() + "\n\t");
        listenerCallBack.listenerToServiceMsg(3,file);
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        //System.out.println("onStop");
    }

    @Override
    public void fraToActMsg(String msg) {

    }
}