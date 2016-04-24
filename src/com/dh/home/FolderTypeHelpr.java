package com.dh.home;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DemonHunter on 2016/1/29. v3.1
 */
public class FolderTypeHelpr {

    public final static String AUTO = "auto";

    private static Map<String, FolderType> map = new HashMap<>();


    public static FolderType getFolderTypeByPackage(String packageName) {
        // 初始化数据
        if (map.isEmpty()) {
            init();
        }
        return map.get(packageName);
    }

    private static void init() {
        map.put("cmb.pb", FolderType.LifeAmusement);
        map.put("cn.jj", FolderType.Game);
        map.put("cn.kuwo.player", FolderType.AudioVideo);
        map.put("com.achievo.vipshop", FolderType.LifeAmusement);
        map.put("com.alcatel.music5.china", FolderType.AudioVideo);
        map.put("com.android.chrome", FolderType.ChatCommunication);
        map.put("com.android.email", FolderType.LearnOffice);
        map.put("com.android.jrdfota", FolderType.SystemTools);
        map.put("com.android.settings", FolderType.SystemTools);
        map.put("com.android.stk", FolderType.SystemTools);
        map.put("com.android.videoeditor", FolderType.AudioVideo);
        map.put("com.autoconnectwifi.app", FolderType.SystemTools);
        map.put("com.autonavi.minimap", FolderType.SystemTools);
        map.put("com.baidu.BaiduMap", FolderType.SystemTools);
        map.put("com.baidu.browser.apps", FolderType.SystemTools);
        map.put("com.baidu.homework", FolderType.LearnOffice);
        map.put("com.baidu.input", FolderType.ChatCommunication);
        map.put("com.baidu.lbs.waimai", FolderType.LifeAmusement);
        map.put("com.baidu.netdisk", FolderType.SystemTools);
        map.put("com.baidu.searchbox", FolderType.SystemTools);
        map.put("com.baidu.video", FolderType.AudioVideo);
        map.put("com.blizzard.wtcg.hearthstone", FolderType.Game);
        map.put("com.book2345.reader", FolderType.NewsReading);
        map.put("com.brianbaek.popstar", FolderType.Game);
        map.put("com.changba", FolderType.AudioVideo);
        map.put("com.chaozh.iReader", FolderType.NewsReading);
        map.put("com.chaozh.iReaderFree", FolderType.NewsReading);
        map.put("com.chinatelecom.bestpayclient", FolderType.LifeAmusement);
        map.put("com.classics.game.goldminer", FolderType.Game);
        map.put("com.cleanmaster.mguard_cn", FolderType.SystemTools);
        map.put("com.ct.client", FolderType.LifeAmusement);
        map.put("com.dewmobile.kuaiya", FolderType.SystemTools);
        map.put("com.dianxinos.optimizer.channel", FolderType.SystemTools);
        map.put("com.duowan.groundhog.mctools", FolderType.SystemTools);
        map.put("com.eg.android.AlipayGphone", FolderType.LifeAmusement);
        map.put("com.example.chinesechess", FolderType.Game);
        map.put("com.facebook.katana", FolderType.ChatCommunication);
        map.put("com.fenbi.android.solar", FolderType.LearnOffice);
        map.put("com.fxo.pol", FolderType.LearnOffice);
        map.put("com.gameley.runningman3.anzhi", FolderType.Game);
        map.put("com.gameloft.android.ANMP.GloftDMCN", FolderType.Game);
        map.put("com.github.shadowsocks", FolderType.Game);
        map.put("com.google.android.apps.docs", FolderType.LearnOffice);
        map.put("com.google.android.apps.docs.editors.docs", FolderType.LearnOffice);
        map.put("com.google.android.apps.docs.editors.sheets", FolderType.LearnOffice);
        map.put("com.google.android.apps.docs.editors.slides", FolderType.LearnOffice);
        map.put("com.google.android.apps.fitness", FolderType.LifeAmusement);
        map.put("com.google.android.apps.magazines", FolderType.NewsReading);
        map.put("com.google.android.apps.messaging", FolderType.ChatCommunication);
        map.put("com.google.android.apps.plus", FolderType.ChatCommunication);
        map.put("com.google.android.gm", FolderType.LearnOffice);
        map.put("com.google.android.gms", FolderType.SystemTools);
        map.put("com.google.android.googlequicksearchbox", FolderType.SystemTools);
        map.put("com.google.android.launcher", FolderType.Theme);
        map.put("com.google.android.maps.mytracks", FolderType.AudioVideo);
        map.put("com.google.android.music", FolderType.AudioVideo);
        map.put("com.google.android.play.games", FolderType.Game);
        map.put("com.google.android.videos", FolderType.AudioVideo);
        map.put("com.google.android.youtube", FolderType.AudioVideo);
        map.put("com.google.zxing.client.android", FolderType.SystemTools);
        map.put("com.halfbrick.fruitninja", FolderType.Game);
        map.put("com.happyelements.AndroidAnimal", FolderType.Game);
        map.put("com.hotbody.fitzero", FolderType.LifeAmusement);
        map.put("com.huati", FolderType.LifeAmusement);
        map.put("com.huluxia.gemetools", FolderType.LifeAmusement);
        map.put("com.hunantv.imgo.activity", FolderType.AudioVideo);
        map.put("com.idreamsky.seer", FolderType.Game);
        map.put("com.iflytek.inputmethod", FolderType.SystemTools);
        map.put("com.imangi.templerun2", FolderType.Game);
        map.put("com.immomo.momo", FolderType.ChatCommunication);
        map.put("com.izhangxin.ddz.android", FolderType.Game);
        map.put("com.jingdong.app.mall", FolderType.LifeAmusement);
        map.put("com.joym.xiongdakuaipao", FolderType.Game);
        map.put("com.jrdcom.alcatelhelp", FolderType.SystemTools);
        map.put("com.jrdcom.compass", FolderType.SystemTools);
        map.put("com.jrdcom.setupwizard", FolderType.SystemTools);
        map.put("com.jrdcom.tethering", FolderType.LearnOffice);
        map.put("com.jrdcom.weather", FolderType.LifeAmusement);
        map.put("com.jrdcom.wifidisplay", FolderType.SystemTools);
        map.put("com.jrdcom.wifitransfer", FolderType.SystemTools);
        map.put("com.kiloo.subwaysurf", FolderType.Game);
        map.put("com.kugou.android", FolderType.AudioVideo);
        map.put("com.letv.android.client", FolderType.AudioVideo);
        map.put("com.ludashi.benchmark", FolderType.SystemTools);
        map.put("com.mediatek.StkSelection", FolderType.SystemTools);
        map.put("com.mediatek.todos", FolderType.LearnOffice);
        map.put("com.meitu.meiyancamera", FolderType.LifeAmusement);
        map.put("com.MobileTicket", FolderType.LifeAmusement);
        map.put("com.mojang.minecraftpe", FolderType.Game);
        map.put("com.mojang.minecraftpe_5", FolderType.Game);
        map.put("com.moji.mjweather", FolderType.SystemTools);
        map.put("com.moxiu.launcher", FolderType.Theme);
        map.put("com.mt.mtxx.mtxx", FolderType.LifeAmusement);
        map.put("com.nd.android.pandahome2", FolderType.Theme);
        map.put("com.outfit7.mytalkingtomfree", FolderType.Game);
        map.put("com.outfit7.talkingtom2", FolderType.Game);
        map.put("com.playrisedigital.ttrs", FolderType.Game);
        map.put("com.pokercity.bydrqp", FolderType.Game);
        map.put("com.pokercity.yzddz.tcl", FolderType.Game);
        map.put("com.pook.race.ch0961", FolderType.Game);
        map.put("com.popcap.pvzthird", FolderType.Game);
        map.put("com.popcap.pvzthirdwvga", FolderType.Game);
        map.put("com.pplive.androidphone", FolderType.AudioVideo);
        map.put("com.qihoo.appstore", FolderType.SystemTools);
        map.put("com.qihoo.browser", FolderType.SystemTools);
        map.put("com.qihoo.video", FolderType.AudioVideo);
        map.put("com.qihoo360.mobilesafe", FolderType.SystemTools);
        map.put("com.qiyi.video", FolderType.AudioVideo);
        map.put("com.qvod.player", FolderType.AudioVideo);
        map.put("com.qzone", FolderType.LifeAmusement);
        map.put("com.rolocule.projectz", FolderType.Game);
        map.put("com.sankuai.meituan", FolderType.LifeAmusement);
        map.put("com.sdax.sz", FolderType.SystemTools);
        map.put("com.sec.chaton", FolderType.ChatCommunication);
        map.put("com.shoujiduoduo.ringtone", FolderType.SystemTools);
        map.put("com.shuqi.controller", FolderType.NewsReading);
        map.put("com.sina.weibo", FolderType.LifeAmusement);
        map.put("com.smile.gifmaker", FolderType.LifeAmusement);
        map.put("com.snda.wifilocating", FolderType.SystemTools);
        map.put("com.soco.veggies4_tcl", FolderType.Game);
        map.put("com.sohu.inputmethod.sogou", FolderType.SystemTools);
        map.put("com.sohu.newsclient", FolderType.NewsReading);
        map.put("com.sohu.sohuvideo", FolderType.AudioVideo);
        map.put("com.ss.android.article.news", FolderType.NewsReading);
        map.put("com.ss.android.article.news", FolderType.NewsReading);
        map.put("com.storm.smart", FolderType.AudioVideo);
        map.put("com.taobao.taobao", FolderType.LifeAmusement);
        map.put("com.tcl.account.china", FolderType.SystemTools);
        map.put("com.tcl.gc.hd", FolderType.Game);
        map.put("com.tcl.live", FolderType.SystemTools);
        map.put("com.tcl.pcsuite", FolderType.SystemTools);
        map.put("com.tct.screenrecorder", FolderType.LifeAmusement);
        map.put("com.tct.weather", FolderType.LifeAmusement);
        map.put("com.tencent.android.qqdownloader", FolderType.SystemTools);
        map.put("com.tencent.feiji", FolderType.Game);
        map.put("com.tencent.game.rhythmmaster", FolderType.Game);
        map.put("com.tencent.karaoke", FolderType.AudioVideo);
        map.put("com.tencent.mm", FolderType.ChatCommunication);
        map.put("com.tencent.mobileqq", FolderType.ChatCommunication);
        map.put("com.tencent.mtt", FolderType.SystemTools);
        map.put("com.tencent.news", FolderType.NewsReading);
        map.put("com.tencent.pao", FolderType.Game);
        map.put("com.tencent.qqgame", FolderType.Game);
        map.put("com.tencent.qqlite", FolderType.ChatCommunication);
        map.put("com.tencent.qqlive", FolderType.AudioVideo);
        map.put("com.tencent.qqmusic", FolderType.AudioVideo);
        map.put("com.tencent.qqpim", FolderType.SystemTools);
        map.put("com.tencent.qqpimsecure", FolderType.SystemTools);
        map.put("com.tencent.tmgp.cf", FolderType.Game);
        map.put("com.tencent.tmgp.sgame", FolderType.Game);
        map.put("com.tianqi2345", FolderType.LifeAmusement);
        map.put("com.ting.mp3.android", FolderType.LifeAmusement);
        map.put("com.tudou.android", FolderType.AudioVideo);
        map.put("com.ubercab", FolderType.LifeAmusement);
        map.put("com.UCMobile", FolderType.SystemTools);
        map.put("com.wandoujia.phoenix2", FolderType.SystemTools);
        map.put("com.workivan.popstar", FolderType.Game);
        map.put("com.xiaoao.riskSnipe", FolderType.Game);
        map.put("com.yipiao", FolderType.LifeAmusement);
        map.put("com.yodo1tier1.yodo1_cmmm.SkiSafari", FolderType.Game);
        map.put("com.youku.phone", FolderType.AudioVideo);
        map.put("com.youloft.calendar", FolderType.SystemTools);
        map.put("deezer.android.app", FolderType.ChatCommunication);
        map.put("org.cocokube.skirt", FolderType.Game);
        map.put("org.keke.tv.vod", FolderType.AudioVideo);
        map.put("tv.pps.mobile", FolderType.AudioVideo);
        map.put("com.miui.mihome2", FolderType.Theme);
        map.put("com.example.android.apis", FolderType.SystemTools);
        map.put("com.android.customlocale2", FolderType.SystemTools);
        map.put("com.android.development_settings", FolderType.SystemTools);
        map.put("com.android.development", FolderType.SystemTools);
        map.put("com.dh.launcher3", FolderType.Theme);
        map.put("com.android.providers.downloads.ui", FolderType.SystemTools);
        map.put("com.cyanogenmod.filemanager", FolderType.LearnOffice);
        map.put("com.android.voicedialer", FolderType.SystemTools);
        map.put("com.android.gesture.builder", FolderType.SystemTools);
        map.put("com.genymotion.superuser", FolderType.SystemTools);
        map.put("com.android.quicksearchbox", FolderType.SystemTools);

    }



}
