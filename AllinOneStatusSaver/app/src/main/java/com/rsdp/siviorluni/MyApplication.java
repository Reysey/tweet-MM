package com.rsdp.siviorluni;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rsdp.siviorluni.util.AppLangSessionManager;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

public class MyApplication extends Application {

    private final String TAG = MyApplication.class.getSimpleName();

    private InterstitialAd mInterstitialAd;
    private AdView adView;

    private com.facebook.ads.InterstitialAd mInterstitialFacebook ;
    private com.facebook.ads.AdView mAdViewFacebook ;
    DatabaseReference databaseads;
    RelativeLayout bannerView;

    String checkNetworkAd;
    String AdMob = "admob"; // don't change this
    String Facebook = "facebook"; // don't change this


    AppLangSessionManager appLangSessionManager;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        appLangSessionManager = new AppLangSessionManager(getApplicationContext());
        setLocale(appLangSessionManager.getLanguage());

// ads server by badr //


        databaseads= FirebaseDatabase.getInstance().getReference();
        databaseads.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                checkNetworkAd = dataSnapshot.child("network-ad").getValue().toString(); //Network Ad
                String bannerID = dataSnapshot.child("ban-admob").getValue().toString(); // Banner Admob
                String InterstitialID = dataSnapshot.child("int-admob").getValue().toString(); //Interstitial Admob
                String bannerIDf = dataSnapshot.child("ban-facebook").getValue().toString();  //Banner  Facebook
                String InterstitialIDf = dataSnapshot.child("int-facebook").getValue().toString();  //Interstitial Facebook

                if (checkNetworkAd.equalsIgnoreCase(AdMob)){
                    //Call you AdRequest AdMob :
                    try {
                        buildAdRequestAdMob(bannerID,InterstitialID);
                    }catch (Exception e){
                        setLog("Somthing wrong about AdMob Request cause : "+e);
                    }
                }else if (checkNetworkAd.equalsIgnoreCase(Facebook)){
                    //Call you AdRequest Facebook :
                    try {
                        RequestFacebookAd(bannerIDf,InterstitialIDf);
                    }catch (Exception e){
                        setLog("Somthing wrong about Facebook Request cause : "+e);
                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    public void setLocale(String lang) {
        if (lang.equals("")){
            lang="en";
        }
        Log.d("Support",lang+"");
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


    }


    //Build AdRequest AdMob : Banner + Interstitial
    private void buildAdRequestAdMob(String banner, String interstitial){

        //Build Banner
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

                bannerView.addView(adView);
            }

           // @Override
            //public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            //}
        });

        //Build Interstitial :
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(interstitial);
        final AdRequest adRequestInterstitial =  new AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequestInterstitial);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                setLog("Interstitial Loaded");
            }


            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                setLog("Interstitial onAdFailedToLoad");

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(adRequestInterstitial);
                setLog("Interstitial onAdClosed");
            }
        });




    }



    public void RequestFacebookAd(String banner ,String Interstitial){
        // Initialize the Audience Network SDK :
        AudienceNetworkAds.initialize(this);

        //Load Facebook BannerAd :
        mAdViewFacebook = new com.facebook.ads.AdView(this, banner, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        mAdViewFacebook.loadAd();
        mAdViewFacebook.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                setLog("Banner Facebook on Failed Loaded");
            }

            @Override
            public void onAdLoaded(Ad ad) {
                setLog("Banner Facebook on Loaded");

                if (bannerView != null){
                    setBannerAd(bannerView);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {}
            @Override
            public void onLoggingImpression(Ad ad) {}
        });

        //Load Facebook InterstitialAd :
        mInterstitialFacebook = new com.facebook.ads.InterstitialAd(this, Interstitial);
        mInterstitialFacebook.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad){
                mInterstitialFacebook.loadAd();
            }
            @Override
            public void onInterstitialDismissed(Ad ad){
                mInterstitialFacebook.loadAd();
            }
            @Override
            public void onError(Ad ad, AdError adError){
                setLog("Interstitial Facebook on Failed Loaded");
            }
            @Override
            public void onAdClicked(Ad ad){}
            @Override
            public void onLoggingImpression(Ad ad){}
            @Override
            public void onAdLoaded(Ad ad) {
                setLog("Interstitial Facebook Loaded");
            }
        });
        mInterstitialFacebook.loadAd();
    }


    public void showInterstitial() {
        // ghadi ndir wahd if, hit ila makanch online dik l9ima dial checkNetworkd ghadi tkon null oghadi tcrusha l app
        if (checkNetworkAd != null){
            if (checkNetworkAd.equalsIgnoreCase(AdMob)){
                // Show Interstitial AdMob After Loading
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

            }else if (checkNetworkAd.equalsIgnoreCase(Facebook)){
                // Show Interstitial Facebook After Loading
                if (mInterstitialFacebook != null && mInterstitialFacebook.isAdLoaded()) {
                    mInterstitialFacebook.show();
                }
            }

        }

    }

    public void setBannerAd(RelativeLayout r){

        if (checkNetworkAd != null){
            if (checkNetworkAd.equalsIgnoreCase(AdMob)){

                if (adView == null) {
                    return;
                }
                if (adView.getParent() != null){
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                r.removeAllViews();
                r.addView(adView);
                r.invalidate();

            }else if (checkNetworkAd.equalsIgnoreCase(Facebook)){
                if (mAdViewFacebook == null) {
                    return;
                }
                if (mAdViewFacebook.getParent() != null){
                    ((ViewGroup) mAdViewFacebook.getParent()).removeView(mAdViewFacebook);
                }
                r.removeAllViews();
                r.addView(mAdViewFacebook);
                r.invalidate();
            }
        }


    }

    private void setLog(String log){
        Log.d("myAd",log);
    }


}
