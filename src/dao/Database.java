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
import java.sql.*;

public class Database {
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver not found! Check your Libraries.");
            e.printStackTrace();
        }
    }

    private static final String URL = "jdbc:sqlite:database.db";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    

    /** Create tables if they don't exist (same schema as Python). */
    public static void init() {
        String[] sqls = {
            """
            CREATE TABLE IF NOT EXISTS barang (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kode_barang TEXT UNIQUE NOT NULL,
                nama_barang TEXT NOT NULL,
                harga REAL NOT NULL)
            """,
            """
            CREATE TABLE IF NOT EXISTS pelanggan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kode_pelanggan TEXT UNIQUE NOT NULL,
                nama TEXT NOT NULL,
                alamat TEXT,
                no_hp TEXT)
            """,
            """
            CREATE TABLE IF NOT EXISTS penjualan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nomor_faktur TEXT UNIQUE NOT NULL,
                tanggal TEXT NOT NULL,
                pelanggan_id INTEGER NOT NULL,
                total REAL NOT NULL,
                FOREIGN KEY (pelanggan_id) REFERENCES pelanggan(id))
            """,
            """
            CREATE TABLE IF NOT EXISTS detail_penjualan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                penjualan_id INTEGER NOT NULL,
                barang_id INTEGER NOT NULL,
                qty INTEGER NOT NULL,
                harga REAL NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY (penjualan_id) REFERENCES penjualan(id),
                FOREIGN KEY (barang_id) REFERENCES barang(id))
            """
        };
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            for (String sql : sqls) st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Gagal membuat database", e);
        }
    }
}
