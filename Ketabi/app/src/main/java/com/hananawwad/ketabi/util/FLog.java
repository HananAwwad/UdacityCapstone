package com.hananawwad.ketabi.util;

import android.util.Log;

/*
 * @author hananawwad
 */
public class FLog {

    /** Switch for showing logs **/
    private static final boolean showLog = true;

    public static void d(Object ob, String mssg){
        if(showLog){
            Log.d(makeLogNameSpace(ob) + " : ", mssg);
        }
    }

    public static void w(Object ob, String mssg){
        if(showLog) {
            Log.w(makeLogNameSpace(ob) + " : ", mssg);
        }
    }

    public static void i(Object ob, String mssg){
        if(showLog) {
            Log.i(makeLogNameSpace(ob) + " : ", mssg);
        }
    }

    public static void e(Object ob, String mssg){
        if(showLog) {
            Log.e(makeLogNameSpace(ob) + " : ", mssg);
        }
    }

    private static String makeLogNameSpace(Object ob){
        return "Ketabi => " + ob.getClass().getName();
    }
}
