# Copertura Rete — versione Android (tablet)

Stesso rilievo della versione iOS, dentro una `WebView` con GPS nativo e servizio in
primo piano: **il giro continua a schermo spento e con l'app in background**.

Non c'e' un APK pronto in questa cartella: l'APK va compilato. Due strade.

---

## A. APK senza installare niente (GitHub Actions) — consigliata

1. Nel repo che hai gia' su GitHub, crea la cartella `android/` e caricaci il contenuto
   di questa cartella.
2. Metti i file del web dentro **`android/app/src/main/assets/web/`**
   (index.html, rete-grafo.js, support.js, ios-frame.jsx, manifest.webmanifest, icona.png:
   il contenuto della cartella `github/`).
3. Copia `.github-workflows-android.yml` nel repo come **`.github/workflows/android.yml`**.
4. Fai push. Vai su **Actions → APK → ultima run → Artifacts → CoperturaRete-apk**.
   Scarichi uno zip con l'APK dentro.

## B. Android Studio

1. Apri Android Studio → *Open* → seleziona la cartella `android`.
   Lascia che scarichi Gradle e l'SDK 34 quando lo propone.
2. Copia il web in `app/src/main/assets/web/` come al punto 2 sopra.
3. **Build → Build Bundle(s)/APK(s) → Build APK(s)**.
   L'APK esce in `app/build/outputs/apk/release/`.
4. Oppure collega il tablet in debug USB e premi *Run*.

Un'icona vera: Android Studio → tasto destro su `res` → *New → Image Asset*, e usa
`icona.png`. Senza questo passaggio serve almeno un `mipmap/ic_launcher`, altrimenti
la build si ferma: la via rapida e' sostituire nel manifest
`android:icon="@mipmap/ic_launcher"` con `android:icon="@android:drawable/ic_menu_mylocation"`.

---

## Installazione sul tablet

L'APK e' firmato con la chiave di debug, quindi non passa dal Play Store:
sul tablet → Impostazioni → Sicurezza → **Installa app sconosciute** per il gestore file
o per il browser da cui lo apri, poi tocca l'APK.

Al primo avvio concedi **Posizione: consenti sempre** e le notifiche (la notifica e'
quella che tiene vivo il rilievo in background — non va disattivata).
In Impostazioni → Batteria, imposta l'app come **non ottimizzata**: alcuni tablet
sospendono i servizi in background e il rilievo si interromperebbe.

## Dati

La copertura resta nella WebView del tablet (localStorage), divisa per settimana ISO.
Gli export CSV finiscono nella cartella Download del tablet.

## Aggiornamenti

Modificato il web, sostituisci `app/src/main/assets/web/` e ricompila
(o fai push, se usi Actions). Alza `versionCode` in `app/build.gradle.kts`
per installare sopra la versione precedente.
