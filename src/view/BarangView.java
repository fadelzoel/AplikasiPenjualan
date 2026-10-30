package view;

/**
 * @author noroi
 */

import controller.BarangController;
import model.Barang;
import util.FormatUtil;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BarangView extends JFrame {
    static { Theme.install(); }

    private final BarangController controller = new BarangController(this);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Kode Barang", "Nama Barang", "Harga"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField tfKode = Theme.field(20);
    private final JTextField tfNama = Theme.field(20);
    private final JTextField tfHarga = Theme.field(20);
    private final JTextField tfCari = Theme.field(25);
    private String selectedKode;

    public BarangView() {
        setTitle("Master Barang");
        setSize(950, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Theme.header("Master Barang", "Kelola data barang dan harga jual"), BorderLayout.NORTH);

        JPanel body = Theme.body(new BorderLayout(0, 16));

        // --- Form ---
        JPanel formCard = Theme.card("Form Barang", new BorderLayout(0, 10));
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        Theme.formRow(form, 0, "Kode Barang", tfKode);
        Theme.formRow(form, 1, "Nama Barang", tfNama);
        Theme.formRow(form, 2, "Harga", tfHarga);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton bSimpan = Theme.button("Simpan Barang", Theme.SUCCESS);
        JButton bEdit = Theme.button("Edit Barang", Theme.PRIMARY);
        JButton bHapus = Theme.button("Hapus Barang", Theme.DANGER);
        JButton bBersih = Theme.button("Bersihkan", Theme.SLATE);
        buttons.add(bBersih);
        buttons.add(bSimpan);
        buttons.add(bEdit);
        buttons.add(bHapus);

        formCard.add(form, BorderLayout.CENTER);
        formCard.add(buttons, BorderLayout.SOUTH);
        body.add(formCard, BorderLayout.NORTH);

        // --- Tabel + pencarian ---
        JPanel tableCard = Theme.card("Daftar Barang", new BorderLayout(0, 10));
        JPanel cari = new JPanel(new BorderLayout(10, 0));
        cari.setOpaque(false);
        cari.add(Theme.mutedLabel("Cari Barang"), BorderLayout.WEST);
        cari.add(tfCari, BorderLayout.CENTER);
        tableCard.add(cari, BorderLayout.NORTH);
        tableCard.add(Theme.scroll(table), BorderLayout.CENTER);
        body.add(tableCard, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // --- Actions (logika tidak berubah) ---
        tfCari.addCaretListener(e -> refresh());
        bSimpan.addActionListener(e -> controller.simpan(
                tfKode.getText().trim(), tfNama.getText().trim(), tfHarga.getText().trim()));
        bEdit.addActionListener(e -> controller.edit(
                selectedKode, tfKode.getText().trim(), tfNama.getText().trim(), tfHarga.getText().trim()));
        bHapus.addActionListener(e -> controller.hapus(selectedKode, tfNama.getText()));
        bBersih.addActionListener(e -> clearForm());

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