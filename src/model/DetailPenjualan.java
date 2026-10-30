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
public record DetailPenjualan(int id, int penjualanId, int barangId,
                              int qty, double harga, double subtotal) {}