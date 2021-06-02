package com.rsdp.siviorluni.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.rsdp.siviorluni.MyApplication;
import com.rsdp.siviorluni.R;
import com.rsdp.siviorluni.databinding.ActivityAboutUsBinding;
import com.rsdp.siviorluni.util.AppLangSessionManager;

import java.util.Locale;

import static com.rsdp.siviorluni.util.Utils.PrivacyPolicyUrl;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding binding;
    AppLangSessionManager appLangSessionManager;
    private MyApplication myApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);

        appLangSessionManager = new AppLangSessionManager(AboutUsActivity.this);
        setLocale(appLangSessionManager.getLanguage());

       // AdsUtils.showGoogleBannerAd(AboutUsActivity.this,binding.adView);

        InitializeAds();


        binding.RLPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(AboutUsActivity.this,WebviewAcitivity.class);
                i.putExtra("URL",PrivacyPolicyUrl);
                i.putExtra("Title", getResources().getString(R.string.prv_policy));
                startActivity(i);
            }
        });
        binding.imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.RLWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.website_tag)));
                    startActivity(browserIntent);
                }catch (Exception ex){
                    Log.d("WebsiteTag", "onClick: "+ex);
                }

            }
        });




        binding.RLEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=AboutUsActivity.this.getResources().getString(R.string.email_tag);

                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={email};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));

//                try{
//                    Intent intent = new Intent (Intent.ACTION_SEND , Uri.parse("mailto:" +email));
//                    intent.setType("text/plain");
//                    startActivity(intent);
//                }catch(Exception e){
//                    Log.d("KKKKKKK",e+"");
//                }

            }
        });

    }


    // ads server by badr //

    private void InitializeAds(){
        myApplication = (MyApplication)getApplicationContext();
        RelativeLayout view = findViewById(R.id.banner);
        myApplication.setBannerAd(view);
    }




    public void setLocale(String lang) {


        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);



    }

}
