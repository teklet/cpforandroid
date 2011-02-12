package org.android.CPForAndroidPlusPlus;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {
	public static boolean checkForInstalledApp(Context ctx, String pkgName) {
		try {
			PackageManager pm = ctx.getPackageManager();
			pm.getPackageInfo(pkgName, 0);
			// Log.d(TAG, pkgString + " is installed");
			return true;
		} catch (NameNotFoundException e) {
			// Log.d(TAG, pkgString + " is not installed");
		}
		return false;
	}

	public static String getAppName(Context ctx, String pkgName) {
		try {
			PackageManager pm = ctx.getPackageManager();
			ApplicationInfo appInfo = pm.getApplicationInfo(pkgName, 0);
			String label = pm.getApplicationLabel(appInfo).toString();
			return label;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	public static String getAppVersionName(Context ctx, String pkgName) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pkgInfo = pm.getPackageInfo(pkgName, 0);
			String ver = pkgInfo.versionName;
			return ver;
		} catch (NameNotFoundException e) {
			return "0";
		}
	}

	public static int getAppVersionCode(Context ctx, String pkgName) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pkgInfo = pm.getPackageInfo(pkgName, 0);
			return pkgInfo.versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}
	
    public void DoSleep(Context c, int ms)
    {
        try {
			Thread.sleep(ms);
		} catch (InterruptedException e1) {
			new PopUpDialog(c, "DoSleep Exception", e1.getMessage()).show();
			e1.printStackTrace();
		}
    }
}
