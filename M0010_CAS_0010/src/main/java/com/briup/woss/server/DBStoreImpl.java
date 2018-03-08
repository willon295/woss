package com.briup.woss.server;

import com.briup.woss.bean.BIDR;
import com.briup.woss.util.Configuration;
import com.briup.woss.util.Logger;
import com.mysql.jdbc.Connection;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

public class DBStoreImpl implements DBStore {
    static String DRIVER = null;
    static String USERNAME = null;
    static String PASSWORD = null;
    static String URL = null;
    static Connection conn = null;
    static Integer BATCH_SIZE;

    private static Logger  logger ;

    public void saveToDB(Collection<BIDR> collection) throws Exception {
        try {
            conn = getConn();
            String sql = "INSERT INTO bidrs  (username,login_date,logout_date,login_ip,nas_ip,time_duration) VALUES (?,?,?,?,?,?)";
            PreparedStatement pre = conn.prepareStatement(sql);
            int count = 0;
            for (BIDR b : collection
                    ) {
                pre.setString(1, b.getAAA_login_name());
                pre.setDate(2, new Date(b.getLogin_date().getTime()));
                pre.setDate(3, new Date(b.getLogout_date().getTime()));
                pre.setString(4, b.getLogin_ip());
                pre.setString(5, b.getNAS_ip());
                pre.setInt(6, b.getTime_duration());
                pre.addBatch();
                if ((count++ % BATCH_SIZE == 0) || count == collection.size()) {
                    pre.executeBatch();
                    pre.execute();
                }
            }
        } catch (SQLException e) {
           e.getStackTrace();
           logger.error("入库失败");
        }
    }



    public static Connection getConn() throws SQLException {
        if (conn == null) {
            conn = (Connection) DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return conn;
        }
        return conn;
    }


    public void setConfiguration(Configuration configuration) {

        try {
            logger=configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Properties properties) {
        USERNAME = properties.getProperty("userName");
        PASSWORD = properties.getProperty("passWord");
        URL = properties.getProperty("url");
        DRIVER = properties.getProperty("driver");
        BATCH_SIZE = Integer.valueOf(properties.getProperty("batch-size"));
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
