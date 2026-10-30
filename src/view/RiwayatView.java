package view;

/**
 * @author noroi
 */

import controller.RiwayatController;
import dao.PenjualanDAO;
import util.FormatUtil;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class RiwayatView extends JFrame {
    static { Theme.install(); }

    private final RiwayatController controller = new RiwayatController();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"No. Faktur", "Tanggal", "Pelanggan", "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);
    private final JTextField tfCari = Theme.field(25);

    public RiwayatView() {
        setTitle("Riwayat Penjualan");
        setSize(950, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Theme.header("Riwayat Penjualan", "Klik dua kali pada transaksi untuk melihat detail"),
                BorderLayout.NORTH);

        JPanel body = Theme.body(new BorderLayout(0, 14));

        JPanel card = Theme.card("Daftar Transaksi", new BorderLayout(0, 10));
        JPanel cari = new JPanel(new BorderLayout(10, 0));
        cari.setOpaque(false);
        cari.add(Theme.mutedLabel("Cari (Faktur/Pelanggan)"), BorderLayout.WEST);
        cari.add(tfCari, BorderLayout.CENTER);
        card.add(cari, BorderLayout.NORTH);
        card.add(Theme.scroll(table), BorderLayout.CENTER);
        body.add(card, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton bRefresh = Theme.button("Refresh", Theme.SLATE);
        JButton bDetail = Theme.button("Lihat Detail", Theme.PRIMARY);
        JButton bCetak = Theme.button("Cetak Bon", Theme.SUCCESS);
        buttons.add(bRefresh);
        buttons.add(bDetail);
        buttons.add(bCetak);
        body.add(buttons, BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);

        // Actions
        tfCari.addCaretListener(e -> refresh());
        bRefresh.addActionListener(e -> refresh());
        bDetail.addActionListener(e -> bukaDetail());

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) bukaDetail();
            }
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

    private void bukaDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi terlebih dahulu.");
            return;
        }
        String nomorFaktur = model.getValueAt(row, 0).toString();
        new DetailTransaksiDialog(this, nomorFaktur).setVisible(true);
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