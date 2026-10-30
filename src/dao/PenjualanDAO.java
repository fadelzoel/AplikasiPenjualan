/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author noroi
 */

import model.ItemTransaksi;
import java.sql.*;
import java.util.List;

public class PenjualanDAO {

    public record Ringkasan(int id, String nomorFaktur, String tanggal,
                            String namaPelanggan, double total) {}

    public record Detail(int id, String nomorFaktur, String tanggal,
                         String pelanggan, String alamat, String noHp, double total) {}

    public record BarisDetail(String kode, String nama, int qty,
                              double harga, double subtotal) {}

    public List<Ringkasan> findAllRingkasan() {
        List<Ringkasan> list = new java.util.ArrayList<>();
        String sql = """
            SELECT p.id, p.nomor_faktur, p.tanggal, pl.nama, p.total
            FROM penjualan p JOIN pelanggan pl ON p.pelanggan_id = pl.id
            ORDER BY p.id DESC""";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Ringkasan(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getDouble(5)));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    /**
     * Saves transaction + details in a single transaction.
     * @return nomor faktur
     */
    public String simpan(int pelangganId, List<ItemTransaksi> items) {
        String tanggal = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new java.util.Date());
        double total = items.stream().mapToDouble(i -> i.subtotal).sum();

        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Determine next faktur number
                int nomor = 1;
                try (Statement st = c.createStatement();
                     ResultSet rs = st.executeQuery(
                         "SELECT id FROM penjualan ORDER BY id DESC LIMIT 1")) {
                    if (rs.next()) nomor = rs.getInt(1) + 1;
                }
                String nomorFaktur = String.format("INV-%05d", nomor);

                int penjualanId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO penjualan(nomor_faktur,tanggal,pelanggan_id,total) VALUES(?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nomorFaktur); ps.setString(2, tanggal);
                    ps.setInt(3, pelangganId); ps.setDouble(4, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        penjualanId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO detail_penjualan(penjualan_id,barang_id,qty,harga,subtotal) VALUES(?,?,?,?,?)")) {
                    for (ItemTransaksi it : items) {
                        ps.setInt(1, penjualanId);
                        ps.setInt(2, it.barangId);
                        ps.setInt(3, it.qty);
                        ps.setDouble(4, it.harga);
                        ps.setDouble(5, it.subtotal);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                c.commit();
                return nomorFaktur;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Detail findDetail(String nomorFaktur) {
        String sql = """
            SELECT p.nomor_faktur, p.tanggal, pl.nama, pl.alamat, pl.no_hp, p.total
            FROM penjualan p JOIN pelanggan pl ON p.pelanggan_id = pl.id
            WHERE p.nomor_faktur = ?""";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nomorFaktur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Detail(0, rs.getString(1), rs.getString(2),
                            rs.getString(3), rs.getString(4), rs.getString(5), rs.getDouble(6));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public List<BarisDetail> findBaris(String nomorFaktur) {
        List<BarisDetail> list = new java.util.ArrayList<>();
        String sql = """
            SELECT b.kode_barang, b.nama_barang, d.qty, d.harga, d.subtotal
            FROM detail_penjualan d
            JOIN barang b ON d.barang_id = b.id
            JOIN penjualan p ON d.penjualan_id = p.id
            WHERE p.nomor_faktur = ?""";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nomorFaktur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BarisDetail(rs.getString(1), rs.getString(2),
                            rs.getInt(3), rs.getDouble(4), rs.getDouble(5)));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
