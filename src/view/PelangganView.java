package view;

/**
 * @author noroi
 */

import controller.PelangganController;
import model.Pelanggan;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PelangganView extends JFrame {
    static { Theme.install(); }

    private final PelangganController controller = new PelangganController(this);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Kode", "Nama Pelanggan", "Alamat", "No. HP"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);
    private final JTextField tfNama = Theme.field(25);
    private final JTextField tfAlamat = Theme.field(25);
    private final JTextField tfNoHp = Theme.field(25);
    private final JTextField tfCari = Theme.field(25);
    private String selectedKode;

    public PelangganView() {
        setTitle("Master Pelanggan");
        setSize(950, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Theme.header("Master Pelanggan", "Kelola data pelanggan, alamat dan nomor HP"), BorderLayout.NORTH);

        JPanel body = Theme.body(new BorderLayout(0, 16));

        // --- Form ---
        JPanel formCard = Theme.card("Form Pelanggan", new BorderLayout(0, 10));
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        Theme.formRow(form, 0, "Nama Pelanggan", tfNama);
        Theme.formRow(form, 1, "Alamat", tfAlamat);
        Theme.formRow(form, 2, "No. HP", tfNoHp);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton bSimpan = Theme.button("Simpan Pelanggan", Theme.SUCCESS);
        JButton bEdit = Theme.button("Edit Pelanggan", Theme.PRIMARY);
        JButton bHapus = Theme.button("Hapus Pelanggan", Theme.DANGER);
        JButton bBersih = Theme.button("Bersihkan", Theme.SLATE);
        buttons.add(bBersih);
        buttons.add(bSimpan);
        buttons.add(bEdit);
        buttons.add(bHapus);

        formCard.add(form, BorderLayout.CENTER);
        formCard.add(buttons, BorderLayout.SOUTH);
        body.add(formCard, BorderLayout.NORTH);

        // --- Tabel + pencarian ---
        JPanel tableCard = Theme.card("Daftar Pelanggan", new BorderLayout(0, 10));
        JPanel cari = new JPanel(new BorderLayout(10, 0));
        cari.setOpaque(false);
        cari.add(Theme.mutedLabel("Cari Pelanggan"), BorderLayout.WEST);
        cari.add(tfCari, BorderLayout.CENTER);
        tableCard.add(cari, BorderLayout.NORTH);
        tableCard.add(Theme.scroll(table), BorderLayout.CENTER);
        body.add(tableCard, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // --- Actions (logika tidak berubah) ---
        tfCari.addCaretListener(e -> refresh());

        bSimpan.addActionListener(e -> controller.simpan(
                tfNama.getText().trim(), tfAlamat.getText().trim(), tfNoHp.getText().trim()));

        bEdit.addActionListener(e -> controller.edit(
                selectedKode, tfNama.getText().trim(), tfAlamat.getText().trim(), tfNoHp.getText().trim()));

        bHapus.addActionListener(e -> controller.hapus(selectedKode, tfNama.getText()));
        bBersih.addActionListener(e -> clearForm());

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