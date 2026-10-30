package view;

import util.LicenseManager;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class ActivationView extends JFrame {
    static { Theme.install(); }

    public ActivationView() {
        setTitle("Aktivasi Aplikasi");
        setSize(580, 500);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Selalu di atas jendela lain, termasuk DashboardView
        setAlwaysOnTop(true);

        add(Theme.header("Aktivasi Aplikasi", "Aplikasi belum diaktivasi"), BorderLayout.NORTH);

        JPanel body = Theme.body(new BorderLayout());
        JPanel card = Theme.card("Data Aktivasi", new BorderLayout(0, 14));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        // Kode perangkat (read-only) + tombol salin
        JTextField tfHwid = Theme.field(20);
        tfHwid.setText(LicenseManager.getHardwareID());
        tfHwid.setEditable(false);
        tfHwid.setBackground(Theme.STRIPE);

        JButton btnSalin = Theme.button("Salin", Theme.SLATE);
        btnSalin.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(tfHwid.getText()), null);
            btnSalin.setText("Tersalin");
            Timer t = new Timer(1500, ev -> btnSalin.setText("Salin"));
            t.setRepeats(false);
            t.start();
        });

        JPanel hwidRow = new JPanel(new BorderLayout(8, 0));
        hwidRow.setOpaque(false);
        hwidRow.add(tfHwid, BorderLayout.CENTER);
        hwidRow.add(btnSalin, BorderLayout.EAST);

        JTextField tfKey = Theme.field(20);

        addBlock(form, 0, "1. Kirim kode ini ke Admin via WhatsApp", hwidRow);
        addBlock(form, 2, "2. Masukkan Kode Aktivasi dari Admin", tfKey);
        card.add(form, BorderLayout.CENTER);

        JButton btnAktivasi = Theme.button("AKTIVASI SEKARANG", Theme.SUCCESS);
        btnAktivasi.setPreferredSize(new Dimension(0, 44));
        card.add(btnAktivasi, BorderLayout.SOUTH);

        body.add(card, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        // Tekan Enter = aktivasi
        getRootPane().setDefaultButton(btnAktivasi);

        btnAktivasi.addActionListener(e -> {
    // Use activate() instead of isValidKey(). This checks AND saves it!
    if (LicenseManager.activate(tfKey.getText().trim())) {
        JOptionPane.showMessageDialog(this, "Aktivasi Berhasil!");
        this.dispose();
        new DashboardView().setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this, "Kode Aktivasi Salah atau tidak valid untuk PC ini!", "Error", JOptionPane.ERROR_MESSAGE);
    }
});
    }

    /** Label di atas, input melebar penuh di bawahnya (memakai 2 baris grid). */
    private void addBlock(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        c.gridy = row;
        c.insets = new Insets(8, 4, 4, 4);
        p.add(Theme.mutedLabel(label), c);

        c.gridy = row + 1;
        c.insets = new Insets(0, 4, 6, 4);
        p.add(field, c);
    }
}