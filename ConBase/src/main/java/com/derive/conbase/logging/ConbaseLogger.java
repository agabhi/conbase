package com.derive.conbase.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ConbaseLogger {

    private String className = null;
    private ConbaseLogger dynaLogger= null;

    /** Actual logger instance that is used for logging */
    private transient Logger logger = null;

    /** Name of the logger eg:- 'com.dyna.web' or 'com.dyna.integration'*/
    private String name = null;

    private ConbaseLogger(Class class1) {
    	this.className = class1.getName();
        this.logger = getLoggerInstance();
    }

    public static ConbaseLogger getLogger(Class class1) {
        return new ConbaseLogger(class1);
     }

    public void trace(Object message) {
        if ( isTraceEnabled())
            getLoggerInstance().log(className, Level.TRACE, message, null);
    }

    public void trace(Object message, Throwable t) {
        if ( isTraceEnabled())
            getLoggerInstance().log(className, Level.TRACE, message, t);
    }

    public void debug(Object message) {
        if ( isDebugEnabled())
            getLoggerInstance().log(className, Level.DEBUG, message, null);
    }

    public void debug(Object message, Throwable t) {
        if(isDebugEnabled()){
            getLoggerInstance().log(className, Level.DEBUG, message, t);
        }
    }

    public void info(Object message) {
        if(isInfoEnabled()){
            getLoggerInstance().log(className, Level.INFO, message, null);
        }
    }

    public void info(Object message, Throwable t) {
        if ( isInfoEnabled())
            getLoggerInstance().log(className, Level.INFO, message, t);
    }

    public void warn(Object message) {
        if ( isWarnEnabled())
            getLoggerInstance().log(className, Level.WARN, message, null);
    }

    public void warn(Object message, Throwable t) {
        if ( isWarnEnabled())
            getLoggerInstance().log(className, Level.WARN, message, t);
    }

    public void error(Object message) {
       getLoggerInstance().log(className, Level.ERROR, message, null);
    }

    public void error(Object message, Throwable t) {
        getLoggerInstance().log(className, Level.ERROR, message, t);
    }

    public void fatal(Object message) {
        getLoggerInstance().log(className, Level.FATAL, message, null);
    }

    public void fatal(Object message, Throwable t) {
        getLoggerInstance().log(className, Level.FATAL, message, t);
    }


    /**
     * Get the Logger instance
     */
    private Logger getLoggerInstance() {
        if (logger == null) {
            logger = Logger.getLogger(className);
        }
        return (this.logger);
    }

    private boolean isDebugEnabled() {
        return getLoggerInstance().isDebugEnabled();
    }

    private boolean isInfoEnabled() {
        return getLoggerInstance().isInfoEnabled();
    }

    private boolean isTraceEnabled() {
        return getLoggerInstance().isEnabledFor(Level.TRACE);
    }

    private boolean isWarnEnabled() {
        return getLoggerInstance().isEnabledFor(Level.WARN);
    }
}
