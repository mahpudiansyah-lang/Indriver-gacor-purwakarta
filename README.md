# INDrivER GACOR – Purwakarta (versi awal)

Aplikasi Android native sederhana untuk membantu driver memantau beberapa area, melihat saran waktu umum berdasarkan jam perangkat, membuka lokasi di Google Maps, dan mencatat pendapatan/order.

## Batasan penting
- Aplikasi ini **tidak terhubung ke sistem inDrive**, tidak membaca order/penumpang, dan tidak bisa menjamin order atau membuat akun menjadi "gacor".
- Zona yang ditampilkan adalah titik pantau awal yang perlu diverifikasi sendiri di lapangan. Tidak ada data permintaan penumpang real-time.
- Saran waktu hanya aturan umum berdasarkan jam lokal perangkat, bukan prediksi berbasis data historis.
- Catatan pendapatan hanya tersimpan selama aplikasi berjalan pada versi prototipe ini.

## Membuat APK
1. Instal Android Studio versi stabil.
2. Ekstrak folder proyek ini.
3. Android Studio → **Open** → pilih folder `IndriverGacorPurwakarta`.
4. Tunggu Gradle Sync dan instal Android SDK 35 jika diminta.
5. Pilih **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
6. APK debug biasanya berada di `app/build/outputs/apk/debug/app-debug.apk`.

## Pengembangan berikutnya
Untuk peta zona yang benar-benar berbasis kondisi, perlu sumber data yang sah (misalnya data order milik driver yang dimasukkan sendiri, survei lapangan, atau API peta/cuaca). Jangan menggunakan bot, scraping, atau akses tidak resmi ke aplikasi inDrive.
