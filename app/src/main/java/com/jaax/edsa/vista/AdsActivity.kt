package com.jaax.edsa.vista

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.jaax.edsa.R

class AdsActivity: AppCompatActivity() {
    private lateinit var adview4: AdView
    private lateinit var adview5: AdView
    private lateinit var adview6: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)

        adview4 = findViewById(R.id.adview4)
        adview5 = findViewById(R.id.adview5)
        adview6 = findViewById(R.id.adview6)

        MobileAds.initialize(this@AdsActivity)
        val adRequest = AdRequest.Builder().build()
        adview4.loadAd( adRequest )
        adview5.loadAd( adRequest )
        adview6.loadAd( adRequest )
    }
    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}