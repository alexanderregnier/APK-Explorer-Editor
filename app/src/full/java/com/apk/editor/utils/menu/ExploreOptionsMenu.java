package com.apk.editor.utils.menu;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.apk.editor.R;
import com.apk.editor.utils.tasks.ExploreAPK;
import com.apk.editor.utils.tasks.QuickEditsAPK;

import java.io.File;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.Dialog.sSingleItemDialog;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by APK Explorer & Editor <apkeditor@protonmail.com> on March 16, 2025
 */
public class ExploreOptionsMenu {

    public static String[] getOption(Context context) {
        return new String[] {
                context.getString(R.string.explore_options_simple),
                context.getString(R.string.explore_options_full),
                context.getString(R.string.explore_options_quick),
                context.getString(R.string.prompt)
        };
    }

    public static void getMenu(String packageName, File apkFile, Uri uri, boolean exit, Activity activity) {
        if (sFileUtils.exist(new File(activity.getCacheDir().getPath(), uri != null ? new File(Objects.requireNonNull(uri.getPath())).getName() : packageName != null ? packageName : apkFile.getName()))) {
            new ExploreAPK(packageName, apkFile, uri, -1, activity).execute();
        } else if (sCommonUtils.getString("decompileSetting", null, activity) == null) {
            new sSingleItemDialog(0, null, new String[] {
                    activity.getString(R.string.explore_options_simple),
                    activity.getString(R.string.explore_options_full),
                    activity.getString(R.string.explore_options_quick)
            }, activity) {

                @Override
                public void onItemSelected(int itemPosition) {
                    if (itemPosition == 2) {
                        new QuickEditsAPK(packageName, apkFile, uri, activity).execute();
                    } else {
                        new ExploreAPK(packageName, apkFile, uri, itemPosition, activity).execute();
                    }
                    if (exit) {
                        activity.finish();
                    }
                }
            }.show();
        } else if (sCommonUtils.getString("decompileSetting", null, activity).equals(activity.getString(R.string.explore_options_quick))) {
            new QuickEditsAPK(packageName, apkFile, uri, activity).execute();
        } else if (sCommonUtils.getString("decompileSetting", null, activity).equals(activity.getString(R.string.explore_options_full))) {
            new ExploreAPK(packageName, apkFile, uri, 1, activity).execute();
        } else {
            new ExploreAPK(packageName, apkFile, uri, 0, activity).execute();
        }
    }

}