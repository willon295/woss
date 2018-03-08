package com.briup.woss.server;

import com.briup.woss.bean.BIDR;
import com.briup.woss.util.BackUP;
import com.briup.woss.util.Configuration;
import com.briup.woss.util.ConfigurationImpl;
import com.briup.woss.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

public class ServerImpl implements Server {

    private Logger logger;
    private BackUP backUP;
    private Integer LISTER_PORT;

    public void reciver() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(LISTER_PORT);
            System.out.println("Server启动成功----");
        } catch (IOException e) {
            logger.error("服务器启动失败");
        }
        DBStore ds = new DBStoreImpl();
        Socket server;
        while (true) {
            server = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            List<BIDR> data = null;
            try {
                data = (List<BIDR>) ois.readObject();
                System.out.println("接收数据---" + data.size());
            } catch (ClassNotFoundException e) {
                logger.error("服务器读取客户端对象失败");
            }
            logger.debug("Server读取数据成功---" + data.size());


            /*-------------------服务器数据存储势失败备份-----------------*/
            try {
                File backList = new File("ServerUserList");
                if (backList.exists()) {
                    List<BIDR> bidrs = (List<BIDR>) backUP.load("ServerUserList", BackUP.LOAD_REMOVE);
                    data.addAll(bidrs);
                }
                ds.saveToDB(data);
            } catch (Exception e) {
                logger.error("数据库连接失败--");
                String fileName = "ServerUserList";
                backUP.store(fileName, data, BackUP.STORE_APPEND);
            }
            logger.debug("数据写入完毕----");
        }

    }

    public void shutDown() throws Exception {


    }

    public void setConfiguration(Configuration configuration) {

        try {
            logger = configuration.getLogger();
            backUP = configuration.getBackup();
        } catch (Exception e) {


        }

    }

    public void init(Properties properties) {
        LISTER_PORT = Integer.valueOf(properties.getProperty("port"));
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new ConfigurationImpl();
        Server s = configuration.getServer();
        s.reciver();
    }
}
