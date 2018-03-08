package com.briup.woss.mr;


import org.apache.hadoop.io.Text;

/**
 * 解析原始数据的类
 */
public class WossDataParser {

    private String aaaName;  //用户名
    private String nasIp;  //nasIp
    private String flag; //上下线标记信息
    private Long time; //上下线时间
    private String userIp;  //登录的IP
    private boolean valid;   //判断当前行是否是合理的数据


    public void parse(String line) {
        String[] strs = line.split("\\|");
        if (strs.length < 5) {
            valid = false;
            return;
        }
        aaaName = strs[0];
        nasIp = strs[1];
        flag = strs[2];
        time = Long.parseLong(strs[3])*1000;
        userIp = strs[4];
        valid = true;
    }

    public void parse(Text text) {
        parse(text.toString());
    }


    public String getAaaName() {
        return aaaName;
    }

    public void setAaaName(String aaaName) {
        this.aaaName = aaaName;
    }

    public String getNasIp() {
        return nasIp;
    }

    public void setNasIp(String nasIp) {
        this.nasIp = nasIp;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
