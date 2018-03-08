package com.briup.woss.client;

import com.briup.woss.bean.BIDR;
import com.briup.woss.util.BackUP;
import com.briup.woss.util.Configuration;
import com.briup.woss.util.ConfigurationImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;

public class GatherImpl implements Gather {


    /*======测试数据===========*/

    int TEST_READ_SIZE = 0;
    /*======测试数据===========*/
    private BackUP backUP;
    private String FILE_PATH;


    public Collection<BIDR> gather() throws IOException {

        String lastReadSize = "LastReadSize";
        String clientLoginUserMap = "ClientLoginUserMap";
        //保存上线的用户信息   ip---用户信息
        Map<String, BIDR> login_user_map = new HashMap<String, BIDR>();
        Map<String, BIDR> lastLoginUserMap = null;
        /*-------读取上一次未下线的用户-------*/
        File file = new File(clientLoginUserMap);
        if (file.exists()) {
            lastLoginUserMap = (Map<String, BIDR>) backUP.load(clientLoginUserMap, BackUP.LOAD_UNREMOVE);
        }

        if (lastLoginUserMap != null) {
            login_user_map = lastLoginUserMap;
            System.out.println("从上次登录用户的数据：" + login_user_map.size());
        }

        /*记录读取的偏移量*/
        long READ_LINE = 0L;

        /*读取上一次的偏移量*/
        Long LAST_READ = 0L;


        File last_read_file = new File(lastReadSize);
        if (last_read_file.exists()) {
            LAST_READ = (Long) backUP.load(lastReadSize, BackUP.LOAD_REMOVE);
        }

        /*记录完整用户信息 List*/
        List<BIDR> userList = new ArrayList<BIDR>();


        /*读取文件缓存*/
        System.out.println(FILE_PATH);
        BufferedReader bf = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(FILE_PATH)));

        String line = null;
        //跳过上次读取的偏移量
        if (LAST_READ != 0) {
            long skip = bf.skip(LAST_READ);
            System.out.println("跳过---" + skip);
            READ_LINE += LAST_READ;
        }

        /*---------从文件读取用户信息---------*/
        while ((line = bf.readLine()) != null) {
            String[] datas = line.split("\\|");
            String user_name = datas[0];
            String nsa_ip = datas[1];
            String action_type = datas[2];// 7 \   8
            long time_stap = Long.parseLong(datas[3]) * 1000;
            String user_ip = datas[4];


            /*将数据进行封装*/
            BIDR b = new BIDR();
            b.setLogin_ip(user_ip);
            b.setNAS_ip(nsa_ip);


            if (action_type.equals("7")) {
                //临时存储进一个 map《ip, 登录时间》
                b.setAAA_login_name(user_name);
                b.setLogin_date(new Timestamp(time_stap));
                login_user_map.put(b.getLogin_ip(), b);

            } else {
                //用户登出 ， 从登录的用户map  通过 ip  读取  用户名，登录时间
                BIDR b2 = login_user_map.get(b.getLogin_ip());
                b.setAAA_login_name(b2.getAAA_login_name());
                b.setLogin_date(b2.getLogin_date());
                b.setLogout_date(new Timestamp(time_stap));
                b.setTime_duration(Integer.parseInt(String.valueOf(b.getLogout_date().getTime() - b.getLogin_date().getTime())));
                userList.add(b);
                /*添加到List之后，从map中移除*/
                login_user_map.remove(b.getLogin_ip());
            }

//            //*======测试数据===========*//*
//            TEST_READ_SIZE++;
//            //*======测试数据===========*//*
//            if (TEST_READ_SIZE == 400) {
//                break;
//            }
            READ_LINE += line.getBytes().length + 2;


        }
        /*-----------------不完整用户信息备份-------------------------*/
        backUP.store(clientLoginUserMap, login_user_map, BackUP.STORE_APPEND);
        System.out.println("只有上线信息的客户：" + login_user_map.size());

        /*--------------------------上次读取偏移量备份-------------------*/

        backUP.store(lastReadSize, READ_LINE, BackUP.STORE_OVERRIDE);
        System.out.println("本次读取的偏移量：" + READ_LINE);
        System.out.println("完整用户数据：" + userList.size());
        return userList;
    }

    public void setConfiguration(Configuration configuration) {

        try {
            backUP = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Properties properties) {
        FILE_PATH = properties.getProperty("src-file");

    }

    public static void main(String[] args) throws Exception {

        Configuration c = new ConfigurationImpl();
        Collection<BIDR> gather = c.getGather().gather();
    }

}
