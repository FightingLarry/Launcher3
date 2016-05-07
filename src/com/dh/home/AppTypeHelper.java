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

// v2.1
public class AppTypeHelper {

    private static final String QUERYINTENT_PHONE =
            "#Intent;action=android.intent.action.DIAL;launchFlags=0x10000000;end";
    public static final String QUERYINTENT_CONTACTS =
            "content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;end";
    private static final String QUERYINTENT_MMS =
            "#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;launchFlags=0x10000000;end";
    private static final String QUERYINTENT_BROWSER =
            "http://m.baidu.com/?from=1000228o#Intent;action=android.intent.action.VIEW;launchFlags=0x10000000;end";
    // AppType对应的本机应用信息
    private static Map<String, List<ActivityInfo>> sPackageMap = new HashMap<>();

    // 持久化Apptype和本机应用的对应关系，1对1
    public synchronized static void configSystemAppIcon(Context context) {

        // configSystemAppIcon只执行一次。
        if (AppTypePreference.getInstance(context).getHasConfigSystemAppIcon()) {
            return;
        }
        AppTypePreference.getInstance(context).saveHasConfigSystemAppIcon(true);

        /* v3.1 */
        List<String> insertedDefaultWorkspace = new ArrayList<>();

        AppType[] appTypes =
                {AppType.PHONE, AppType.CONTACTS, AppType.BROWSER, AppType.MMS, AppType.CLOCK, AppType.CALENDAR,
                        AppType.GALLERY, AppType.EMAIL, AppType.CALCULATOR, AppType.SETTING, AppType.CAMERA,
                        AppType.MUSIC};

        next: for (AppType appType : appTypes) {
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
                    // 没有找到匹配的应用，在我们筛选出来的包名里面选一个。
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
                        // 通过QueryInten找到了应用
                        infos.add(info);
                    }
                }
            } else {
                // 得到AppType对应的包名的List
                infos = matchLoaclAppsByAppType(context, appType.getValue());
            }
            if (infos != null && infos.size() > 0) {
                if (isQueryIntent) {
                    // 直接取第一个就行。
                    ActivityInfo activityInfo = infos.get(0);
                    saveToDb(context, appType.getValue(), activityInfo, insertedDefaultWorkspace);
                } else {
                    // 筛选出唯一的应用，系统应用优先。
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
                            // 如果是系统应用，直接持久化
                            saveToDb(context, appType.getValue(), info, insertedDefaultWorkspace);
                            continue next;
                        }
                    }
                    // 如果不是系统应用，找到list的第一个应用信息，持久化。
                    ActivityInfo activityInfo = infos.get(0);
                    saveToDb(context, appType.getValue(), activityInfo, insertedDefaultWorkspace);
                }
            }
        }

        // v3.1 文件夹类型
        // 获取手机上所有的应用信息。
        Intent localIntent = new Intent(Intent.ACTION_MAIN, null);
        localIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> localList = context.getPackageManager().queryIntentActivities(localIntent, 0);
        for (ResolveInfo resolveInfo : localList) {
            String packageName = resolveInfo.activityInfo.packageName;
            // 判断应用是否已经加入到WorkSpace，已经加入了，就不会加入到文件夹。
            if (!insertedDefaultWorkspace.contains(packageName)) {
                FolderType folderType = FolderTypeHelpr.getFolderTypeByPackage(packageName);
                if (folderType != null) {
                    saveToDb(context, folderType.getValue(), resolveInfo.activityInfo, insertedDefaultWorkspace,
                            AppTypeTable.ITEM_TYPE_FOLDER);
                }
            }
        }
    }

    private static void saveToDb(Context context, String appType, ActivityInfo activityInfo,
            List<String> insertedDefaultWorkspace, int itemType) {
        PackageManager packageManager = context.getPackageManager();
        AppTypeModel model = new AppTypeModel();
        model.appType = appType;
        model.packageName = activityInfo.packageName;
        model.className = activityInfo.name;
        model.title = activityInfo.loadLabel(packageManager).toString();
        model.itemType = itemType;
        AppTypeTable.save(context, model);
        if (itemType == AppTypeTable.ITEM_TYPE_APP) {
            insertedDefaultWorkspace.add(activityInfo.packageName);
        }
    }

    private static void saveToDb(Context context, String appType, ActivityInfo activityInfo,
            List<String> insertedDefaultWorkspace) {
        saveToDb(context, appType, activityInfo, insertedDefaultWorkspace, AppTypeTable.ITEM_TYPE_APP);
    }

    synchronized static List<ActivityInfo> matchLoaclAppsByAppType(Context context, String appType) {

        if (sPackageMap.isEmpty()) {
            matchLoaclApps(context);
        }

        List<ActivityInfo> list = new ArrayList<>();
        // 多个Apptype，用逗号隔开。
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
            // 非系统应用列表
            List<ActivityInfo> unSystemAppList = new ArrayList<ActivityInfo>();
            // 优先系统应用
            while (localIterator.hasNext()) {
                ResolveInfo localResolveInfo = localIterator.next();
                // 不是应用
                if ((localResolveInfo.activityInfo == null) || (localResolveInfo.activityInfo.applicationInfo == null)) {
                    continue;
                }
                // 判断是不是系统应用
                if ((ApplicationInfo.FLAG_SYSTEM & localResolveInfo.activityInfo.applicationInfo.flags) == 0) {
                    // 不是系统应用
                    unSystemAppList.add(localResolveInfo.activityInfo);
                    continue;
                }
                // 想要找的系统应用
                return localResolveInfo.activityInfo;
            }
            // 非系统应用找第一个。
            if (unSystemAppList != null && unSystemAppList.size() > 0) {
                return unSystemAppList.get(0);
            }
        } catch (URISyntaxException localURISyntaxException) {
            localURISyntaxException.printStackTrace();
        }

        return null;
    }

    // v2.1 start
    private static void matchLoaclApps(Context paramContext) {
        // 查找出本地所有应用
        Intent localIntent = new Intent(Intent.ACTION_MAIN, null);
        localIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> localList = paramContext.getPackageManager().queryIntentActivities(localIntent, 0);
        // 筛选市场上的应用。
        List<String> matchArrayList = new ArrayList<>();
        matchArrayList.add("com.tcl.mid.android.camera");
        matchArrayList.add("com.android.jrdcamera.CameraLauncher");
        matchArrayList.add("com.jrdcom.camera.CameraLauncher");
        matchArrayList.add("com.android.camera.Camera");
        matchArrayList.add("com.google.android.camera");
        matchArrayList.add("com.android.camera");
        matchArrayList.add("com.android.camera.CameraActivity");
        matchArrayList.add("com.miui.camera");
        matchArrayList.add("com.motorola.Camera.Camera");
        matchArrayList.add("com.sec.android.app.camera.Camera");
        matchArrayList.add("com.android.camera.CameraEntry");
        matchArrayList.add("com.android.camera.CameraLauncher");
        matchArrayList.add("com.sonyericsson.android.camera");
        matchArrayList.add("com.lge.camera");
        matchArrayList.add("com.android.lgecamera");
        matchArrayList.add("com.android.hwcamera");
        matchArrayList.add("com.mediatek.camera");
        matchArrayList.add("com.baidu.camera");
        matchArrayList.add("com.oppo.camera");
        matchArrayList.add("com.oppo.camera.CameraLauncher");
        matchArrayList.add("com.motorola.camera");
        matchArrayList.add("zte.com.cn.camera.Camera");
        matchArrayList.add("com.baidu.camera.CameraLauncher");
        matchArrayList.add("com.htc.camera");
        matchArrayList.add("com.asus.camera");
        matchArrayList.add("com.meizu.media.camera");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.CAMERA.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.jrdcom.music");
        matchArrayList.add("com.lewa.player");
        matchArrayList.add("com.lewa.player.activity.SplashActivity");
        matchArrayList.add("com.android.music.MusicBrowserActivity");
        matchArrayList.add("com.miui.player");
        matchArrayList.add("com.miui.music");
        matchArrayList.add("com.motorola.cmp");
        matchArrayList.add("com.motorola.motmusic");
        matchArrayList.add("com.motorola.blur.music");
        matchArrayList.add("com.sec.android.app.music");
        matchArrayList.add("com.lenovo.leos.lephone.music");
        matchArrayList.add("com.lenovo.music");
        matchArrayList.add("com.htc.music");
        matchArrayList.add("com.lge.music");
        matchArrayList.add("com.sonyericsson.music");
        matchArrayList.add("com.andrew.apollo");
        matchArrayList.add("com.android.mediacenter");
        matchArrayList.add("com.oppo.music");
        matchArrayList.add("com.airplayme.android.phone");
        matchArrayList.add("com.android.bbkmusic.MusicBrowserActivity");
        matchArrayList.add("com.gwsoft.imusic.controller");
        matchArrayList.add("com.google.android.music");
        matchArrayList.add("com.android.music");
        matchArrayList.add("com.android.bbkmusic");
        matchArrayList.add("com.meizu.musiconline");
        matchArrayList.add("com.meizu.media.music");
        matchArrayList.add("com.lewa.player.ui.outer.MusicMainEntryActivity");
        matchArrayList.add("com.oppo.music.MainListActivity");
        matchArrayList.add("com.smartisanos.music.activities.MusicMain");
        matchArrayList.add("com.coolpad.music");
        matchArrayList.add("com.ting.mp3.android");
        matchArrayList.add("com.android.strengthenmusic");
        matchArrayList.add("cn.zte.music.MusicBrowserActivity");
        matchArrayList.add("cn.nubia.music.preset");
        matchArrayList.add("com.baidu.musicplayer");
        matchArrayList.add("com.imusic.musicplayer");
        matchArrayList.add("com.asus.music");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.MUSIC.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.lenovomobile.deskclock");
        matchArrayList.add("com.tct.timetool");
        matchArrayList.add("com.lenovo.deskclock");
        matchArrayList.add("com.sec.android.app.clockpackage");
        matchArrayList.add("com.htc.android.worldclock.WorldClockTabControl");
        matchArrayList.add("com.lge.alarm");
        matchArrayList.add("com.lge.clock");
        matchArrayList.add("com.android.alarmclock.AlarmClock");
        matchArrayList.add("com.motorola.blur.alarmclock");
        matchArrayList.add("com.sonyericsson.organizer.deskclock.DeskClock");
        matchArrayList.add("com.sonyericsson.organizer.Organizer");
        matchArrayList.add("com.baidu.baiduclock");
        matchArrayList.add("zte.com.cn.alarmclock");
        matchArrayList.add("com.leadcore.clock");
        matchArrayList.add("com.android.BBKClock");
        matchArrayList.add("com.yulong.android.xtime");
        matchArrayList.add("com.android.alarmclock");
        matchArrayList.add("com.android.deskclock.DeskClock");
        matchArrayList.add("com.android.deskclock");
        matchArrayList.add("com.oppo.alarmclock.AlarmClock");
        matchArrayList.add("com.aurora.deskclock");
        matchArrayList.add("com.smartisanos.clock.activity.ClockActivity");
        matchArrayList.add("com.android.timemanager");
        matchArrayList.add("cn.nubia.deskclock.preset");
        matchArrayList.add("com.asus.deskclock");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.CLOCK.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.tct.calendar");
        matchArrayList.add("com.motorola.calendar");
        matchArrayList.add("com.lenovo.app.Calendar");
        matchArrayList.add("com.htc.calendar");
        matchArrayList.add("com.bbk.calendar.MainActivity");
        matchArrayList.add("com.yulong.android.calendar");
        matchArrayList.add("com.google.android.syncadapters.calendar");
        matchArrayList.add("com.android.providers.calendar");
        matchArrayList.add("com.lenovo.calendar.SplashActivity");
        matchArrayList.add("com.google.android.calendar");
        matchArrayList.add("com.android.calendar.AllInOneActivity");
        matchArrayList.add("com.android.calendar2");
        matchArrayList.add("com.android.calendar");
        matchArrayList.add("cn.nubia.calendar.preset");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.CALENDAR.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.google.android.calculator");
        matchArrayList.add("com.tct.calculator");
        matchArrayList.add("com.sec.android.app.calculator.Calculator");
        matchArrayList.add("com.sec.android.app.calculator");
        matchArrayList.add("com.sec.android.app.popupcalculator");
        matchArrayList.add("com.baidu.calculator2");
        matchArrayList.add("com.android.bbkcalculator.Calculator");
        matchArrayList.add("my.android.calc");
        matchArrayList.add("com.android.calculator2");
        matchArrayList.add("com.android.calculator");
        matchArrayList.add("com.smartisanos.calculator.Calculator");
        matchArrayList.add("cn.nubia.calculator2.preset");
        matchArrayList.add("com.htc.calculator");
        matchArrayList.add("com.asus.calculator");
        matchArrayList.add("com.lenovo.calculator");
        matchArrayList.add("com.meizu.flyme.calculator");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.CALCULATOR.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.lewa.gallery3d");
        matchArrayList.add("com.jrdcom.android.gallery3d.app.Gallery");
        matchArrayList.add("com.tct.gallery3d");
        matchArrayList.add("com.google.android.apps.photos");
        matchArrayList.add("com.google.android.apps.plus.phone.ConversationListActivity");
        matchArrayList.add("com.google.android.gallery3d");
        matchArrayList.add("com.android.gallery3d");
        matchArrayList.add("com.android.gallery");
        matchArrayList.add("com.android.gallery3d.app.GalleryActivity");
        matchArrayList.add("com.cooliris.media");
        matchArrayList.add("com.cooliris.media.Gallery");
        matchArrayList.add("com.motorola.blurgallery");
        matchArrayList.add("com.motorola.motgallery");
        matchArrayList.add("com.motorola.cgallery.Dashboard");
        matchArrayList.add("com.cooliris.media.RenderView");
        matchArrayList.add("com.htc.album");
        matchArrayList.add("com.lenovo.scg");
        matchArrayList.add("com.sonyericsson.gallery");
        matchArrayList.add("com.sonyericsson.album");
        matchArrayList.add("com.sec.android.gallery3d");
        matchArrayList.add("com.motorola.gallery");
        matchArrayList.add("com.baidu.gallery3D.media");
        matchArrayList.add("com.oppo.cooliris.media");
        matchArrayList.add("com.miui.gallery");
        matchArrayList.add("com.htc.album.AlbumTabSwitchActivity");
        matchArrayList.add("com.android.camera.GalleryPicker");
        matchArrayList.add("com.alensw.PicFolder");
        matchArrayList.add("com.oppo.gallery3d.app.Gallery");
        matchArrayList.add("zte.com.cn.gallery3d.app.Gallery");
        matchArrayList.add("com.gionee.gallery");
        matchArrayList.add("com.baidu.gallery3d.app.Gallery");
        matchArrayList.add("com.asus.ephoto");
        matchArrayList.add("com.meizu.media.gallery");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.GALLERY.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.android.settings");
        matchArrayList.add("com.android.settings.MiuiSettings");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.SETTING.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.tclmarket");
        matchArrayList.add("com.android.vending.AssetBrowserActivity");
        matchArrayList.add("com.tencent.assistant");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.MARKET.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.android.email.activity.Welcome");
        matchArrayList.add("com.htc.android.mail.MailListTab");
        matchArrayList.add("com.htc.android.mail.MultipleActivitiesMain");
        matchArrayList.add("com.motorola.blur.email.mailbox.ViewFolderActivity");
        matchArrayList.add("com.android.email.activity.EmailActivity");
        matchArrayList.add("com.lge.email");
        matchArrayList.add("com.google.android.gm.ConversationListActivityGmail");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.EMAIL.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.google.android.maps.MapsActivity");
        matchArrayList.add("com.baidu.BaiduMap");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.MAPS.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.lewa.updater");
        matchArrayList.add("com.android.updater.MainActivity");
        matchArrayList.add("com.sonyericsson.updatecenter");
        matchArrayList.add("com.oppo.ota.activity.HomeActivity");
        matchArrayList.add("com.oppo.ota");
        matchArrayList.add("com.lenovo.ota");
        matchArrayList.add("com.yulong.android.ota");
        matchArrayList.add("gn.com.android.update");
        matchArrayList.add("com.mediatek.updatesystem");
        matchArrayList.add("com.huawei.android.hwouc.ui.activities.MainEntranceActivity");
        matchArrayList.add("com.lewa.updater");
        matchArrayList.add("com.mediatek.GoogleOta");
        matchArrayList.add("com.baidu.android.ota");
        matchArrayList.add("com.lge.updatecenter");
        matchArrayList.add("com.meizu.flyme.update");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.UPDATER.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.android.providers.downloads.ui.DownloadList");
        matchArrayList.add("com.android.providers.downloads.ui.DownloadsListTab");
        matchArrayList.add("com.android.providers.downloads.ui.DownloadPagerActivity");
        matchArrayList.add("com.android.providers.downloads.ui");
        matchArrayList.add("com.android.providers.downloads");
        matchArrayList.add("com.yulong.android.filebrowser.activity.FileLatestActivity");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.DOWNLOADS.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.android.dialer");
        matchArrayList.add("com.google.android.dialer");
        matchArrayList.add("com.android.htcdialer");
        matchArrayList.add("com.android.contacts.activities.TwelveKeyDialer");
        matchArrayList.add("com.android.contacts.TwelveKeyDialer");
        matchArrayList.add("com.android.phone");
        matchArrayList.add("com.android.contacts.activities.DialtactsActivity");
        matchArrayList.add("com.android.providers.telephony");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.PHONE.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.android.contacts");
        matchArrayList.add("com.google.android.contacts");
        matchArrayList.add("com.android.contacts.activities.PeopleActivity");
        matchArrayList.add("com.android.contacts.DialtactsContactsEntryActivity");
        matchArrayList.add("com.google.android.syncadapters.contacts");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.CONTACTS.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.google.android.browser");
        matchArrayList.add("com.android.browser");
        matchArrayList.add("com.android.chrome");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.BROWSER.getValue());
        matchArrayList.clear();
        matchArrayList.add("com.google.android.talk");
        matchArrayList.add("com.android.contacts.activities.MessageActivity");
        matchArrayList.add("com.android.mms");
        matchArrayList.add("com.google.android.mms");
        matchLoaclApps(paramContext, localList, matchArrayList, AppType.MMS.getValue());
        matchArrayList.clear();
    }

    /**
     * @param paramContext
     * @param localList 手机上所有应用
     * @param matchArrayList 应用类型对应的市场上的应用
     * @param appType 应用类型
     */
    private static void matchLoaclApps(Context paramContext, List<ResolveInfo> localList, List<String> matchArrayList,
            String appType) {
        // 用于保存匹配的应用信息。
        List<ActivityInfo> localArrayList = new ArrayList<>();
        Iterator<String> collectIterator = matchArrayList.iterator();
        // 市场上的应用
        while (collectIterator.hasNext()) {
            String collect = collectIterator.next();
            Iterator<ResolveInfo> resolveInfos = localList.iterator();
            // 本机应用
            while (resolveInfos.hasNext()) {
                // 本机的一个应用信息
                ResolveInfo localResolveInfo = resolveInfos.next();
                // className，packageName
                if ((collect.equals(localResolveInfo.activityInfo.name))
                        || (collect.equals(localResolveInfo.activityInfo.packageName)))
                    if (!localArrayList.contains(localResolveInfo.activityInfo)) {
                        localArrayList.add(localResolveInfo.activityInfo);
                    }
            }
        }
        sPackageMap.put(appType, localArrayList);
    }

}
