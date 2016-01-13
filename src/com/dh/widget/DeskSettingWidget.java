package com.dh.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

public class DeskSettingWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_desk_setting);
        remoteViews.setImageViewBitmap(R.id.widget_desk_setting_image,
                Utilities.createIconBitmap(context.getResources().getDrawable(R.drawable.desk_settings), context));
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
