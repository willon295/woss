package com.briup.woss.client;

import com.briup.woss.bean.BIDR;
import com.briup.woss.util.BackUP;
import com.briup.woss.util.Configuration;
import com.briup.woss.util.ConfigurationImpl;
import com.briup.woss.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class ClientImpl implements Client {

    private Logger logger;
    private BackUP backUP;
    private String SERVER_IP;
    private Integer SERVER_PORT;

    public void send(Collection<BIDR> list) {
        Socket s = null;
        File backList = new File("ClientUserList");
        if (backList.exists()) {
            //如果上次的文件存在，将数据合并，删除备份文件
            List<BIDR> clientUserList = (List<BIDR>) backUP.load("ClientUserList", BackUP.LOAD_REMOVE);
            list.addAll(clientUserList);
        }
        try {
            s = new Socket(SERVER_IP, SERVER_PORT);
        } catch (IOException e) {
            logger.error("服务器连接失败");
        }
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(list);
            oos.flush();
        } catch (IOException e) {
            logger.error("网络异常");
            /*-------------客户端网络异常文件备份-----------------*/
            String filename = "ClientUserList";
            backUP.store(filename, list, BackUP.STORE_APPEND);
        }


    }

    public void setConfiguration(Configuration configuration) {

        try {
            logger = configuration.getLogger();
            backUP = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Properties properties) {
        SERVER_IP = (String) properties.get("ip");
        SERVER_PORT = Integer.valueOf(properties.getProperty("port"));

    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new ConfigurationImpl();
        Client c = configuration.getClient();
        Gather g = configuration.getGather();
        List<BIDR> gather = (List<BIDR>) g.gather();
        System.out.println("采集数据---" + gather.size());
        c.send(gather);
        System.out.println("成功发送----");
    }
}
