package com.happy.happysdk;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;


public class AdManager {
    private static AdManager mAdManager;
    private Activity mAct;
    private int mAdIndex=0;
    private final String TAG="AdManager";
    private HashMap<Integer,AdConfig> ad_configs=new HashMap<Integer,AdConfig>();

    public static AdManager getInstance() {
        if (mAdManager == null) {
            mAdManager = new AdManager();
        }
        return mAdManager;
    }

    public void init(Activity act)
    {
        mAct=act;
        loadJson();
        //Log.e(TAG,getAndroidId());
        //UnityManager.getInstance().init(mAct);
        //new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("72B53935998177842601FAEA87926E9F"));
        AdMobManager.getInstance().init(mAct,getAdConfig(AdType.Admob));

    }

    public void logGoogleAdId(){
           new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String adid = AdvertisingIdClient.getGoogleAdId(mAct);
                    Log.e(TAG,adid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getAndroidId(){
        try {
            return android.provider.Settings.Secure.getString(
                    mAct.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        }catch (Throwable e){
            return "";
        }
    }

    protected AdConfig getAdConfig(int adType){
        if(this.ad_configs.containsKey(adType)){
            return this.ad_configs.get(adType);
        }
        return null;
    }

    private void loadJson(){
        try {
            InputStreamReader isr = new InputStreamReader(mAct.getAssets().open("AdConfig.json"),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONArray adJson = new JSONArray(builder.toString());//builder读取了JSON中的数据。
            for (int adType = 0; adType < adJson.length(); adType++) {
                JSONObject adObject=adJson.getJSONObject(adType);
                AdConfig adConfig=new AdConfig();
                adConfig.adType=adType;
                String bannerId=adObject.getString("bannerId");
                if(bannerId.isEmpty()==false){
                    adConfig.bannerId=bannerId;
                }else {
                    Log.e(TAG,"bannerId为空");
                }
                String insertId=adObject.getString("insertId");
                if(insertId.isEmpty()==false){
                    adConfig.insertId=insertId;
                }else{
                    Log.e(TAG,"insertId为空");
                }
                JSONArray rewardIds = adObject.getJSONArray("rewardId");//从JSONObject中取出数组对象
                if(rewardIds.length()<=0){
                    Log.e(TAG,"rewardIds为空");
                }
                for (int i = 0; i < rewardIds.length(); i++) {
                    String rewardId = rewardIds.getString(i);//取出数组中的字符串
                    adConfig.rewardIds.add(rewardId);
                }
                this.ad_configs.put(adType,adConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBanner(){
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UnityManager.getInstance().showBanner();
                AdMobManager.getInstance().showBanner();
            }
        });

    }

    public void closeBanner(){
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UnityManager.getInstance().closeBanner();
                AdMobManager.getInstance().closeBanner();
            }
        });
    }

    public void showInterVideo(int index,AdResultListen adListen)
    {
        this.mAdIndex=index;
        adListen.onAdSucess();
//        AdMobManager.getInstance().resetFinded();
//        mAct.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                AdMobManager.getInstance().showInterVideo(mAdIndex,adListen);
//            }
//        });
    }

    public void showInter()
    {
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UnityManager.getInstance().showInterstitial();
                AdMobManager.getInstance().showInterstitial();
            }
        });
    }
    /*可优化为根据权重优化播放哪个平台的广告*/
    public void showReward(int index,AdResultListen adListen)
    {
        //adListen.onAdSucess();
        this.mAdIndex=index;
        //UnityManager.getInstance().resetFinded();
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UnityManager.getInstance().showReward(mAdIndex,adListen);
                AdMobManager.getInstance().startShowReward(mAdIndex,adListen);
            }
        });
    }

    public void showMessage(final String message) {
        mAct.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(mAct, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void exitGame()
    {
        mAct.finish();
        System.exit(0);
    }
}
