package view;

/**
 * @author noroi
 */

import controller.PenjualanController;
import dao.BarangDAO;
import dao.PelangganDAO;
import model.Barang;
import model.ItemTransaksi;
import model.Pelanggan;
import util.FormatUtil;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PenjualanView extends JFrame {
    static { Theme.install(); }

    private final PenjualanController controller = new PenjualanController(this);

    private final DefaultTableModel modelCart = new DefaultTableModel(
            new Object[]{"Kode", "Nama Barang", "Qty", "Harga", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tableCart = new JTable(modelCart);

    private final JComboBox<Pelanggan> cbPelanggan = new JComboBox<>();
    private final JComboBox<Barang> cbBarang = new JComboBox<>();
    private final JTextField tfHarga = Theme.field(10);
    private final JTextField tfQty = Theme.field(5);
    private final JLabel lblSubtotal = new JLabel("Rp 0");
    private final JLabel lblTotal = new JLabel("Rp 0");

    public PenjualanView() {
        setTitle("Penjualan");
        setSize(1000, 740);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Theme.header("Penjualan", "Pilih pelanggan, tambahkan barang, lalu simpan transaksi"),
                BorderLayout.NORTH);

        Theme.styleCombo(cbPelanggan, 320);
        Theme.styleCombo(cbBarang, 320);
        lblSubtotal.setFont(Theme.BASE.deriveFont(Font.BOLD, 16f));
        lblSubtotal.setForeground(Theme.PRIMARY);

        JPanel body = Theme.body(new BorderLayout(0, 14));

        // --- Atas: pelanggan + form barang ---
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);

        JPanel cardPelanggan = Theme.card("Pelanggan", new GridBagLayout());
        Theme.formRow(cardPelanggan, 0, "Pelanggan", cbPelanggan);
        cardPelanggan.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(cardPelanggan);
        top.add(Box.createVerticalStrut(12));

        JPanel panelBarang = Theme.card("Tambah Barang", new GridBagLayout());
        panelBarang.setAlignmentX(Component.LEFT_ALIGNMENT);
        put(panelBarang, Theme.mutedLabel("Barang"), 0, 0, 0, 1);
        put(panelBarang, cbBarang, 1, 0, 1, 1);
        put(panelBarang, Theme.mutedLabel("Harga"), 2, 0, 0, 1);
        put(panelBarang, tfHarga, 3, 0, 0.4, 1);
        put(panelBarang, Theme.mutedLabel("Qty"), 0, 1, 0, 1);
        put(panelBarang, tfQty, 1, 1, 1, 1);
        put(panelBarang, Theme.mutedLabel("Subtotal"), 2, 1, 0, 1);
        put(panelBarang, lblSubtotal, 3, 1, 0.4, 1);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton btnHapus = Theme.button("Hapus Item", Theme.DANGER);
        JButton btnTambah = Theme.button("Tambah Barang", Theme.PRIMARY);
        btnPanel.add(btnHapus);
        btnPanel.add(btnTambah);
        put(panelBarang, btnPanel, 0, 2, 1, 4);
        top.add(panelBarang);

        body.add(top, BorderLayout.NORTH);

        // --- Tengah: keranjang ---
        JPanel cartCard = Theme.card("Keranjang", new BorderLayout());
        cartCard.add(Theme.scroll(tableCart), BorderLayout.CENTER);
        body.add(cartCard, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // --- Bawah: total + simpan ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.CARD);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalPanel.setOpaque(false);
        totalPanel.add(Theme.mutedLabel("TOTAL"));
        lblTotal.setFont(Theme.BASE.deriveFont(Font.BOLD, 26f));
        lblTotal.setForeground(Theme.PRIMARY);
        totalPanel.add(lblTotal);
        footer.add(totalPanel, BorderLayout.WEST);

        JButton btnSimpan = Theme.button("SIMPAN TRANSAKSI", Theme.SUCCESS);
        btnSimpan.setFont(Theme.BASE.deriveFont(Font.BOLD, 15f));
        btnSimpan.setPreferredSize(new Dimension(240, 44));
        footer.add(btnSimpan, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);

        // --- Load Data & Listeners (logika tidak berubah) ---
        loadData();
        tfQty.setText("1");

        cbBarang.addActionListener(e -> {
            Barang b = (Barang) cbBarang.getSelectedItem();
            if (b != null) {
                tfHarga.setText(String.valueOf(b.harga()));
                hitungSubtotal();
            }
        });

        tfQty.addCaretListener(e -> hitungSubtotal());
        tfHarga.addCaretListener(e -> hitungSubtotal());

        btnTambah.addActionListener(e -> {
            Barang b = (Barang) cbBarang.getSelectedItem();
            if (b == null) { JOptionPane.showMessageDialog(this, "Pilih barang!"); return; }
            try {
                int qty = Integer.parseInt(tfQty.getText());
                double harga = Double.parseDouble(tfHarga.getText());
                controller.tambahItem(b.id(), b.kode(), b.nama(), qty, harga);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Qty/Harga harus angka!");
            }
        });

        btnHapus.addActionListener(e -> controller.hapusItem(tableCart.getSelectedRow()));

        btnSimpan.addActionListener(e -> {
            Pelanggan p = (Pelanggan) cbPelanggan.getSelectedItem();
            controller.simpan(p);
        });
    }

    /** Helper kecil untuk menaruh komponen di GridBagLayout. */
    private void put(JPanel p, JComponent c, int x, int y, double weightX, int width) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y;
        g.weightx = weightX;
        g.gridwidth = width;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 5, 5, 8);
        p.add(c, g);
    }

    private void loadData() {
        cbPelanggan.removeAllItems();
        for (Pelanggan p : new PelangganDAO().findAll()) cbPelanggan.addItem(p);

        cbBarang.removeAllItems();
        for (Barang b : new BarangDAO().findAll()) cbBarang.addItem(b);
    }

    private void hitungSubtotal() {
        try {
            double harga = Double.parseDouble(tfHarga.getText());
            int qty = Integer.parseInt(tfQty.getText());
            lblSubtotal.setText(FormatUtil.rupiah(harga * qty));
        } catch (NumberFormatException e) {
            lblSubtotal.setText("Rp 0");
        }
    }

    public void refreshTabel() {
        modelCart.setRowCount(0);
        for (ItemTransaksi item : controller.getCart()) {
            modelCart.addRow(new Object[]{
                    item.kodeBarang, item.namaBarang, item.qty,
                    FormatUtil.rupiah(item.harga), FormatUtil.rupiah(item.subtotal)
            });
        }
        lblTotal.setText(FormatUtil.rupiah(controller.getTotal()));
    }

    public void clearBarangForm() {
        cbBarang.setSelectedIndex(-1);
        tfHarga.setText("");
        tfQty.setText("1");
        lblSubtotal.setText("Rp 0");
    }

    public void resetAfterSave() {
        cbPelanggan.setSelectedIndex(-1);
        clearBarangForm();
    }
}