package com.apk.editor.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;

import com.apk.editor.R;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/*
 * Created by APK Explorer & Editor <apkeditor@protonmail.com> on March 04, 2021
 */
public class APKEditorUtils {

    public static int getThemeAccentColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    public static boolean isFullVersion(Context context) {
        return context.getPackageName().equals("com.apk.editor");
    }

    public static CharSequence fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static void unzip(String zip, String path) {
        try (ZipFile zipFile = new ZipFile(zip)) {
            zipFile.extractAll(path);
        } catch (IOException ignored) {
        }
    }

    public static void zip(File path, File zip) {
        try (ZipFile zipFile = new ZipFile(zip)) {
            for (File mFile : Objects.requireNonNull(path.listFiles())) {
                if (mFile.isDirectory()) {
                    zipFile.addFolder(mFile);
                } else {
                    zipFile.addFile(mFile);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static boolean isDocumentsUI(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}