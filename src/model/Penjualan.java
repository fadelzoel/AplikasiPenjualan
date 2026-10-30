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
public record Penjualan(int id, String nomorFaktur, String tanggal,
                        int pelangganId, double total) {}
