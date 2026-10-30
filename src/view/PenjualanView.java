/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author noroi
 */

import controller.PenjualanController;
import dao.BarangDAO;
import dao.PelangganDAO;
import model.Barang;
import model.ItemTransaksi;
import model.Pelanggan;
import util.FormatUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PenjualanView extends JFrame {
    private final PenjualanController controller = new PenjualanController(this);
    
    private final DefaultTableModel modelCart = new DefaultTableModel(
            new Object[]{"Kode", "Nama Barang", "Qty", "Harga", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tableCart = new JTable(modelCart);

    private final JComboBox<Pelanggan> cbPelanggan = new JComboBox<>();
    private final JComboBox<Barang> cbBarang = new JComboBox<>();
    private final JTextField tfHarga = new JTextField(10);
    private final JTextField tfQty = new JTextField(5);
    private final JLabel lblSubtotal = new JLabel("Rp 0");
    private final JLabel lblTotal = new JLabel("Rp 0");

    public PenjualanView() {
        setTitle("Penjualan");
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("PENJUALAN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // --- Pelanggan Panel ---
        JPanel panelPelanggan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPelanggan.add(new JLabel("Pelanggan:"));
        panelPelanggan.add(cbPelanggan);
        mainPanel.add(panelPelanggan);

        // --- Form Barang Panel ---
        JPanel panelBarang = new JPanel(new GridBagLayout());
        panelBarang.setBorder(BorderFactory.createTitledBorder("Tambah Barang"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panelBarang.add(new JLabel("Barang:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panelBarang.add(cbBarang, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; panelBarang.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; panelBarang.add(tfHarga, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelBarang.add(new JLabel("Qty:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panelBarang.add(tfQty, gbc);

        gbc.gridx = 2; gbc.gridy = 1; panelBarang.add(new JLabel("Subtotal:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; panelBarang.add(lblSubtotal, gbc);

        JPanel btnPanel = new JPanel();
        JButton btnTambah = new JButton("Tambah Barang");
        JButton btnHapus = new JButton("Hapus Item");
        btnPanel.add(btnTambah);
        btnPanel.add(btnHapus);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; panelBarang.add(btnPanel, gbc);
        mainPanel.add(panelBarang);

        add(mainPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        JScrollPane scrollPane = new JScrollPane(tableCart);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("TOTAL: "));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        totalPanel.add(lblTotal);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JButton btnSimpan = new JButton("SIMPAN TRANSAKSI");
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 14));
        btnSimpan.setPreferredSize(new Dimension(250, 40));
        bottomPanel.add(btnSimpan, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Load Data & Listeners ---
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
