package com.dh.home;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.dh.preference.AppTypePreference;


public class AppTypeHelper {

    private static final String QUERYINTENT_PHONE =
            "#Intent;action=android.intent.action.DIAL;launchFlags=0x10000000;end";
    public static final String QUERYINTENT_CONTACTS =
            "content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;end";
    private static final String QUERYINTENT_MMS =
            "#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;launchFlags=0x10000000;end";
    private static final String QUERYINTENT_BROWSER =
            "http://m.baidu.com/?from=1000228o#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;end";

    private static Map<String, List<ActivityInfo>> sPackageMap = new HashMap<>();

    public synchronized static void configSystemAppIcon(Context context) {

        if (AppTypePreference.getInstance(context).getHasConfigSystemAppIcon()) {
            return;
        }
        AppTypePreference.getInstance(context).saveHasConfigSystemAppIcon(true);

        next: for (AppType appType : AppType.values()) {
            List<ActivityInfo> infos = new ArrayList<>();
            String queryintent = "";
            boolean isQueryIntent = false;
            if (appType == AppType.PHONE) {
                queryintent = QUERYINTENT_PHONE;
                isQueryIntent = true;
            } else if (appType == AppType.CONTACTS) {
                queryintent = QUERYINTENT_CONTACTS;
                isQueryIntent = true;
            } else if (appType == AppType.MMS) {
                queryintent = QUERYINTENT_MMS;
                isQueryIntent = true;
            } else if (appType == AppType.BROWSER) {
                queryintent = QUERYINTENT_BROWSER;
                isQueryIntent = true;
            }

            if (isQueryIntent) {
                ActivityInfo info = matchLoaclAppsByQueryIntent(context, queryintent);
                if (info == null) {
                    infos = matchLoaclAppsByAppType(context, appType.getValue());
                } else {
                    // lyc 华为手机联系人、电话、短信写在一个程序里面，坑啊。。。
                    if (appType == AppType.PHONE || appType == AppType.MMS) {
                        ActivityInfo contacts = matchLoaclAppsByQueryIntent(context, QUERYINTENT_CONTACTS);
                        if (!(contacts != null && info.packageName.equals(contacts.packageName) && info.name
                                .equals(contacts.name))) {
                            infos.add(info);
                        }
                    } else {
                        infos.add(info);
                    }
                }
            } else {
                infos = matchLoaclAppsByAppType(context, appType.getValue());
            }
            if (infos != null && infos.size() > 0) {
                if (isQueryIntent) {
                    ActivityInfo activityInfo = infos.get(0);
                    saveToDb(context, appType, activityInfo);
                } else {
                    for (ActivityInfo info : infos) {
                        String packageName = info.packageName;
                        boolean isSystemApp = false;
                        if (!TextUtils.isEmpty(packageName)) {
                            try {
                                PackageInfo pkg = context.getPackageManager().getPackageInfo(packageName, 0);
                                isSystemApp =
                                        (pkg != null) && (pkg.applicationInfo != null)
                                                && ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
                            } catch (NameNotFoundException e) {}
                        }
                        if (isSystemApp) {
                            saveToDb(context, appType, info);
                            continue next;
                        }
                    }
                    ActivityInfo activityInfo = infos.get(0);
                    saveToDb(context, appType, activityInfo);
                }
            }

        }
    }

    private static void saveToDb(Context context, AppType appType, ActivityInfo activityInfo) {
        PackageManager packageManager = context.getPackageManager();
        AppTypeModel model = new AppTypeModel();
        model.appType = appType.getValue();
        model.packageName = activityInfo.packageName;
        model.className = activityInfo.name;
        model.title = activityInfo.loadLabel(packageManager).toString();
        AppTypeTable.save(context, model);
    }

    synchronized static List<ActivityInfo> matchLoaclAppsByAppType(Context context, String appType) {
        if (sPackageMap.isEmpty()) {
            matchLoaclApps(context);
        }
        List<ActivityInfo> list = new ArrayList<>();
        String[] appTypeArr = appType.split(",");
        for (int i = 0; i < appTypeArr.length; i++) {
            if (!TextUtils.isEmpty(appTypeArr[i])) {
                List<ActivityInfo> temp = sPackageMap.get(appTypeArr[i]);
                if (temp != null && temp.size() > 0) {
                    list.addAll(temp);
                }
            }
        }
        return list;
    }

    synchronized static ActivityInfo matchLoaclAppsByQueryIntent(Context context, String queryIntent) {
        PackageManager localPackageManager = context.getPackageManager();
        try {
            Iterator<ResolveInfo> localIterator =
                    localPackageManager.queryIntentActivities(Intent.parseUri(queryIntent, 0),
                            PackageManager.MATCH_DEFAULT_ONLY).iterator();
            List<ActivityInfo> unSystemAppList = new ArrayList<ActivityInfo>();
            while (localIterator.hasNext()) {
                ResolveInfo localResolveInfo = localIterator.next();
                if ((localResolveInfo.activityInfo == null) || (localResolveInfo.activityInfo.applicationInfo == null)) {
                    continue;
                }
                if ((ApplicationInfo.FLAG_SYSTEM & localResolveInfo.activityInfo.applicationInfo.flags) == 0) {
                    // 不是系统应用
                    unSystemAppList.add(localResolveInfo.activityInfo);
                    continue;
                }
                return localResolveInfo.activityInfo;
            }
            if (unSystemAppList != null && unSystemAppList.size() > 0) {
                return unSystemAppList.get(0);
            }
        } catch (URISyntaxException localURISyntaxException) {
            localURISyntaxException.printStackTrace();
        }

        return null;
    }

    private static void matchLoaclApps(Context paramContext) {
        Intent localIntent = new Intent(Intent.ACTION_MAIN, null);
        localIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> localList = paramContext.getPackageManager().queryIntentActivities(localIntent, 0);
        List<String> localArrayList = new ArrayList<>();
        localArrayList.add("com.tcl.mid.android.camera");
        localArrayList.add("com.android.jrdcamera.CameraLauncher");
        localArrayList.add("com.jrdcom.camera.CameraLauncher");
        localArrayList.add("com.android.camera.Camera");
        localArrayList.add("com.google.android.camera");
        localArrayList.add("com.android.camera");
        localArrayList.add("com.android.camera.CameraActivity");
        localArrayList.add("com.miui.camera");
        localArrayList.add("com.motorola.Camera.Camera");
        localArrayList.add("com.sec.android.app.camera.Camera");
        localArrayList.add("com.android.camera.CameraEntry");
        localArrayList.add("com.android.camera.CameraLauncher");
        localArrayList.add("com.sonyericsson.android.camera");
        localArrayList.add("com.lge.camera");
        localArrayList.add("com.android.lgecamera");
        localArrayList.add("com.android.hwcamera");
        localArrayList.add("com.mediatek.camera");
        localArrayList.add("com.baidu.camera");
        localArrayList.add("com.oppo.camera");
        localArrayList.add("com.oppo.camera.CameraLauncher");
        localArrayList.add("com.motorola.camera");
        localArrayList.add("zte.com.cn.camera.Camera");
        localArrayList.add("com.baidu.camera.CameraLauncher");
        localArrayList.add("com.htc.camera");
        localArrayList.add("com.asus.camera");
        localArrayList.add("com.meizu.media.camera");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.CAMERA.getValue());
        localArrayList.clear();
        localArrayList.add("com.jrdcom.music");
        localArrayList.add("com.lewa.player");
        localArrayList.add("com.lewa.player.activity.SplashActivity");
        localArrayList.add("com.android.music.MusicBrowserActivity");
        localArrayList.add("com.miui.player");
        localArrayList.add("com.miui.music");
        localArrayList.add("com.motorola.cmp");
        localArrayList.add("com.motorola.motmusic");
        localArrayList.add("com.motorola.blur.music");
        localArrayList.add("com.sec.android.app.music");
        localArrayList.add("com.lenovo.leos.lephone.music");
        localArrayList.add("com.lenovo.music");
        localArrayList.add("com.htc.music");
        localArrayList.add("com.lge.music");
        localArrayList.add("com.sonyericsson.music");
        localArrayList.add("com.andrew.apollo");
        localArrayList.add("com.android.mediacenter");
        localArrayList.add("com.oppo.music");
        localArrayList.add("com.airplayme.android.phone");
        localArrayList.add("com.android.bbkmusic.MusicBrowserActivity");
        localArrayList.add("com.gwsoft.imusic.controller");
        localArrayList.add("com.google.android.music");
        localArrayList.add("com.android.music");
        localArrayList.add("com.android.bbkmusic");
        localArrayList.add("com.meizu.musiconline");
        localArrayList.add("com.meizu.media.music");
        localArrayList.add("com.lewa.player.ui.outer.MusicMainEntryActivity");
        localArrayList.add("com.oppo.music.MainListActivity");
        localArrayList.add("com.smartisanos.music.activities.MusicMain");
        localArrayList.add("com.coolpad.music");
        localArrayList.add("com.ting.mp3.android");
        localArrayList.add("com.android.strengthenmusic");
        localArrayList.add("cn.zte.music.MusicBrowserActivity");
        localArrayList.add("cn.nubia.music.preset");
        localArrayList.add("com.baidu.musicplayer");
        localArrayList.add("com.imusic.musicplayer");
        localArrayList.add("com.asus.music");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.MUSIC.getValue());
        localArrayList.clear();
        localArrayList.add("com.lenovomobile.deskclock");
        localArrayList.add("com.tct.timetool");
        localArrayList.add("com.lenovo.deskclock");
        localArrayList.add("com.sec.android.app.clockpackage");
        localArrayList.add("com.htc.android.worldclock.WorldClockTabControl");
        localArrayList.add("com.lge.alarm");
        localArrayList.add("com.lge.clock");
        localArrayList.add("com.android.alarmclock.AlarmClock");
        localArrayList.add("com.motorola.blur.alarmclock");
        localArrayList.add("com.sonyericsson.organizer.deskclock.DeskClock");
        localArrayList.add("com.sonyericsson.organizer.Organizer");
        localArrayList.add("com.baidu.baiduclock");
        localArrayList.add("zte.com.cn.alarmclock");
        localArrayList.add("com.leadcore.clock");
        localArrayList.add("com.android.BBKClock");
        localArrayList.add("com.yulong.android.xtime");
        localArrayList.add("com.android.alarmclock");
        localArrayList.add("com.android.deskclock.DeskClock");
        localArrayList.add("com.android.deskclock");
        localArrayList.add("com.oppo.alarmclock.AlarmClock");
        localArrayList.add("com.aurora.deskclock");
        localArrayList.add("com.smartisanos.clock.activity.ClockActivity");
        localArrayList.add("com.android.timemanager");
        localArrayList.add("cn.nubia.deskclock.preset");
        localArrayList.add("com.asus.deskclock");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.CLOCK.getValue());
        localArrayList.clear();
        localArrayList.add("com.tct.calendar");
        localArrayList.add("com.motorola.calendar");
        localArrayList.add("com.lenovo.app.Calendar");
        localArrayList.add("com.htc.calendar");
        localArrayList.add("com.bbk.calendar.MainActivity");
        localArrayList.add("com.yulong.android.calendar");
        localArrayList.add("com.google.android.syncadapters.calendar");
        localArrayList.add("com.android.providers.calendar");
        localArrayList.add("com.lenovo.calendar.SplashActivity");
        localArrayList.add("com.google.android.calendar");
        localArrayList.add("com.android.calendar.AllInOneActivity");
        localArrayList.add("com.android.calendar2");
        localArrayList.add("com.android.calendar");
        localArrayList.add("cn.nubia.calendar.preset");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.CALENDAR.getValue());
        localArrayList.clear();
        localArrayList.add("com.google.android.calculator");
        localArrayList.add("com.tct.calculator");
        localArrayList.add("com.sec.android.app.calculator.Calculator");
        localArrayList.add("com.sec.android.app.calculator");
        localArrayList.add("com.sec.android.app.popupcalculator");
        localArrayList.add("com.baidu.calculator2");
        localArrayList.add("com.android.bbkcalculator.Calculator");
        localArrayList.add("my.android.calc");
        localArrayList.add("com.android.calculator2");
        localArrayList.add("com.android.calculator");
        localArrayList.add("com.smartisanos.calculator.Calculator");
        localArrayList.add("cn.nubia.calculator2.preset");
        localArrayList.add("com.htc.calculator");
        localArrayList.add("com.asus.calculator");
        localArrayList.add("com.lenovo.calculator");
        localArrayList.add("com.meizu.flyme.calculator");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.CALCULATOR.getValue());
        localArrayList.clear();
        localArrayList.add("com.lewa.gallery3d");
        localArrayList.add("com.jrdcom.android.gallery3d.app.Gallery");
        localArrayList.add("com.tct.gallery3d");
        localArrayList.add("com.google.android.apps.photos");
        localArrayList.add("com.google.android.apps.plus.phone.ConversationListActivity");
        localArrayList.add("com.google.android.gallery3d");
        localArrayList.add("com.android.gallery3d");
        localArrayList.add("com.android.gallery");
        localArrayList.add("com.android.gallery3d.app.GalleryActivity");
        localArrayList.add("com.cooliris.media");
        localArrayList.add("com.cooliris.media.Gallery");
        localArrayList.add("com.motorola.blurgallery");
        localArrayList.add("com.motorola.motgallery");
        localArrayList.add("com.motorola.cgallery.Dashboard");
        localArrayList.add("com.cooliris.media.RenderView");
        localArrayList.add("com.htc.album");
        localArrayList.add("com.lenovo.scg");
        localArrayList.add("com.sonyericsson.gallery");
        localArrayList.add("com.sonyericsson.album");
        localArrayList.add("com.sec.android.gallery3d");
        localArrayList.add("com.motorola.gallery");
        localArrayList.add("com.baidu.gallery3D.media");
        localArrayList.add("com.oppo.cooliris.media");
        localArrayList.add("com.miui.gallery");
        localArrayList.add("com.htc.album.AlbumTabSwitchActivity");
        localArrayList.add("com.android.camera.GalleryPicker");
        localArrayList.add("com.alensw.PicFolder");
        localArrayList.add("com.oppo.gallery3d.app.Gallery");
        localArrayList.add("zte.com.cn.gallery3d.app.Gallery");
        localArrayList.add("com.gionee.gallery");
        localArrayList.add("com.baidu.gallery3d.app.Gallery");
        localArrayList.add("com.asus.ephoto");
        localArrayList.add("com.meizu.media.gallery");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.GALLERY.getValue());
        localArrayList.clear();
        localArrayList.add("com.android.settings");
        localArrayList.add("com.android.settings.MiuiSettings");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.SETTING.getValue());
        localArrayList.clear();
        localArrayList.add("com.tclmarket");
        localArrayList.add("com.android.vending.AssetBrowserActivity");
        localArrayList.add("com.tencent.assistant");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.MARKET.getValue());
        localArrayList.clear();
        localArrayList.add("com.android.email.activity.Welcome");
        localArrayList.add("com.htc.android.mail.MailListTab");
        localArrayList.add("com.htc.android.mail.MultipleActivitiesMain");
        localArrayList.add("com.motorola.blur.email.mailbox.ViewFolderActivity");
        localArrayList.add("com.android.email.activity.EmailActivity");
        localArrayList.add("com.lge.email");
        localArrayList.add("com.google.android.gm.ConversationListActivityGmail");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.EMAIL.getValue());
        localArrayList.clear();
        localArrayList.add("com.google.android.maps.MapsActivity");
        localArrayList.add("com.baidu.BaiduMap");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.MAPS.getValue());
        localArrayList.clear();
        localArrayList.add("com.lewa.updater");
        localArrayList.add("com.android.updater.MainActivity");
        localArrayList.add("com.sonyericsson.updatecenter");
        localArrayList.add("com.oppo.ota.activity.HomeActivity");
        localArrayList.add("com.oppo.ota");
        localArrayList.add("com.lenovo.ota");
        localArrayList.add("com.yulong.android.ota");
        localArrayList.add("gn.com.android.update");
        localArrayList.add("com.mediatek.updatesystem");
        localArrayList.add("com.huawei.android.hwouc.ui.activities.MainEntranceActivity");
        localArrayList.add("com.lewa.updater");
        localArrayList.add("com.mediatek.GoogleOta");
        localArrayList.add("com.baidu.android.ota");
        localArrayList.add("com.lge.updatecenter");
        localArrayList.add("com.meizu.flyme.update");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.UPDATER.getValue());
        localArrayList.clear();
        localArrayList.add("com.android.providers.downloads.ui.DownloadList");
        localArrayList.add("com.android.providers.downloads.ui.DownloadsListTab");
        localArrayList.add("com.android.providers.downloads.ui.DownloadPagerActivity");
        localArrayList.add("com.android.providers.downloads.ui");
        localArrayList.add("com.android.providers.downloads");
        localArrayList.add("com.yulong.android.filebrowser.activity.FileLatestActivity");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.DOWNLOADS.getValue());
        localArrayList.clear();
        localArrayList.add("com.android.dialer");
        localArrayList.add("com.google.android.dialer");
        localArrayList.add("com.android.htcdialer");
        localArrayList.add("com.android.contacts.activities.TwelveKeyDialer");
        localArrayList.add("com.android.contacts.TwelveKeyDialer");
        localArrayList.add("com.android.phone");
        localArrayList.add("com.android.contacts.activities.DialtactsActivity");
        localArrayList.add("com.android.providers.telephony");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.PHONE.getValue());
        localArrayList.clear();
        localArrayList.add("com.android.contacts");
        localArrayList.add("com.google.android.contacts");
        localArrayList.add("com.android.contacts.activities.PeopleActivity");
        localArrayList.add("com.android.contacts.DialtactsContactsEntryActivity");
        localArrayList.add("com.google.android.syncadapters.contacts");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.CONTACTS.getValue());
        localArrayList.clear();
        localArrayList.add("com.google.android.browser");
        localArrayList.add("com.android.browser");
        localArrayList.add("com.android.chrome");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.BROWSER.getValue());
        localArrayList.clear();
        localArrayList.add("com.google.android.talk");
        localArrayList.add("com.android.contacts.activities.MessageActivity");
        localArrayList.add("com.android.mms");
        localArrayList.add("com.google.android.mms");
        matchLoaclApps(paramContext, localList, localArrayList, AppType.MMS.getValue());
        localArrayList.clear();
    }

    private static void matchLoaclApps(Context paramContext, List<ResolveInfo> localList, List<String> collectList,
            String paramString) {
        List<ActivityInfo> localArrayList = new ArrayList<>();
        Iterator<String> collectIterator = collectList.iterator();
        while (collectIterator.hasNext()) {
            String collect = (String) collectIterator.next();
            Iterator<ResolveInfo> resolveInfos = localList.iterator();
            while (resolveInfos.hasNext()) {
                ResolveInfo localResolveInfo = (ResolveInfo) resolveInfos.next();
                if ((collect.equals(localResolveInfo.activityInfo.name))
                        || (collect.equals(localResolveInfo.activityInfo.packageName)))
                    if (!localArrayList.contains(localResolveInfo.activityInfo)) {
                        localArrayList.add(localResolveInfo.activityInfo);
                    }
            }
        }
        sPackageMap.put(paramString, localArrayList);
    }

}
