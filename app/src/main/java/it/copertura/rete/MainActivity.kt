package it.copertura.rete

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true          // localStorage: copertura settimanale
            settings.allowFileAccess = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.textZoom = 100                    // ignora il font di sistema
            setBackgroundColor(0xFFF7F6F4.toInt())
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        WebView.setWebContentsDebuggingEnabled(true)   // debug da chrome://inspect

        Ponte.collega(this, web)
        web.addJavascriptInterface(Ponte, "GpsNativo")
        web.loadUrl("file:///android_asset/web/index.html")
        setContentView(web)

        chiediPermessi()
    }

    private fun chiediPermessi() {
        val mancanti = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) mancanti += Manifest.permission.ACCESS_FINE_LOCATION
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) mancanti += Manifest.permission.POST_NOTIFICATIONS
        if (mancanti.isNotEmpty()) ActivityCompat.requestPermissions(this, mancanti.toTypedArray(), 1)
    }

    override fun onDestroy() {
        Ponte.scollega()
        super.onDestroy()
    }
}
