package view;

import util.LicenseManager;
import util.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardView extends JFrame {
    static { Theme.install(); }

    public DashboardView() {
        setTitle("Aplikasi Penjualan");
        setSize(780, 600); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        int daysLeft = LicenseManager.getTrialDaysLeft();

        // --- MAIN CONTAINER ---
        JPanel container = new JPanel(new BorderLayout());
        
        // 1. ADD BANNER (Only once)
        if (daysLeft != 999 && daysLeft != -1) {
            JPanel banner = new JPanel();
            banner.setBackground(daysLeft <= 2 ? new Color(0xEF4444) : new Color(0xF59E0B));
            banner.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            JLabel lblBanner = new JLabel("Masa Trial: " + daysLeft + " hari tersisa. Hubungi Admin untuk aktivasi.");
            lblBanner.setForeground(Color.WHITE);
            lblBanner.setFont(new Font("Arial", Font.BOLD, 13));
            banner.add(lblBanner);
            container.add(banner, BorderLayout.NORTH);
        }

        // 2. ADD HEADER & BODY
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(Theme.header("Aplikasi Penjualan", "Pilih menu untuk memulai"), BorderLayout.NORTH);

        JPanel body = Theme.body(new BorderLayout(0, 16));
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.add(menuCard("Master Barang", "Kelola data barang dan harga jual.",
                Theme.PRIMARY, () -> new BarangView().setVisible(true)));
        grid.add(menuCard("Master Pelanggan", "Kelola data pelanggan, alamat dan nomor HP.",
                new Color(0x7C3AED), () -> new PelangganView().setVisible(true)));
        grid.add(menuCard("Penjualan", "Buat transaksi baru dan simpan ke faktur.",
                Theme.SUCCESS, () -> new PenjualanView().setVisible(true)));
        grid.add(menuCard("Riwayat Penjualan", "Lihat detail transaksi dan cetak bon.",
                new Color(0xF59E0B), () -> new RiwayatView().setVisible(true)));
        body.add(grid, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        JButton keluar = Theme.button("Keluar", Theme.DANGER);
        keluar.addActionListener(e -> dispose());
        footer.add(keluar);
        body.add(footer, BorderLayout.SOUTH);

        mainPanel.add(body, BorderLayout.CENTER);
        container.add(mainPanel, BorderLayout.CENTER);
        
        add(container, BorderLayout.CENTER);
    }

    private JPanel menuCard(String title, String desc, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        Border inner = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, accent),
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER), inner));
        card.setBackground(Theme.CARD);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel t = new JLabel(title);
        t.setFont(Theme.BASE.deriveFont(Font.BOLD, 18f));
        t.setForeground(Theme.TEXT);
        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(Theme.BASE);
        d.setForeground(Theme.MUTED);
        d.setVerticalAlignment(SwingConstants.TOP);
        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(Theme.HOVER); }
            @Override public void mouseExited(MouseEvent e) { card.setBackground(Theme.CARD); }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return card;
    }
}