package com.briup.woss.util;

import java.util.Properties;

public class LoggerImpl implements Logger{

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LoggerImpl.class);

    public void debug(String msg) {

        logger.debug(msg);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void fatal(String msg) {

        logger.fatal(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {

        logger.warn(msg);
    }


    public void setConfiguration(Configuration configuration) {

    }

    public void init(Properties properties) {


    }
}
