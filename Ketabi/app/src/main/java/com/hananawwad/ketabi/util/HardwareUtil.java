package com.hananawwad.ketabi.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author hananawwad
 */
public class HardwareUtil {

    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}
