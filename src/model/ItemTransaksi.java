/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author noroi
 */

/** Line item in the in-memory cart. */
public class ItemTransaksi {
    public int barangId;
    public String kodeBarang;
    public String namaBarang;
    public int qty;
    public double harga;
    public double subtotal;

    public ItemTransaksi(int barangId, String kodeBarang, String namaBarang,
                         int qty, double harga) {
        this.barangId = barangId;
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.qty = qty;
        this.harga = harga;
        this.subtotal = harga * qty;
    }
}
