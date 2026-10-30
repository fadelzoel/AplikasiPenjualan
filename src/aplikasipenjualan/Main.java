/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplikasipenjualan;

/**
 *
 * @author noroi
 */


import dao.Database;
import view.DashboardView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // 1. Inisialisasi Database (membuat tabel jika belum ada)
        Database.init();

        // 2. Set Look and Feel agar tampilan mengikuti OS (Windows/Mac/Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Gagal set Look and Feel: " + e.getMessage());
        }

        // 3. Jalankan Aplikasi di Event Dispatch Thread (best practice untuk Swing)
        SwingUtilities.invokeLater(() -> {
            DashboardView dashboard = new DashboardView();
            dashboard.setVisible(true);
        });
    }
}
