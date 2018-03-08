package com.briup.woss.util;

import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.common.ConfigurationAWare;
import com.briup.woss.common.WossModule;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {
    //临时存放 实体类，用于getXXX方法
    private Map<String ,WossModule> moduleMap = new HashMap<String, WossModule>();

    public ConfigurationImpl() throws Exception {
        this("src/main/resources/conf/conf.xml");
    }

    public ConfigurationImpl(String s) throws Exception {

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(s);

        //获取根节点
        Element root = document.getRootElement();

        //遍历一级字节点
        for (Object o : root.elements()
                ) {
            //获取到元素
            Element e1 = (Element) o;
            //获取到类名
            String className = e1.attributeValue("class");
            WossModule module = (WossModule) Class.forName(className).newInstance();

            //每个实例对象的 属性
            Properties properties = new Properties();
            //遍历二级节点
            for (Object o2 : e1.elements()
                    ) {
                Element e2 = (Element) o2;
                String key = e2.getName();
                String value = e2.getText();
                properties.put(key, value);
            }
            //将属性传入，初始化

            module.init(properties);
            moduleMap.put(e1.getName(),module);
            for (Object ooo: moduleMap.values()
                 ) {
                if (ooo instanceof ConfigurationAWare){
                    ((ConfigurationAWare) ooo).setConfiguration(this);
                }
            }
        }

    }


    public BackUP getBackup() throws Exception {
        return (BackUP) moduleMap.get("backup");
    }

    public Logger getLogger() throws Exception {
        return (Logger) moduleMap.get("logger");
    }

    public Server getServer() throws Exception {
        return (Server) moduleMap.get("server");
    }

    public DBStore getDbStore() throws Exception {
        return (DBStore) moduleMap.get("dbstore");
    }

    public Client getClient() throws Exception {
        return (Client) moduleMap.get("client");
    }

    public Gather getGather() throws Exception {
        return (Gather) moduleMap.get("gather");
    }
}
