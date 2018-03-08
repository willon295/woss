package com.briup.woss.util;

import java.io.*;
import java.util.Properties;



/*
 * 1. 网络异常，客户端备份
 * 2. 数据库连接异常，服务器备份
 * 3. 备份上一次采集的长度，下次采集跳过相应长度skip()
 * 4. 不完整的用户信息备份
 * */

public class BackUPImpl implements BackUP {

    private Logger logger;
    private String FILE_PATH;

    /*key是文件名 ， 通过文件名获取文件*/
    public Object load(String key, boolean flag) {
        Object loadFile = null;
        File file = new File(key);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            loadFile = ois.readObject();
        } catch (IOException e) {
            e.getStackTrace();
            logger.error("文件读取失败");
        } catch (ClassNotFoundException e) {
            e.getStackTrace();
            logger.error("对象读取失败");
        }
        return loadFile;
    }


    /*key是文件名，Object是要保存的List*/
    public void store(String key, Object data, boolean flag) {
        File file = new File(key);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(data);
        } catch (IOException e) {
            e.getStackTrace();
            logger.error("文件备份失败");
        }

    }

    public void setConfiguration(Configuration configuration) {

        try {
            logger = configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Properties properties) {

        FILE_PATH = properties.getProperty("back-temp");
    }
}
