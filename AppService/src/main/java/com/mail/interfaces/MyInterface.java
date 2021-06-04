package com.mail.interfaces;


import java.io.File;

/**
 * Copyright © CapRobin
 * <p>
 * Name：InterfaceUtil
 * Describe：接口工具类
 * Date：2021-04-21 18:07:51
 * Author: YuFarong CapRobin@yeah.net
 */
public class MyInterface {
    /**
     * Describe：Activity——>Fragment传值
     * Params:
     * Return:
     * Date：2021-04-21 18:08:09
     */
    public interface ActivityCallBack {
        void listenerToServiceMsg(int optType, File file);
    }


    /**
     * Describe：Fragment——>Activity传值
     * Params:
     * Return:
     * Date：2021-04-21 18:08:09
     */
    public interface FragmentCallBack {
        void fraToActMsg(String msg);
    }
}
