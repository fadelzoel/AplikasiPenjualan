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

import dao.PenjualanDAO;
import model.ItemTransaksi;
import model.Pelanggan;
import view.PenjualanView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PenjualanController {
    private final PenjualanView view;
    private final PenjualanDAO dao = new PenjualanDAO();
    private final List<ItemTransaksi> cart = new ArrayList<>();

    public PenjualanController(PenjualanView view) { 
        this.view = view; 
    }

    public List<ItemTransaksi> getCart() { 
        return cart; 
    }

    public double getTotal() {
        return cart.stream().mapToDouble(i -> i.subtotal).sum();
    }

    public void tambahItem(int barangId, String kode, String nama, int qty, double harga) {
        if (qty <= 0) { warn("Qty harus lebih dari 0."); return; }
        if (harga <= 0) { warn("Harga harus lebih dari 0."); return; }
        
        cart.add(new ItemTransaksi(barangId, kode, nama, qty, harga));
        view.refreshTabel();
        view.clearBarangForm();
    }

    public void hapusItem(int index) {
        if (index < 0 || index >= cart.size()) {
            warn("Pilih barang yang ingin dihapus."); return;
        }
        cart.remove(index);
        view.refreshTabel();
    }

    public void simpan(Pelanggan pelanggan) {
        if (cart.isEmpty()) { warn("Belum ada barang dalam transaksi."); return; }
        if (pelanggan == null) { warn("Pilih pelanggan terlebih dahulu."); return; }

        try {
            String nomor = dao.simpan(pelanggan.id(), cart);
            JOptionPane.showMessageDialog(null,
                    "Transaksi berhasil disimpan.\n\nNomor Faktur: " + nomor,
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            view.refreshTabel();
            view.resetAfterSave();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    "Transaksi gagal disimpan.\n\n" + e.getMessage(),
                    "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void warn(String m) { 
        JOptionPane.showMessageDialog(null, m, "Peringatan", JOptionPane.WARNING_MESSAGE); 
    }
}