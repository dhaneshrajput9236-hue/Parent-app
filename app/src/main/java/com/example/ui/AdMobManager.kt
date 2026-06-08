package com.example.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"
    
    // Provided AdMob configurations
    const val APP_ID = "ca-app-pub-3767503288694165~6284196148"
    const val BANNER_UNIT_ID = "ca-app-pub-3767503288694165/1314784965"
    const val INTERSTITIAL_UNIT_ID = "ca-app-pub-3767503288694165/9931641586"
    
    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    fun initialize(context: Context) {
        MobileAds.initialize(context) { status ->
            Log.d(TAG, "MobileAds initialized: $status")
            loadInterstitial(context.applicationContext)
        }
    }

    fun loadInterstitial(context: Context) {
        if (mInterstitialAd != null || isAdLoading) return
        
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Ad failed to load: ${adError.message}")
                    mInterstitialAd = null
                    isAdLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad loaded successfully.")
                    mInterstitialAd = interstitialAd
                    isAdLoading = false
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit = {}) {
        val ad = mInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad dismissed.")
                    mInterstitialAd = null
                    onAdDismissed()
                    // Pre-load the next one
                    loadInterstitial(activity.applicationContext)
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Ad failed to show: ${adError.message}")
                    mInterstitialAd = null
                    onAdDismissed()
                    loadInterstitial(activity.applicationContext)
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "Ad was not ready yet.")
            onAdDismissed()
            loadInterstitial(activity.applicationContext)
        }
    }
    
    fun isInterstitialReady(): Boolean = mInterstitialAd != null
}

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdMobManager.BANNER_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
