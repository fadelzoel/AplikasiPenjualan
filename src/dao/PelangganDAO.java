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

import model.Pelanggan;
import java.sql.*;
import java.util.*;

public class PelangganDAO {

    public List<Pelanggan> findAll() {
        List<Pelanggan> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT id,kode_pelanggan,nama,alamat,no_hp FROM pelanggan ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Pelanggan(rs.getInt("id"), rs.getString("kode_pelanggan"),
                        rs.getString("nama"), rs.getString("alamat"), rs.getString("no_hp")));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public String nextKode() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT kode_pelanggan FROM pelanggan ORDER BY id DESC LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return "PLG001";
            int n = Integer.parseInt(rs.getString(1).replace("PLG", "")) + 1;
            return String.format("PLG%03d", n);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void insert(String kode, String nama, String alamat, String noHp) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO pelanggan(kode_pelanggan,nama,alamat,no_hp) VALUES(?,?,?,?)")) {
            ps.setString(1, kode); ps.setString(2, nama);
            ps.setString(3, alamat); ps.setString(4, noHp);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(String kode, String nama, String alamat, String noHp) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE pelanggan SET nama=?, alamat=?, no_hp=? WHERE kode_pelanggan=?")) {
            ps.setString(1, nama); ps.setString(2, alamat);
            ps.setString(3, noHp); ps.setString(4, kode);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(String kode) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM pelanggan WHERE kode_pelanggan=?")) {
            ps.setString(1, kode);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
