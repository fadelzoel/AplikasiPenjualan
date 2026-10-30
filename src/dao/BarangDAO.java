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

import model.Barang;
import java.sql.*;
import java.util.*;

public class BarangDAO {

    public List<Barang> findAll() {
        List<Barang> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT id,kode_barang,nama_barang,harga FROM barang ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Barang(rs.getInt("id"), rs.getString("kode_barang"),
                        rs.getString("nama_barang"), rs.getDouble("harga")));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public void insert(String kode, String nama, double harga) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO barang(kode_barang,nama_barang,harga) VALUES(?,?,?)")) {
            ps.setString(1, kode); ps.setString(2, nama); ps.setDouble(3, harga);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new DuplicateKeyException("Kode barang sudah digunakan.");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(String kodeLama, String kode, String nama, double harga) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE barang SET kode_barang=?, nama_barang=?, harga=? WHERE kode_barang=?")) {
            ps.setString(1, kode); ps.setString(2, nama);
            ps.setDouble(3, harga); ps.setString(4, kodeLama);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException ex) {
            throw new DuplicateKeyException("Kode barang sudah digunakan.");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(String kode) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM barang WHERE kode_barang=?")) {
            ps.setString(1, kode);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static class DuplicateKeyException extends RuntimeException {
        public DuplicateKeyException(String m) { super(m); }
    }
}
