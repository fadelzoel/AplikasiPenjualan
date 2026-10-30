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

public class RiwayatView extends JFrame {
    private final RiwayatController controller = new RiwayatController();
    
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"No. Faktur", "Tanggal", "Pelanggan", "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    
    private final JTable table = new JTable(model);
    private final JTextField tfCari = new JTextField(25);

    public RiwayatView() {
        setTitle("Riwayat Penjualan");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JLabel title = new JLabel("RIWAYAT PENJUALAN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Cari (Faktur/Pelanggan):"));
        topPanel.add(tfCari);
        add(topPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.add(topPanel, BorderLayout.NORTH);
        centerWrap.add(scroll, BorderLayout.CENTER);
        add(centerWrap, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton bDetail = new JButton("Lihat Detail");
        JButton bCetak = new JButton("Cetak Bon");
        JButton bRefresh = new JButton("Refresh");
        buttons.add(bDetail);
        buttons.add(bCetak);
        buttons.add(bRefresh);
        add(buttons, BorderLayout.SOUTH);

        // Actions
        tfCari.addCaretListener(e -> refresh());
        bRefresh.addActionListener(e -> refresh());
        
        bDetail.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih transaksi terlebih dahulu.");
                return;
            }
            String nomorFaktur = model.getValueAt(row, 0).toString();
            new DetailTransaksiDialog(this, nomorFaktur).setVisible(true);
        });

        bCetak.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih transaksi yang ingin dicetak.");
                return;
            }
            String nomorFaktur = model.getValueAt(row, 0).toString();
            util.PdfCetak.cetakBon(nomorFaktur);
        });

        refresh();
    }

    public void refresh() {
        String cari = tfCari.getText().trim();
        List<PenjualanDAO.Ringkasan> data = controller.load(cari);
        model.setRowCount(0);
        for (PenjualanDAO.Ringkasan r : data) {
            model.addRow(new Object[]{
                    r.nomorFaktur(), r.tanggal(), r.namaPelanggan(), FormatUtil.rupiah(r.total())
            });
        }
    }
}