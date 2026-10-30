/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author noroi
 */

import controller.RiwayatController;
import dao.PenjualanDAO;
import util.FormatUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DetailTransaksiDialog extends JDialog {
    public DetailTransaksiDialog(JFrame parent, String nomorFaktur) {
        super(parent, "Detail " + nomorFaktur, true);
        setSize(800, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        RiwayatController controller = new RiwayatController();
        PenjualanDAO.Detail transaksi = controller.detail(nomorFaktur);
        List<PenjualanDAO.BarisDetail> detail = controller.baris(nomorFaktur);

        JLabel title = new JLabel("DETAIL TRANSAKSI", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        if (transaksi != null) {
            infoPanel.add(new JLabel("No. Faktur : " + transaksi.nomorFaktur()));
            infoPanel.add(new JLabel("Tanggal : " + transaksi.tanggal()));
            infoPanel.add(new JLabel("Pelanggan : " + transaksi.pelanggan()));
            infoPanel.add(new JLabel("Alamat : " + transaksi.alamat()));
            infoPanel.add(new JLabel("No. HP : " + transaksi.noHp()));
        }
        add(infoPanel, BorderLayout.NORTH); // Note: simplified layout for now

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Kode", "Nama Barang", "Qty", "Harga", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        
        for (PenjualanDAO.BarisDetail b : detail) {
            model.addRow(new Object[]{
                    b.kode(), b.nama(), b.qty(), FormatUtil.rupiah(b.harga()), FormatUtil.rupiah(b.subtotal())
            });
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.add(infoPanel, BorderLayout.NORTH);
        centerWrap.add(scroll, BorderLayout.CENTER);
        add(centerWrap, BorderLayout.CENTER);

        if (transaksi != null) {
            JLabel lblTotal = new JLabel("TOTAL : " + FormatUtil.rupiah(transaksi.total()));
            lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
            lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
            lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 30));
            add(lblTotal, BorderLayout.SOUTH);
        }
    }
}
