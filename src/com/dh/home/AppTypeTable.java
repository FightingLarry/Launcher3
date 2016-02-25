package com.dh.home;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.android.launcher3.LauncherProvider;

/**
 * // v2.1 Created by DemonHunter on 2016/2/2.
 */
public class AppTypeTable {

    public static final String TABLE_APPTYPE = "appType";

    public static final String ID = "_id";

    public static final Uri CONTENT_URI = Uri.parse("content://" + LauncherProvider.AUTHORITY + "/" + TABLE_APPTYPE);

    public static final String APPTYPE = "appType";

    public static final String PACKAGENAME = "packageName";

    public static final String CLASSNAME = "className";

    public static final String TITLE = "title";

    public static final String MODIFIED = "modified";

    public static AppTypeModel queryByPackage(Context context, String packageName) {
        String[] selectionArgs = {packageName};
        List<AppTypeModel> models = query(context, null, PACKAGENAME + "=? ", selectionArgs, null);
        if (models != null && models.size() > 0) {
            return models.get(0);
        }
        return null;
    }

    public static AppTypeModel queryByAppType(Context context, String appType) {
        String[] selectionArgs = {appType};
        List<AppTypeModel> models = query(context, null, APPTYPE + "=? ", selectionArgs, null);
        if (models != null && models.size() > 0) {
            return models.get(0);
        }
        return null;
    }

    public static List<AppTypeModel> query(Context context, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            final ContentResolver contentResolver = context.getContentResolver();
            final Uri uri = CONTENT_URI;
            cursor = contentResolver.query(uri, projection, selection.toString(), selectionArgs, sortOrder);
            List<AppTypeModel> list = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    AppTypeModel model = new AppTypeModel();
                    model.appType = cursor.getString(cursor.getColumnIndexOrThrow(APPTYPE));
                    model.packageName = cursor.getString(cursor.getColumnIndexOrThrow(PACKAGENAME));
                    model.className = cursor.getString(cursor.getColumnIndexOrThrow(CLASSNAME));
                    model.title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
                    list.add(model);
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static Uri save(Context context, AppTypeModel appTypeModel) {
        try {
            final ContentResolver contentResolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(APPTYPE, appTypeModel.appType);
            values.put(PACKAGENAME, appTypeModel.packageName);
            values.put(CLASSNAME, appTypeModel.className);
            values.put(TITLE, appTypeModel.title);
            return contentResolver.insert(CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
