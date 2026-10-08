# BLE Tracking Device App 📡

Aplikasi Android Native modern berbasis **Jetpack Compose** untuk memindai (*scanning*), melacak kekuatan sinyal (*RSSI*), mengestimasi jarak, dan menyimpan riwayat lokasi perangkat **Bluetooth Low Energy (BLE)** di sekitar secara *real-time*.

---

## 📌 Fitur Utama

- 🔍 **Live BLE Scanning & Radar Animation**: Pemindaian perangkat BLE sekitar secara *real-time* dilengkapi indikator animasi radar yang responsif.
- 📊 **Signal Category & Distance Estimation**: Pengelompokan kualitas sinyal (*Sangat Kuat, Kuat, Sedang, Lemah, Sinyal Hilang*) dan kalkulasi perkiraan jarak dalam meter.
- 💾 **Riwayat Perangkat (Local Persistence)**: Penyimpanan otomatis perangkat terdeteksi ke database lokal Room sehingga riwayat perangkat dapat diperiksa kapan saja.
- 📱 **Detail Perangkat (Detail Device Screen)**: Informasi rinci mengenai perangkat seperti Nama, MAC Address, nilai RSSI (dBm), estimasi jarak (meter), kategori sinyal, serta timestamp terakhir terlihat.
- 🔍 **Real-time Search & Filter**: Pencarian instan berdasarkan Nama Perangkat atau MAC Address pada tab Live Scan maupun Riwayat.
- 🛡️ **Adaptive Bluetooth & Permission Management**: Penanganan izin lokasi dan Bluetooth runtime secara dinamis sesuai versi OS Android (Android 12+ / Android <12) serta Bottom Sheet pengaktifan Bluetooth.

---

## 🛠️ Tech Stack

- **Bahasa Pemrograman**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Arsitektur**: MVVM (Model-View-ViewModel) + Unidirectional Data Flow (UDF)
- **Dependency Injection**: [Hilt (Dagger-Hilt)](https://developer.android.com/training/dependency-injection/hilt-android)
- **Asynchronous & Reactive Programming**: Kotlin Coroutines & `StateFlow` / `Channel` (dengan *throttling/sampling* untuk performa pemindaian BLE yang lancar)
- **Database Lokal**: [Room Database](https://developer.android.com/training/data-storage/room) dengan [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html)
- **Navigasi**: Jetpack Compose Navigation

---

## 📐 Konsep Arsitektur & Desain

### 1. MVVM (Model-View-ViewModel)
Aplikasi ini menerapkan pola arsitektur **MVVM** untuk memisahkan logika bisnis, data, dan tampilan UI:
- **Model**: Entitas data (`BleDeviceEntity`, `BleDeviceHistoryEntity`), Data Access Object (DAO), dan `BleDeviceRepository` sebagai sumber data utama.
- **View**: Layar Compose deklaratif (`HomeScreen`, `DetailDeviceScreen`, `HistoryDeviceScreen`) yang hanya fokus merender UI berdasarkan state.
- **ViewModel**: `BleViewModel` mengelola komunikasi BLE, operasi database asynchronous, serta mempertahankan state aplikasi agar tahan terhadap *configuration change* (seperti rotasi layar).

### 2. UDF (Unidirectional Data Flow)
Aliran data pada aplikasi bergerak secara satu arah (UDF) untuk memastikan konsistensi UI dan meminimalkan *side-effect*:
- **State (`BleUiState`)**: `BleViewModel` memancarkan `StateFlow<BleUiState>` yang dibaca secara *read-only* oleh komponen UI.
- **Event (`BleUiEvent`)**: Interaksi pengguna di UI (seperti `StartScan`, `StopScan`, `OnSearchQueryChanged`, `DeleteHistoryItem`) dikirimkan kembali ke ViewModel dalam bentuk *Event* terdefinisi.

### 3. Dependency Injection (Hilt)
Pengelolaan dependensi dilakukan menggunakan **Hilt** melalui `AppModule` untuk memfasilitasi *decoupling*, *reusability*, dan kemudahan pengujian (*testability*):
- Menyediakan instance `BluetoothAdapter` dan `BluetoothLeScanner`.
- Menyediakan instance Room Database (`DatabaseBle`), DAO (`BleDeviceDao`, `BleDeviceHistoryDao`), dan `BleDeviceRepository`.

---

## 🤖 Catatan Penggunaan AI (AI Disclaimer)

> **Note**: Bantuan **Artificial Intelligence (AI)** dalam proyek ini digunakan secara spesifik untuk mempermudah dan mempercepat tugas-tugas pengembangan **layouting (UI Compose)** serta hal-hal yang bersifat **repetitif**, seperti:
> - Pembuatan struktur awal *Data Class / Entity* (`BleDeviceEntity`, `BleDeviceHistoryEntity`).
> - Pembuatan *boilerplate event action detail* dan komponen UI pendukung.
> - Penyusunan *layouting Compose* dan *preview composables*.
> - Penyusunan README.md.
