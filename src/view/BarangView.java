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

import controller.BarangController;
import model.Barang;
import util.FormatUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BarangView extends JFrame {
    private final BarangController controller = new BarangController(this);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Kode Barang", "Nama Barang", "Harga"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField tfKode = new JTextField(20);
    private final JTextField tfNama = new JTextField(20);
    private final JTextField tfHarga = new JTextField(20);
    private final JTextField tfCari = new JTextField(25);
    private String selectedKode;

    public BarangView() {
        setTitle("Master Barang");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JLabel title = new JLabel("MASTER BARANG", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel cari = new JPanel();
        cari.add(new JLabel("Cari Barang:"));
        cari.add(tfCari);
        center.add(cari);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Form Barang"));
        form.add(new JLabel("Kode Barang:")); form.add(tfKode);
        form.add(new JLabel("Nama Barang:")); form.add(tfNama);
        form.add(new JLabel("Harga:")); form.add(tfHarga);
        
        JPanel formWrap = new JPanel();
        formWrap.add(form);
        center.add(formWrap);

        JPanel buttons = new JPanel();
        JButton bSimpan = new JButton("Simpan Barang");
        JButton bEdit = new JButton("Edit Barang");
        JButton bHapus = new JButton("Hapus Barang");
        buttons.add(bSimpan); buttons.add(bEdit); buttons.add(bHapus);
        center.add(buttons);

        add(center, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(850, 250));
        JPanel tableWrap = new JPanel();
        tableWrap.add(scroll);
        add(tableWrap, BorderLayout.SOUTH);

        // Actions
        tfCari.addCaretListener(e -> refresh());
        bSimpan.addActionListener(e -> controller.simpan(
                tfKode.getText().trim(), tfNama.getText().trim(), tfHarga.getText().trim()));
        bEdit.addActionListener(e -> controller.edit(
                selectedKode, tfKode.getText().trim(), tfNama.getText().trim(), tfHarga.getText().trim()));
        bHapus.addActionListener(e -> controller.hapus(selectedKode, tfNama.getText()));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            selectedKode = model.getValueAt(row, 0).toString();
            tfKode.setText(selectedKode);
            tfNama.setText(model.getValueAt(row, 1).toString());
            String harga = model.getValueAt(row, 2).toString()
                    .replace("Rp ", "").replace(".", "");
            tfHarga.setText(harga);
        });

        refresh();
    }

    public void refresh() {
        String cari = tfCari.getText().trim();
        List<Barang> data = controller.loadData(cari);
        model.setRowCount(0);
        for (Barang b : data) {
            model.addRow(new Object[]{b.kode(), b.nama(), FormatUtil.rupiah(b.harga())});
        }
    }

    public void clearForm() {
        tfKode.setText(""); tfNama.setText(""); tfHarga.setText("");
        selectedKode = null;
        table.clearSelection();
    }
}