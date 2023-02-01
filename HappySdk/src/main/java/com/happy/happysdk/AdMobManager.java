package com.happy.happysdk;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import java.util.ArrayList;


public class AdMobManager {
    private static AdMobManager mAdManager;
    private Activity mAct;
    private AdView mAdView;
    private RewardedInterstitialAd mRewardedInterstitialAd;
    private InterstitialAd mInterstitialAd;
    private ArrayList<RewardedAd> mRewardedAd=new ArrayList<>();
    private boolean mIsLoadAllRewardAd=false;
    private ArrayList<Integer> finded_type=new ArrayList<Integer>();
    private AdConfig ad_config=new AdConfig();

    private final String TAG="AdMobManager";

    public static AdMobManager getInstance() {
        if (mAdManager == null) {
            mAdManager = new AdMobManager();
        }
        return mAdManager;
    }

    public void init(Activity act,AdConfig adConfig)
    {
        //new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("72B53935998177842601FAEA87926E9F"));
        mAct=act;
        this.ad_config=adConfig;
        MobileAds.initialize(mAct, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                //loadBanner();
                  loadAllReward();
                  loadInterstitial();
//                loadInsertVideoAd();
            }
        });
        //ResponseInfo.getAdapterResponse()
        //showBanner();
    }

    public void resetFinded()
    {
        this.finded_type.clear();
    }

    //先写死对齐屏幕下方，有时间再优化可配置
    void loadBanner(){
        if( ad_config!=null ){
            mAdView = new AdView(mAct);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ad_config.bannerId);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            //调节广告起始位置的左边距
            //layoutParams.setMarginStart(0);
            layoutParams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            mAct.addContentView(mAdView,layoutParams);
            mAdView.setVisibility(View.GONE);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.e(TAG,"banner onAdLoaded");
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    Log.e(TAG,"banner onAdFailedToLoad:"+adError.getMessage());
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }
    }

    public void showBanner(){
        Log.e(TAG,"Showbanner");
        if(mAdView!=null){
            mAdView.setVisibility(View.VISIBLE);
        }

    }

    public void closeBanner(){
        Log.e(TAG,"closeBanner");
        if(mAdView!=null) {
            mAdView.setVisibility(View.GONE);
        }
    }

    public void loadInsertVideoAd() {
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(mAct, ad_config.insertVideoId,
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        mRewardedInterstitialAd = ad;
                        mRewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            /** Called when the ad failed to show full screen content. */
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.i(TAG, "onAdFailedToShowFullScreenContent");
                            }

                            /** Called when ad showed the full screen content. */
                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.i(TAG, "onAdShowedFullScreenContent");
                                super.onAdShowedFullScreenContent();
                                mRewardedInterstitialAd=null;
                                loadInsertVideoAd();
                            }

                            /** Called when full screen content is dismissed. */
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.i(TAG, "onAdDismissedFullScreenContent");
                            }
                        });
                        Log.e(TAG, "RewardedInterstitialAd onAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mRewardedInterstitialAd=null;
                        Log.e(TAG, "RewardedInterstitialAd onAdFailedToLoad:"+loadAdError.toString());
                    }
                });
    }

    public void showInterVideo(int videoType, AdResultListen adListen){
        if(mRewardedInterstitialAd!=null)
        {
            mRewardedInterstitialAd.show(mAct, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    adListen.onAdSucess();
                    Log.e(TAG,"showReward-onUserEarnedReward");
                }
            });
        }else
        {
            loadInsertVideoAd();
        }
    }


    void loadAllReward()
    {
        //AdSettings.addTestDevice("455ebcc3-e1ca-463f-9f3a-a79f86b22503");
        if(mIsLoadAllRewardAd==false){
            mIsLoadAllRewardAd=true;
            for(int i=0; i<ad_config.rewardIds.size(); i++)
            {
                mRewardedAd.add(null);
                loadReward(i);
            }
        }
    }

    private void loadInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(mAct,ad_config.insertId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "Interstitial-onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                loadInterstitial();
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
        });

    }

    private void loadReward(int videoType)
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(mAct, ad_config.rewardIds.get(videoType), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                mRewardedAd.set(videoType,rewardedAd);
//                String id=rewardedAd.getResponseInfo().getResponseId();
//                Log.d(TAG,id);
//                Log.d(TAG,rewardedAd.getResponseInfo().getMediationAdapterClassName());
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        mRewardedAd.set(videoType,null);
                        loadReward(videoType);
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG,"onAdClicked");
                    }
                });
                Log.d(TAG,"loadReward-onAdLoaded:"+videoType);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mRewardedAd.set(videoType,null);
                Log.e(TAG,"loadReward-onAdFailedToLoad:"+videoType);
            }
        });
    }

    public void showInterstitial()
    {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(mAct);
        } else {
            loadInterstitial();
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    public boolean startShowReward(int videoType, AdResultListen adListen){
        if(videoType>=ad_config.rewardIds.size()){
            Log.e(TAG,"广告索引越界，不存在:"+videoType);
            return false;
        }
        this.resetFinded();
        return this.showReward(videoType,adListen);
    }
    /* 展示一个激励视频广告,videoType:要展示的视频广告ID的索引，adListen：本次广告展示的结果回调,返回本次视频广告是否可以展示*/
    private boolean showReward(int videoType, AdResultListen adListen)
    {
        if(mRewardedAd.get(videoType)!=null)
        {
            mRewardedAd.get(videoType).show(mAct, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    adListen.onAdSucess();
                    Log.e(TAG,"showReward-onUserEarnedReward");
                }
            });
            return true;
        }else
        {
            loadReward(videoType);
            return showOtherVideo(videoType,adListen);
        }
    }

    private boolean showOtherVideo(int videoType,AdResultListen adListen)
    {
        finded_type.add((videoType));
        boolean isSuc=false;
        ArrayList<Integer> noFindId=new ArrayList<Integer>();
        for(int i=0; i<ad_config.rewardIds.size(); i++)
        {
            if(!finded_type.contains(i))
            {
                noFindId.add(i);
            }
        }
        //随机
        int size=noFindId.size();
        if(size>0)
        {
            int findId=noFindId.get((int) Math.floor(Math.random()*size));
            showReward(findId,adListen);
        }else
        {
            adListen.onAdFailed();
            showMessage("Rewarded ads are loading, please wait...");
        }

        return isSuc;
    }

    public void AdResult(boolean isSuccess) {
        Log.e(TAG,"AdResult");
        if(isSuccess) {
//            Cocos2dxHelper.runOnGLThread(new Runnable() {
//                @Override
//                public void run() {
//                    Cocos2dxJavascriptJavaBridge.evalString("cc.APK.adResult(1)");
//                }
//            });

        } else {
//            Cocos2dxHelper.runOnGLThread(new Runnable() {
//                @Override
//                public void run() {
//                    Cocos2dxJavascriptJavaBridge.evalString("cc.APK.adResult(0)");
//                }
//            });
        }
    }

//    public void onResume() {
//        if(mRewardedVideoAd!=null)
//        mRewardedVideoAd.resume(mAct);
//
//    }
//
//    public void onPause() {
//        if(mRewardedVideoAd!=null)
//        mRewardedVideoAd.pause(mAct);
//
//    }
//
//    public void onDestroy() {
//        if(mRewardedVideoAd!=null)
//        mRewardedVideoAd.destroy(mAct);
//
//    }
    public void showMessage(final String message) {
        mAct.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(mAct, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
//    /*动态申请权限*/
//    private List<String> mNeedRequestPMSList = new ArrayList<>();
//    private boolean checkAndRequestPermissions() {
//        mNeedRequestPMSList = new ArrayList<>();
//        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mAct, Manifest.permission.SEND_SMS)) {
//            mNeedRequestPMSList.add(Manifest.permission.SEND_SMS);
//            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
//            if (ActivityCompat.shouldShowRequestPermissionRationale(mAct,
//                    Manifest.permission.SEND_SMS)) {
//                //showMessage("You have disabled the permission to send text messages, please turn it on again");
//            }
//        }
//        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mAct, Manifest.permission.RECEIVE_SMS)) {
//            mNeedRequestPMSList.add(Manifest.permission.RECEIVE_SMS);
//        }
//        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mAct, Manifest.permission.READ_SMS)) {
//            mNeedRequestPMSList.add(Manifest.permission.READ_SMS);
//        }
//        if (0 == mNeedRequestPMSList.size()) {
//            return true;
//        } else {
//            mAct.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String[] temp = new String[mNeedRequestPMSList.size()];
//                    mNeedRequestPMSList.toArray(temp);
//                    ActivityCompat.requestPermissions(mAct, temp, 1);
//                }
//            });
//        }
//        return false;
//    }
}
