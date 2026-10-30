/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author noroi
 */

import dao.PelangganDAO;
import model.Pelanggan;
import view.PelangganView;

import javax.swing.*;
import java.util.List;

public class PelangganController {
    private final PelangganView view;
    private final PelangganDAO dao = new PelangganDAO();

    public PelangganController(PelangganView view) { this.view = view; }

    public List<Pelanggan> loadData(String cari) {
        return dao.findAll().stream()
                .filter(p -> cari.isBlank()
                        || p.kode().toLowerCase().contains(cari)
                        || p.nama().toLowerCase().contains(cari)
                        || (p.alamat() != null && p.alamat().toLowerCase().contains(cari))
                        || (p.noHp() != null && p.noHp().toLowerCase().contains(cari)))
                .toList();
    }

    public void simpan(String nama, String alamat, String noHp) {
        if (nama.isBlank()) { warn("Nama pelanggan harus diisi."); return; }
        if (!validNoHp(noHp)) { warn("No. HP hanya boleh berisi angka, spasi, +, dan -."); return; }
        String kode = dao.nextKode();
        dao.insert(kode, nama, alamat, noHp);
        info("Pelanggan berhasil disimpan.\n\nKode: " + kode);
        view.clearForm();
        view.refresh();
    }

    public void edit(String kode, String nama, String alamat, String noHp) {
        if (kode == null) { warn("Pilih pelanggan yang ingin diedit."); return; }
        if (nama.isBlank()) { warn("Nama pelanggan harus diisi."); return; }
        if (!validNoHp(noHp)) { warn("No. HP hanya boleh berisi angka, spasi, +, dan -."); return; }
        dao.update(kode, nama, alamat, noHp);
        info("Data pelanggan berhasil diperbarui.");
        view.clearForm();
        view.refresh();
    }

    public void hapus(String kode, String nama) {
        if (kode == null) { warn("Pilih pelanggan yang ingin dihapus."); return; }
        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus pelanggan?\n\n" + kode + " - " + nama,
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        dao.delete(kode);
        info("Pelanggan berhasil dihapus.");
        view.clearForm();
        view.refresh();
    }

    private boolean validNoHp(String s) {
        return s.chars().allMatch(ch -> "0123456789+- ".indexOf(ch) >= 0);
    }
    private void warn(String m) { JOptionPane.showMessageDialog(null, m, "Peringatan", JOptionPane.WARNING_MESSAGE); }
    private void info(String m) { JOptionPane.showMessageDialog(null, m, "Berhasil", JOptionPane.INFORMATION_MESSAGE); }
}
