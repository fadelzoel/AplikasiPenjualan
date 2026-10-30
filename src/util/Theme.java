/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author noroi
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Set;

public final class Theme {
    // ===== Palet warna =====
    public static final Color PRIMARY   = new Color(0x2563EB);
    public static final Color SUCCESS   = new Color(0x16A34A);
    public static final Color DANGER    = new Color(0xDC2626);
    public static final Color SLATE     = new Color(0x64748B);
    public static final Color BG        = new Color(0xF1F5F9);
    public static final Color CARD      = Color.WHITE;
    public static final Color BORDER    = new Color(0xE2E8F0);
    public static final Color TEXT      = new Color(0x0F172A);
    public static final Color MUTED     = new Color(0x64748B);
    public static final Color HEADER_BG = new Color(0x1E293B);
    public static final Color STRIPE    = new Color(0xF8FAFC);
    public static final Color SELECT    = new Color(0xDBEAFE);
    public static final Color HOVER     = new Color(0xF0F6FF);

    // ===== Font =====
    public static final Font BASE  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD  = BASE.deriveFont(Font.BOLD);
    public static final Font TITLE = BASE.deriveFont(Font.BOLD, 24f);

    private static final Set<String> RIGHT_COLS = Set.of("Harga", "Subtotal", "Total");

    private static boolean installed;

    private Theme() {}

    /** Panggil sekali sebelum komponen dibuat (aman dipanggil berulang). */
    public static void install() {
        if (installed) return;
        installed = true;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(BASE));
            }
        }
    }

    // ===== Layout helpers =====

    /** Banner gelap di bagian atas jendela. */
    public static JPanel header(String title, String subtitle) {
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 2));
        p.setBackground(HEADER_BG);
        p.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        JLabel t = new JLabel(title);
        t.setFont(TITLE);
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(BASE);
        s.setForeground(new Color(0x94A3B8));
        p.add(t);
        p.add(s);
        return p;
    }

    /** Panel isi utama dengan latar abu muda dan padding. */
    public static JPanel body(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));
        return p;
    }

    /** Kartu putih dengan border tipis; title boleh null. */
    public static JPanel card(String title, LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(CARD);
        Border line = BorderFactory.createLineBorder(BORDER);
        if (title == null) {
            p.setBorder(BorderFactory.createCompoundBorder(line,
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        } else {
            TitledBorder tb = BorderFactory.createTitledBorder(line, " " + title + " ");
            tb.setTitleFont(BOLD);
            tb.setTitleColor(PRIMARY);
            p.setBorder(BorderFactory.createCompoundBorder(tb,
                    BorderFactory.createEmptyBorder(6, 12, 12, 12)));
        }
        return p;
    }

    /** Baris form: label di kiri, input melebar di kanan (panel harus GridBagLayout). */
    public static void formRow(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.insets = new Insets(5, 4, 5, 14);
        p.add(mutedLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 4);
        p.add(field, c);
    }

    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BOLD);
        l.setForeground(MUTED);
        return l;
    }

    // ===== Input =====

    public static JTextField field(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(BASE);
        f.setForeground(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E1)),
                BorderFactory.createEmptyBorder(6, 9, 6, 9)));
        return f;
    }

    public static void styleCombo(JComboBox<?> c, int width) {
        c.setFont(BASE);
        c.setBackground(Color.WHITE);
        c.setPreferredSize(new Dimension(width, 34));
    }

    // ===== Tombol =====

    public static JButton button(String text, Color base) {
        JButton b = new JButton(text) {
            private boolean hover;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = base;
                if (!isEnabled()) c = new Color(0xCBD5E1);
                else if (getModel().isPressed()) c = base.darker().darker();
                else if (hover) c = base.darker();
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(BOLD);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        return b;
    }

    // ===== Tabel =====

    public static void styleTable(JTable t) {
        t.setFont(BASE);
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(SELECT);
        t.setSelectionForeground(TEXT);

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tb, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tb, v, sel, false, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setForeground(TEXT);
                setBackground(sel ? SELECT : (row % 2 == 0 ? Color.WHITE : STRIPE));
                String s = v == null ? "" : v.toString();
                setHorizontalAlignment(s.startsWith("Rp") ? RIGHT : (v instanceof Number ? CENTER : LEFT));
                return this;
            }
        };
        t.setDefaultRenderer(Object.class, cell);
        t.setDefaultRenderer(Number.class, cell);

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 38));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tb, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tb, v, false, false, row, col);
                String name = v == null ? "" : v.toString();
                setOpaque(true);
                setBackground(HEADER_BG);
                setForeground(Color.WHITE);
                setFont(BOLD);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(RIGHT_COLS.contains(name) ? RIGHT
                        : name.equals("Qty") ? CENTER : LEFT);
                return this;
            }
        });
    }

    /** Styling tabel + dibungkus scroll pane yang rapi. */
    public static JScrollPane scroll(JTable t) {
        styleTable(t);
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }
}