package it.copertura.rete

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.android.gms.location.*

/**
 * Servizio in primo piano: il rilievo continua a schermo spento e con l'app in background,
 * come richiede un giro di sorveglianza in auto.
 */
class ServizioRilievo : Service() {

    companion object {
        const val AVVIA = "avvia"
        const val FERMA = "ferma"
        private const val CANALE = "rilievo"
    }

    private lateinit var client: FusedLocationProviderClient

    private val callback = object : LocationCallback() {
        override fun onLocationResult(r: LocationResult) {
            val l = r.lastLocation ?: return
            Ponte.fix(l.latitude, l.longitude, l.accuracy, if (l.hasSpeed()) l.speed else 0f)
        }

        override fun onLocationAvailability(a: LocationAvailability) {
            if (!a.isLocationAvailable) Ponte.errore("Segnale GPS non disponibile")
        }
    }

    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == FERMA) {
            client.removeLocationUpdates(callback)
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(1, notifica())

        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(1000)
            .setMinUpdateDistanceMeters(3f)
            .setWaitForAccurateFix(false)
            .build()

        try {
            client.requestLocationUpdates(req, callback, mainLooper)
        } catch (e: SecurityException) {
            Ponte.errore("Permesso posizione negato")
            stopSelf()
        }
        return START_STICKY
    }

    private fun notifica(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26 && nm.getNotificationChannel(CANALE) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CANALE, "Rilievo in corso", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val apri = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val b = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, CANALE)
                else @Suppress("DEPRECATION") Notification.Builder(this)
        return b.setContentTitle("Rilievo copertura attivo")
            .setContentText("Registrazione dei tratti sorvegliati in corso")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(apri)
            .setOngoing(true)
            .build()
    }

    override fun onBind(i: Intent?): IBinder? = null

    override fun onDestroy() {
        client.removeLocationUpdates(callback)
        super.onDestroy()
    }
}
