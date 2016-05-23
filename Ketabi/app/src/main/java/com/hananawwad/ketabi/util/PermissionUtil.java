package com.hananawwad.ketabi.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hananawwad
 */
public class PermissionUtil {


    public static boolean isPermissionGranted(Context context, String permission){
        int permissionValue = ContextCompat.checkSelfPermission(context, permission);
        return permissionValue == PackageManager.PERMISSION_GRANTED;
    }

    public static List<String> deniedPermissions(Context context, String... permissions){

        List<String> deniedPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            if(!isPermissionGranted(context, permission)){
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }


    public static boolean confirmGrantedPermissions(int[] grantResult){
        for (int aGrantResult : grantResult) {
            if (aGrantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
