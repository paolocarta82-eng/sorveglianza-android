package it.copertura.rete

import android.app.Activity
import android.content.Intent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import java.lang.ref.WeakReference

/**
 * Ponte fra la pagina e il GPS nativo.
 * La pagina chiama GpsNativo.avvia() / GpsNativo.ferma();
 * i fix tornano con window.pushFix(lat, lon, precisione, velocita).
 */
object Ponte {

    private var act: WeakReference<Activity>? = null
    private var web: WeakReference<WebView>? = null

    fun collega(a: Activity, w: WebView) { act = WeakReference(a); web = WeakReference(w) }
    fun scollega() { act = null; web = null }

    @JavascriptInterface
    fun avvia() {
        val a = act?.get() ?: return
        a.startForegroundService(Intent(a, ServizioRilievo::class.java).setAction(ServizioRilievo.AVVIA))
    }

    @JavascriptInterface
    fun ferma() {
        val a = act?.get() ?: return
        a.startService(Intent(a, ServizioRilievo::class.java).setAction(ServizioRilievo.FERMA))
    }

    fun fix(lat: Double, lon: Double, precisione: Float, velocita: Float) {
        val w = web?.get() ?: return
        val js = "window.pushFix && window.pushFix(%.7f,%.7f,%.1f,%.2f)"
            .format(java.util.Locale.US, lat, lon, precisione, velocita)
        w.post { w.evaluateJavascript(js, null) }
    }

    fun errore(testo: String) {
        val w = web?.get() ?: return
        val js = "window.pushGpsError && window.pushGpsError('" + testo.replace("'", "\\'") + "')"
        w.post { w.evaluateJavascript(js, null) }
    }
}
