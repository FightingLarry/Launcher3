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
    /* v3.1 */
    public static final String ITEM_TYPE = "item_type";

    public static final int ITEM_TYPE_APP = 0;

    public static final int ITEM_TYPE_FOLDER = 1;

    public static final String TABLE_NAME = "appType";

    public static final String ID = "_id";

    public static final Uri CONTENT_URI = Uri.parse("content://" + LauncherProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final String APPTYPE = "appType";

    public static final String PACKAGENAME = "packageName";

    public static final String CLASSNAME = "className";

    public static final String TITLE = "title";

    public static final String MODIFIED = "modified";

    // v3.0
    public static AppTypeModel queryByPackageClassName(Context context, String packageName, String className) {
        String[] selectionArgs = {packageName, className, String.valueOf(ITEM_TYPE_APP)};
        List<AppTypeModel> models =
                query(context, null, PACKAGENAME + "=? AND " + CLASSNAME + "=? " + ITEM_TYPE + "=? ", selectionArgs,
                        null);
        if (models != null && models.size() > 0) {
            return models.get(0);
        }
        return null;
    }

    // v3.1
    public static List<AppTypeModel> queryByFolderType(Context context, String folderType, int itemType) {
        String[] selectionArgs = {folderType, String.valueOf(itemType)};
        return query(context, null, APPTYPE + "=? AND " + ITEM_TYPE + "=? ", selectionArgs, null);
    }

    /**
     * 通过AppType查找应用信息
     * 
     * @param context
     * @param appType
     * @return
     */
    public static AppTypeModel queryByAppType(Context context, String appType) {
        String[] selectionArgs = {appType, String.valueOf(ITEM_TYPE_APP)};
        List<AppTypeModel> models = query(context, null, APPTYPE + "=? " + ITEM_TYPE + "=? ", selectionArgs, null);
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
            // 查找AppTypeTable表
            cursor = contentResolver.query(uri, projection, selection.toString(), selectionArgs, sortOrder);
            List<AppTypeModel> list = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // 存储结果
                    AppTypeModel model = new AppTypeModel();
                    model.appType = cursor.getString(cursor.getColumnIndexOrThrow(APPTYPE));
                    model.packageName = cursor.getString(cursor.getColumnIndexOrThrow(PACKAGENAME));
                    model.className = cursor.getString(cursor.getColumnIndexOrThrow(CLASSNAME));
                    model.title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
                    model.itemType = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_TYPE));
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
            values.put(ITEM_TYPE, appTypeModel.itemType);
            return contentResolver.insert(CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
