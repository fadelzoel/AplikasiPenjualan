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

public record Barang(int id, String kode, String nama, double harga) {
    @Override public String toString() { return kode + " - " + nama; }
}
