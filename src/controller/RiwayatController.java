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

import java.util.List;
import java.util.stream.Collectors;

public class RiwayatController {
    private final PenjualanDAO dao = new PenjualanDAO();

    public List<PenjualanDAO.Ringkasan> load(String cari) {
        String q = cari.toLowerCase();
        return dao.findAllRingkasan().stream()
                .filter(r -> r.nomorFaktur().toLowerCase().contains(q)
                        || r.namaPelanggan().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public PenjualanDAO.Detail detail(String nomorFaktur) {
        return dao.findDetail(nomorFaktur);
    }

    public List<PenjualanDAO.BarisDetail> baris(String nomorFaktur) {
        return dao.findBaris(nomorFaktur);
    }
}
