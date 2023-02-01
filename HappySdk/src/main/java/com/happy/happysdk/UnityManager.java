//package com.happy.happysdk;
//
//import android.app.Activity;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import com.unity3d.ads.IUnityAdsInitializationListener;
//import com.unity3d.ads.IUnityAdsLoadListener;
//import com.unity3d.ads.IUnityAdsShowListener;
//import com.unity3d.ads.UnityAds;
//import com.unity3d.services.banners.BannerView;
//import com.unity3d.services.banners.UnityBannerSize;
//
//import java.util.ArrayList;
//
//
//public class UnityManager {
//    private static UnityManager mUnityManager;
//    private Activity mAct;
//
//    private BannerView mBannerView;
//    private ArrayList<String> mRewardId=new ArrayList<String>();
//    private boolean mIsLoadAllRewardAd=false;
//    private ArrayList<Integer> finded_type=new ArrayList<Integer>();
//    private boolean isAdd=false;
//
//    private final String TAG="UnityManager";
//    private AdConfig ad_config=new AdConfig();
//
//    public static UnityManager getInstance() {
//        if (mUnityManager == null) {
//            mUnityManager = new UnityManager();
//        }
//        return mUnityManager;
//    }
//    public void init(Activity act,AdConfig adConfig)
//    {
//        mAct=act;
//        this.ad_config=adConfig;
//        UnityAds.initialize(mAct, ad_config.gameID, false, new IUnityAdsInitializationListener() {
//            @Override
//            public void onInitializationComplete() {
//                loadBanner();
//                loadAllReward();
//            }
//
//            @Override
//            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
//                Log.e(TAG,s);
//            }
//        });
//        //ResponseInfo.getAdapterResponse()
//        //showBanner();
//    }
//
//    public void resetFinded()
//    {
//        this.finded_type.clear();
//    }
//
//    void loadBanner(){
//        mBannerView=new BannerView(mAct,ad_config.bannerId, UnityBannerSize.getDynamicSize(mAct));
//        mBannerView.load();
//
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
//        //调节广告起始位置的左边距
//        //layoutParams.setMarginStart(0);
//        layoutParams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
//        mAct.addContentView(mBannerView,layoutParams);
//
//        mBannerView.setVisibility(View.GONE);
//
//        mBannerView.setListener(new BannerView.Listener() {
//            @Override
//            public void onBannerLoaded(BannerView bannerAdView) {
//                super.onBannerLoaded(bannerAdView);
//                //ApkManager.getInstance().addAdLoadedNum(1);
//                Log.e(TAG,"onBannerLoaded");
//            }
//        });
//    }
//
//    public void showBanner(){
//        Log.e(TAG,"Showbanner");
//        if(mBannerView!=null){
//            mBannerView.setVisibility(View.VISIBLE);
//        }else {
//            loadBanner();
//        }
//
//
//    }
//
//    public void closeBanner(){
//        Log.e(TAG,"closeBanner");
//        if(mBannerView!=null){
//            mBannerView.setVisibility(View.GONE);
//        }
//    }
//
//
//
//
//    void loadAllReward()
//    {
//        //AdSettings.addTestDevice("455ebcc3-e1ca-463f-9f3a-a79f86b22503");
//        if(mIsLoadAllRewardAd==false){
//            mIsLoadAllRewardAd=true;
//            for(int i=0; i<ad_config.rewardIds.size(); i++)
//            {
//                loadReward(i);
//            }
//        }
//    }
//
//    private void loadInterstitial()
//    {
//
//
//    }
//
//    private void loadReward(int videoType)
//    {
//        UnityAds.load(ad_config.rewardIds.get(videoType), new IUnityAdsLoadListener() {
//            @Override
//            public void onUnityAdsAdLoaded(String s) {
//                mRewardId.set(videoType,ad_config.rewardIds.get(videoType));
//                if(isAdd==false){
//                    //ApkManager.getInstance().addAdLoadedNum(1);
//                    isAdd=true;
//                }
//
//            }
//
//            @Override
//            public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {
//                mRewardId.set(videoType,null);
//            }
//        });
//    }
//
//    public void showInterstitial()
//    {
//
//    }
//
//    public boolean startShowReward(int videoType, AdResultListen adListen){
//        if(videoType>=ad_config.rewardIds.size()){
//            Log.e(TAG,"广告索引越界，不存在:"+videoType);
//            return false;
//        }
//        this.resetFinded();
//        return this.showReward(videoType,adListen);
//    }
//
//    public boolean showReward(int videoType, AdResultListen adListen)
//    {
//        if(mRewardId.get(videoType)!=null)
//        {
//            Log.e(TAG,"video:"+videoType);
//            UnityAds.show(mAct, mRewardId.get(videoType), new IUnityAdsShowListener() {
//                @Override
//                public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
//                    Log.e(TAG,"onUnityAdsShowFailure:"+s1);
//                    mRewardId.set(videoType,null);
//                    loadReward(videoType);
//                    adListen.onAdFailed();
//                    showMessage("Rewarded video display failed, please try again later");
//                }
//
//                @Override
//                public void onUnityAdsShowStart(String s) {
//                    mRewardId.set(videoType,null);
//                    loadReward(videoType);
//                }
//
//                @Override
//                public void onUnityAdsShowClick(String s) {
//
//                }
//
//                @Override
//                public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
//                    Log.e(TAG,"onUnityAdsShowComplete:"+unityAdsShowCompletionState);
//                    if (unityAdsShowCompletionState.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
//                        // Reward the user for watching the ad to completion
//                        adListen.onAdSucess();
//                    } else {
//                        // Do not reward the user for skipping the ad
//                        adListen.onAdFailed();
//                    }
//                }
//            });
//            return true;
//        }else
//        {
//            loadReward(videoType);
//            Log.e(TAG,"showReward:"+videoType+"为null,寻找其他广告位");
//            return showOtherVideo(videoType,adListen);
//        }
//    }
//
//    private boolean showOtherVideo(int videoType,AdResultListen adListen)
//    {
//        finded_type.add((videoType));
//        boolean isSuc=false;
//        ArrayList<Integer> noFindId=new ArrayList<Integer>();
//        for(int i=0; i<ad_config.rewardIds.size(); i++)
//        {
//            if(!finded_type.contains(i) && mRewardId.get(i)!=null)
//            {
//                noFindId.add(i);
//            }
//        }
//        //随机
//        int size=noFindId.size();
//        if(size>0)
//        {
//            int findId=noFindId.get((int) Math.floor(Math.random()*size));
//            showReward(findId,adListen);
//        }else
//        {
//            adListen.onAdFailed();
//            showMessage("Rewarded ads are loading, please wait...");
//        }
//
//        return isSuc;
//    }
//
//    public void showMessage(final String message) {
//        mAct.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                Toast.makeText(mAct, message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
////    /*动态申请权限*/
////    private List<String> mNeedRequestPMSList = new ArrayList<>();
////    private boolean checkAndRequestPermissions() {
////        mNeedRequestPMSList = new ArrayList<>();
////        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mAct, Manifest.permission.SEND_SMS)) {
////            mNeedRequestPMSList.add(Manifest.permission.SEND_SMS);
////            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
////            if (ActivityCompat.shouldShowRequestPermissionRationale(mAct,
////                    Manifest.permission.SEND_SMS)) {
////                //showMessage("You have disabled the permission to send text messages, please turn it on again");
////            }
////        }
////        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mAct, Manifest.permission.RECEIVE_SMS)) {
////            mNeedRequestPMSList.add(Manifest.permission.RECEIVE_SMS);
////        }
////        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mAct, Manifest.permission.READ_SMS)) {
////            mNeedRequestPMSList.add(Manifest.permission.READ_SMS);
////        }
////        if (0 == mNeedRequestPMSList.size()) {
////            return true;
////        } else {
////            mAct.runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    String[] temp = new String[mNeedRequestPMSList.size()];
////                    mNeedRequestPMSList.toArray(temp);
////                    ActivityCompat.requestPermissions(mAct, temp, 1);
////                }
////            });
////        }
////        return false;
////    }
//}
