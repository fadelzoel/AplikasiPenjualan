/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author noroi
 */


import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {
    public DashboardView() {
        setTitle("Aplikasi Penjualan");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel title = new JLabel("APLIKASI PENJUALAN");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(40));
        add(title);
        add(Box.createVerticalStrut(30));

        add(menuButton("MASTER BARANG", () -> new BarangView().setVisible(true)));
        add(menuButton("MASTER PELANGGAN", () -> new PelangganView().setVisible(true)));
        add(menuButton("PENJUALAN", () -> new PenjualanView().setVisible(true)));
        add(menuButton("RIWAYAT PENJUALAN", () -> new RiwayatView().setVisible(true))); // We will make RiwayatView next!
        add(menuButton("KELUAR", this::dispose));
    }

    private JButton menuButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(300, 40));
        b.addActionListener(e -> action.run());
        return b;
    }
}
