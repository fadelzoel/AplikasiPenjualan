# Aplikasi Penjualan (Java Desktop POS)

Aplikasi Point of Sales (POS) berbasis desktop yang dibangun menggunakan **Java 21 (Swing)**, **SQLite**, dan **MVC Architecture**. Aplikasi ini mencakup manajemen inventaris, pelanggan, transaksi penjualan, serta pembuatan nota dalam format PDF.

##  Fitur Utama
* **Manajemen Data:** CRUD untuk Master Barang dan Master Pelanggan.
* **Transaksi Penjualan:** Keranjang belanja interaktif dengan perhitungan subtotal dan total otomatis.
* **Riwayat & Cetak:** Laporan riwayat penjualan lengkap dengan fitur cetak PDF (Nota/Bon).
* **Modern UI:** Tampilan antarmuka kustom menggunakan Java Swing tanpa library UI eksternal.

---

##  Highlight Teknis: Offline Licensing & Anti-Tamper System
*(Kenapa proyek ini bukan sekadar aplikasi CRUD biasa)*

Tantangan terbesar dari aplikasi desktop komersial adalah distribusi. Jika kita menaruh *secret key* di dalam aplikasi, maka aplikasi tersebut bisa di-decompile dan di-crack. 

Untuk mengatasi hal ini, saya membangun sistem lisensi kustom dari nol (tanpa library pihak ketiga) dengan spesifikasi berikut:

### 1. Hardware Locking (HWID)
Kunci aktivasi tidak bisa dipakai di sembarang komputer. Aplikasi mengambil **MAC Address** dari perangkat keras, melakukan *hashing* (SHA-256), dan mengubahnya menjadi **Hardware ID (HWID)** unik untuk setiap klien.

### 2. Asymmetric Cryptography (Ed25519)
Sistem lisensi ini menggunakan **Ed25519 Public-Key Cryptography** (fitur bawaan Java 15+).
* **Client App** hanya menyimpan **Public Key**. Public key ini hanya bisa **memverifikasi** lisensi, tapi secara matematis **tidak bisa** digunakan untuk membuat lisensi baru.
* **Admin Tool** (dijalankan oleh developer) menyimpan **Private Key**. Developer menggunakan private key ini untuk menandatangani HWID klien dan menghasilkan kode aktivasi yang unik.
* Artinya: Meskipun hacker berhasil melakukan *reverse engineering* pada file `.jar` aplikasi, mereka tetap tidak bisa membuat kode aktivasi sendiri.

### 3. Anti-Tamper Trial System (Multi-Layer Storage)
Sistem masa uji coba (trial) dirancang agar sangat sulit untuk dilewati:
* **Triple Redundancy:** Data trial disimpan di 3 tempat sekaligus (Folder aplikasi, Folder tersembunyi `~/.bisnisfadel`, dan **Windows Registry** via `java.util.prefs`). Jika user menghapus salah satu, sistem akan memulihkannya dari salinan lain.
* **HMAC-SHA256 Validation:** File trial diberi tanda tangan digital (HMAC). Jika user mencoba mengubah tanggal secara manual menggunakan Notepad, tanda tangan akan rusak dan aplikasi langsung mengunci diri.
* **Anti-Clock Tampering:** Aplikasi mencatat kapan terakhir kali dibuka. Jika user mencoba memundurkan jam komputer untuk memperpanjang masa trial, aplikasi akan mendeteksinya dan menganggap trial telah berakhir.
* **HWID Binding:** File trial tidak bisa di-copy-paste ke komputer lain karena terikat dengan HWID.

---

##  Teknologi yang Digunakan
* **Bahasa:** Java 21 (Memanfaatkan fitur `record` dan Ed25519)
* **GUI:** Java Swing (Custom Themed)
* **Database:** SQLite (JDBC)
* **PDF Generator:** iTextPDF
* **Arsitektur:** MVC (Model-View-Controller) + DAO Pattern

##  Struktur Proyek
* `model/` : Representasi data (Barang, Pelanggan, Penjualan).
* `view/` : Antarmuka pengguna (Swing).
* `controller/` : Logika bisnis yang menghubungkan View dan DAO.
* `dao/` : Database Access Object (Query SQLite).
* `util/` : Utility (Format Rupiah, PDF Printer, License Manager).

##  Cara Menjalankan
1. Clone repository ini.
2. Buka project di NetBeans atau IntelliJ IDEA.
3. Tambahkan library `.jar` yang berada di folder `libs` ke dalam *Project Classpath*.
4. Jalankan file `Main.java`.