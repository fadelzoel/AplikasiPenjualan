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


import dao.BarangDAO;
import model.Barang;
import view.BarangView;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class BarangController {
    private final BarangView view;
    private final BarangDAO dao = new BarangDAO();

    public BarangController(BarangView view) { 
        this.view = view; 
    }

    public List<Barang> loadData(String cari) {
        return dao.findAll().stream()
                .filter(b -> cari.isBlank()
                        || b.kode().toLowerCase().contains(cari.toLowerCase())
                        || b.nama().toLowerCase().contains(cari.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void simpan(String kode, String nama, String hargaStr) {
        if (kode.isBlank() || nama.isBlank() || hargaStr.isBlank()) {
            warn("Semua data harus diisi."); return;
        }
        double harga;
        try { harga = Double.parseDouble(hargaStr); }
        catch (NumberFormatException e) { warn("Harga harus berupa angka."); return; }
        if (harga <= 0) { warn("Harga harus lebih dari 0."); return; }

        try {
            dao.insert(kode, nama, harga);
            info("Barang berhasil disimpan.");
            view.clearForm();
            view.refresh();
        } catch (BarangDAO.DuplicateKeyException e) {
            warn("Kode barang sudah digunakan.");
        }
    }

    public void edit(String kodeLama, String kode, String nama, String hargaStr) {
        if (kodeLama == null) { warn("Pilih barang yang ingin diedit."); return; }
        if (kode.isBlank() || nama.isBlank() || hargaStr.isBlank()) {
            warn("Semua data harus diisi."); return;
        }
        double harga;
        try { harga = Double.parseDouble(hargaStr); }
        catch (NumberFormatException e) { warn("Harga harus berupa angka."); return; }
        if (harga <= 0) { warn("Harga harus lebih dari 0."); return; }

        try {
            dao.update(kodeLama, kode, nama, harga);
            info("Data barang berhasil diperbarui.");
            view.clearForm();
            view.refresh();
        } catch (BarangDAO.DuplicateKeyException e) {
            warn("Kode barang sudah digunakan.");
        }
    }

    public void hapus(String kode, String nama) {
        if (kode == null) { warn("Pilih barang yang ingin dihapus."); return; }
        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus barang?\n\n" + kode + " - " + nama,
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        dao.delete(kode);
        info("Barang berhasil dihapus.");
        view.clearForm();
        view.refresh();
    }

    private void warn(String m) { 
        JOptionPane.showMessageDialog(null, m, "Peringatan", JOptionPane.WARNING_MESSAGE); 
    }
    
    private void info(String m) { 
        JOptionPane.showMessageDialog(null, m, "Berhasil", JOptionPane.INFORMATION_MESSAGE); 
    }
}