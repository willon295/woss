package com.briup.woss.mr.cl;

import org.apache.hadoop.io.Text;

public class DataParser {

    private boolean valid;
    private String flag;
    private BIDRWritable bidr = new BIDRWritable();
    public void parse(String line) {
        String[] strs = line.split("\\|");
        if (strs.length < 5) {
            valid = false;
            return;
        }
        String aaaName = strs[0];
        String nasIp = strs[1];
        String flag = strs[2];
        Long time = Long.parseLong(strs[3]) * 1000;
        String userIp = strs[4];
        valid = true;
        if ("7".equals(flag)){
            bidr.setAaaName(aaaName);
            bidr.setNsaIp(nasIp);
            bidr.setLoginTime(time);
            bidr.setLoginIp(userIp);
        }else if ("8".equals(flag)){
            bidr.setLoginIp(userIp);
            bidr.setLogoutTime(time);
        }
    }

    public  void  parse(Text text) {
        parse(text.toString());
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public BIDRWritable getBidr() {
        return bidr;
    }

    public void setBidr(BIDRWritable bidr) {
        this.bidr = bidr;
    }
}
