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

import controller.PelangganController;
import model.Pelanggan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PelangganView extends JFrame {
    private final PelangganController controller = new PelangganController(this);
    
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Kode", "Nama Pelanggan", "Alamat", "No. HP"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    
    private final JTable table = new JTable(model);
    private final JTextField tfNama = new JTextField(25);
    private final JTextField tfAlamat = new JTextField(25);
    private final JTextField tfNoHp = new JTextField(25);
    private final JTextField tfCari = new JTextField(25);
    private String selectedKode;

    public PelangganView() {
        setTitle("Master Pelanggan");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JLabel title = new JLabel("MASTER PELANGGAN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel cari = new JPanel();
        cari.add(new JLabel("Cari Pelanggan:"));
        cari.add(tfCari);
        center.add(cari);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Form Pelanggan"));
        form.add(new JLabel("Nama Pelanggan:")); form.add(tfNama);
        form.add(new JLabel("Alamat:")); form.add(tfAlamat);
        form.add(new JLabel("No. HP:")); form.add(tfNoHp);
        
        JPanel formWrap = new JPanel();
        formWrap.add(form);
        center.add(formWrap);

        JPanel buttons = new JPanel();
        JButton bSimpan = new JButton("Simpan Pelanggan");
        JButton bEdit = new JButton("Edit Pelanggan");
        JButton bHapus = new JButton("Hapus Pelanggan");
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
                tfNama.getText().trim(), tfAlamat.getText().trim(), tfNoHp.getText().trim()));
                
        bEdit.addActionListener(e -> controller.edit(
                selectedKode, tfNama.getText().trim(), tfAlamat.getText().trim(), tfNoHp.getText().trim()));
                
        bHapus.addActionListener(e -> controller.hapus(selectedKode, tfNama.getText()));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            selectedKode = model.getValueAt(row, 0).toString();
            tfNama.setText(model.getValueAt(row, 1).toString());
            tfAlamat.setText(model.getValueAt(row, 2).toString());
            tfNoHp.setText(model.getValueAt(row, 3).toString());
        });

        refresh();
    }

    public void refresh() {
        String cari = tfCari.getText().trim().toLowerCase();
        List<Pelanggan> data = controller.loadData(cari);
        model.setRowCount(0);
        for (Pelanggan p : data) {
            model.addRow(new Object[]{p.kode(), p.nama(), p.alamat(), p.noHp()});
        }
    }

    public void clearForm() {
        tfNama.setText(""); tfAlamat.setText(""); tfNoHp.setText("");
        selectedKode = null;
        table.clearSelection();
    }
}