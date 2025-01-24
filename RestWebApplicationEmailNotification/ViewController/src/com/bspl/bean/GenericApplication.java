package com.bspl.bean;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("resources")
public class GenericApplication extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Register root resources.
        classes.add(WS_GenratePdfFile.class);
        classes.add(WS_HR_EMAIL.class);
        classes.add(WS_SendMail.class);

        // Register provider classes.

        return classes;
    }
}
