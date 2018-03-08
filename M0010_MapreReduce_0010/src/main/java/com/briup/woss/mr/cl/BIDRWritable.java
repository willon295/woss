package com.briup.woss.mr.cl;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


// 数据实体类 ，实现 Hadoop 序列化接口

public class BIDRWritable implements Writable {

    private String aaaName;
    private String nsaIp;
    private Long loginTime;
    private Long logoutTime;
    private String loginIp;
    private Long timeDuration;

    public BIDRWritable() {
    }

    public String getAaaName() {
        return aaaName;
    }

    public void setAaaName(String aaaName) {
        this.aaaName = aaaName;
    }

    public String getNsaIp() {
        return nsaIp;
    }

    public void setNsaIp(String nsaIp) {
        this.nsaIp = nsaIp;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Long getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(Long timeDuration) {
        this.timeDuration = timeDuration;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(aaaName);
        out.writeUTF(nsaIp);
        out.writeLong(loginTime);
        out.writeLong(logoutTime);
        out.writeUTF(loginIp);
        out.writeLong(timeDuration);
    }



    @Override
    public void readFields(DataInput in) throws IOException {
        this.aaaName = in.readUTF();
        this.nsaIp = in.readUTF();
        this.loginTime = in.readLong();
        this.logoutTime = in.readLong();
        this.loginIp = in.readUTF();
        this.timeDuration = in.readLong();
    }

    @Override
    public String toString() {
        return "BIDRWritable{" +
                "aaaName='" + aaaName + '\'' +
                ", nsaIp='" + nsaIp + '\'' +
                ", loginTime=" + loginTime +
                ", logoutTime=" + logoutTime +
                ", loginIp='" + loginIp + '\'' +
                ", timeDuration=" + timeDuration +
                '}';
    }
}
