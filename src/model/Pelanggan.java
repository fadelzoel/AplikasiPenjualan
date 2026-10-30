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

public record Pelanggan(int id, String kode, String nama, String alamat, String noHp) {
    @Override public String toString() { return kode + " - " + nama; }
}
