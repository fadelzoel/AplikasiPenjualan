package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Lisensi:
 *  - Kunci aktivasi = tanda tangan digital Ed25519 atas Hardware ID.
 *    Aplikasi hanya menyimpan PUBLIC key, jadi kunci tidak bisa dibuat dari isi jar.
 *    Private key hanya ada di tool admin (LicenseAdmin).
 *  - Trial disimpan di 3 tempat sekaligus dan dicek mundur-nya tanggal sistem.
 *
 * Butuh Java 15+ (Ed25519 bawaan JDK).
 */
public final class LicenseManager {

    // Tempel public key hasil "LicenseAdmin gen" di sini (aman ikut di-distribusi).
    private static final String PUBLIC_KEY_B64 = "MCowBQYDK2VwAyEAC/b/4g6of47aTjmDSk3tEbaToj7mdMb2r64mflI+P1c=";

    // Hanya untuk mempersulit edit manual file trial. Ganti dengan string acak Anda.
    private static final String HMAC_KEY = "x8F2kL9pQ1zW4vB7";

    private static final int TRIAL_DAYS = 7;

    private static final Path APP_LICENSE = Paths.get("license.dat");
    private static final Path APP_TRIAL = Paths.get("trial.dat");
    private static final Path HOME_DIR = Paths.get(System.getProperty("user.home"), ".bisnisfadel");
    private static final Path HOME_LICENSE = HOME_DIR.resolve("lic.dat");
    private static final Path HOME_TRIAL = HOME_DIR.resolve("sys.cfg");
    private static final String PREF_NODE = "bisnisfadel";
    private static final String PREF_KEY = "t";

    private static String cachedHwid;

    private LicenseManager() {}

    // ===================== Hardware ID =====================

    public static String getHardwareID() {
        if (cachedHwid != null) return cachedHwid;
        String raw;
        try {
            List<String> macs = new ArrayList<>();
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isLoopback() || ni.isVirtual()) continue;
                byte[] mac = ni.getHardwareAddress();
                if (mac == null || mac.length != 6) continue;
                StringBuilder sb = new StringBuilder();
                for (byte b : mac) sb.append(String.format("%02X", b));
                macs.add(sb.toString());
            }
            Collections.sort(macs); // urutan tetap supaya ID stabil
            raw = macs.isEmpty()
                    ? System.getProperty("user.name") + "|" + System.getProperty("os.name")
                    + "|" + System.getenv("COMPUTERNAME")
                    : macs.get(0);
        } catch (Exception e) {
            raw = "FALLBACK|" + System.getProperty("user.name");
        }
        try {
            byte[] h = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (i == 4) sb.append('-');
                sb.append(String.format("%02X", h[i]));
            }
            cachedHwid = sb.toString();
        } catch (Exception e) {
            cachedHwid = "ERROR-HWID";
        }
        return cachedHwid;
    }

    // ===================== Aktivasi =====================

    /** Cek apakah kunci valid untuk komputer ini (tanpa menyimpan apa pun). */
    public static boolean isValidKey(String key) {
        return verify(getHardwareID(), key);
    }

    /** Valid -> simpan & kembalikan true. */
    public static boolean activate(String key) {
        if (!isValidKey(key)) return false;
        String clean = clean(key);
        writeFile(APP_LICENSE, clean);
        writeFile(HOME_LICENSE, clean);
        return true;
    }

    /** Kompatibilitas dengan ActivationView lama: simpan hasil aktivasi terakhir. */
    public static void saveActivation() { /* sudah disimpan oleh activate(); tidak perlu apa-apa */ }

    public static boolean isActivated() {
        for (Path p : new Path[]{APP_LICENSE, HOME_LICENSE}) {
            String key = readFile(p);
            if (key != null && verify(getHardwareID(), key)) return true;
        }
        return false;
    }

    private static boolean verify(String hwid, String key) {
        try {
            byte[] sig = Base64.getUrlDecoder().decode(clean(key));
            PublicKey pk = KeyFactory.getInstance("Ed25519")
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY_B64)));
            Signature s = Signature.getInstance("Ed25519");
            s.initVerify(pk);
            s.update(hwid.getBytes(StandardCharsets.UTF_8));
            return s.verify(sig);
        } catch (Exception e) {
            return false;
        }
    }

    /** DEV ONLY: pura-pura trial sudah dimulai N hari lalu. Hapus sebelum dijual. */
    public static void devSetTrialDaysAgo(int daysAgo) {
        LocalDate today = LocalDate.now();
        saveTrial(today.minusDays(daysAgo), today);
    }

    /** DEV ONLY: menjelaskan kenapa sebuah kode ditolak. Hapus sebelum dijual. */
    public static String diagnose(String key) {
        StringBuilder sb = new StringBuilder();
        String hwid = getHardwareID();
        String k = clean(key);
        sb.append("HWID di PC ini   : ").append(hwid).append('\n');
        sb.append("Panjang kode     : ").append(k.length()).append(" (harusnya 86)\n");

        if (PUBLIC_KEY_B64.startsWith("PASTE")) {
            return sb.append("MASALAH: PUBLIC_KEY_B64 belum diisi (masih placeholder).\n").toString();
        }
        try {
            KeyFactory.getInstance("Ed25519")
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY_B64)));
        } catch (Exception e) {
            return sb.append("MASALAH: PUBLIC_KEY_B64 tidak valid/terpotong: ").append(e).append('\n').toString();
        }
        try {
            byte[] sig = Base64.getUrlDecoder().decode(k);
            sb.append("Panjang signature: ").append(sig.length).append(" byte (harusnya 64)\n");
        } catch (Exception e) {
            return sb.append("MASALAH: kode bukan Base64 yang valid (ada karakter salah?).\n").toString();
        }
        boolean ok = verify(hwid, key);
        sb.append("Hasil verifikasi : ").append(ok).append('\n');
        if (!ok) {
            sb.append("Artinya: public key di LicenseManager BUKAN pasangan private.key yang dipakai "
                    + "untuk sign (paling sering terjadi kalau 'gen' dijalankan lebih dari sekali "
                    + "atau ada lebih dari satu private.key).\n");
        }
        return sb.toString();
    }

    private static String clean(String key) {
        return key == null ? "" : key.replaceAll("\\s", "");
    }

    // ===================== Trial =====================

    private record Trial(LocalDate start, LocalDate last) {}

    /**
     * @return 999 jika sudah aktivasi, sisa hari jika trial berjalan,
     *         -1 jika trial habis / data dimanipulasi / tanggal sistem dimundurkan.
     */
    public static int getTrialDaysLeft() {
        if (isActivated()) return 999;

        String[] records = {readFile(APP_TRIAL), readFile(HOME_TRIAL), readPref()};
        LocalDate today = LocalDate.now();
        LocalDate start = null, last = null;
        boolean found = false;

        for (String raw : records) {
            if (raw == null) continue;
            Trial t = parse(raw);
            if (t == null) return -1;                 // ada file tapi rusak/diedit
            found = true;
            if (start == null || t.start().isBefore(start)) start = t.start();
            if (last == null || t.last().isAfter(last)) last = t.last();
        }

        if (!found) { start = today; last = today; } // benar-benar pertama kali jalan
        if (today.isBefore(last)) return -1;          // jam sistem dimundurkan

        saveTrial(start, today);                      // pulihkan salinan yang hilang + update last-run

        long left = TRIAL_DAYS - ChronoUnit.DAYS.between(start, today);
        return left > 0 ? (int) left : -1;
    }

    private static Trial parse(String raw) {
        try {
            String[] p = raw.trim().split("\\|");
            if (p.length != 4) return null;
            String body = p[0] + "|" + p[1] + "|" + p[2];
            if (!p[3].equals(hmac(body))) return null;
            if (!p[2].equals(getHardwareID())) return null; // file disalin dari komputer lain
            return new Trial(LocalDate.parse(p[0]), LocalDate.parse(p[1]));
        } catch (Exception e) {
            return null;
        }
    }

    private static void saveTrial(LocalDate start, LocalDate last) {
        String body = start + "|" + last + "|" + getHardwareID();
        String record = body + "|" + hmac(body);
        writeFile(APP_TRIAL, record);
        writeFile(HOME_TRIAL, record);
        try {
            Preferences.userRoot().node(PREF_NODE).put(PREF_KEY, record);
        } catch (Exception ignored) { }
    }

    private static String readPref() {
        try {
            return Preferences.userRoot().node(PREF_NODE).get(PREF_KEY, null);
        } catch (Exception e) {
            return null;
        }
    }

    // ===================== Util =====================

    private static String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(HMAC_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return "ERROR";
        }
    }

    private static String readFile(Path p) {
        try {
            return Files.exists(p) ? Files.readString(p, StandardCharsets.UTF_8).trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static void writeFile(Path p, String content) {
        try {
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            Files.writeString(p, content, StandardCharsets.UTF_8);
        } catch (Exception ignored) { }
    }
}