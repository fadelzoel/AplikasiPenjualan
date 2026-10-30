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
import java.util.List;

public class DetailTransaksiDialog extends JDialog {
    static { Theme.install(); }

    public DetailTransaksiDialog(JFrame parent, String nomorFaktur) {
        super(parent, "Detail " + nomorFaktur, true);
        setSize(820, 620);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        RiwayatController controller = new RiwayatController();
        PenjualanDAO.Detail transaksi = controller.detail(nomorFaktur);
        List<PenjualanDAO.BarisDetail> detail = controller.baris(nomorFaktur);

        add(Theme.header("Detail Transaksi", nomorFaktur), BorderLayout.NORTH);

        JPanel body = Theme.body(new BorderLayout(0, 14));

        // --- Info transaksi ---
        JPanel info = Theme.card("Informasi", new GridBagLayout());
        if (transaksi != null) {
            Theme.formRow(info, 0, "No. Faktur", value(transaksi.nomorFaktur()));
            Theme.formRow(info, 1, "Tanggal", value(transaksi.tanggal()));
            Theme.formRow(info, 2, "Pelanggan", value(transaksi.pelanggan()));
            Theme.formRow(info, 3, "Alamat", value(transaksi.alamat()));
            Theme.formRow(info, 4, "No. HP", value(transaksi.noHp()));
        } else {
            Theme.formRow(info, 0, "Status", value("Data transaksi tidak ditemukan"));
        }
        body.add(info, BorderLayout.NORTH);

        // --- Daftar barang ---
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

        JPanel items = Theme.card("Daftar Barang", new BorderLayout());
        items.add(Theme.scroll(table), BorderLayout.CENTER);
        body.add(items, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // --- Footer: total + tutup ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.CARD);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalPanel.setOpaque(false);
        totalPanel.add(Theme.mutedLabel("TOTAL"));
        if (transaksi != null) {
            JLabel lblTotal = new JLabel(FormatUtil.rupiah(transaksi.total()));
            lblTotal.setFont(Theme.BASE.deriveFont(Font.BOLD, 24f));
            lblTotal.setForeground(Theme.PRIMARY);
            totalPanel.add(lblTotal);
        }
        footer.add(totalPanel, BorderLayout.WEST);

        JButton bTutup = Theme.button("Tutup", Theme.SLATE);
        bTutup.addActionListener(e -> dispose());
        footer.add(bTutup, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);
    }

    private JLabel value(Object text) {
        JLabel l = new JLabel(text == null ? "-" : text.toString());
        l.setFont(Theme.BASE);
        l.setForeground(Theme.TEXT);
        return l;
    }
}