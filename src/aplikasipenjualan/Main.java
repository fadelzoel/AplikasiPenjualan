package aplikasipenjualan;

import dao.Database;
import util.LicenseManager;
import view.DashboardView;
import view.ActivationView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Database
        Database.init();

        // 2. Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Check License / Trial
        SwingUtilities.invokeLater(() -> {
            int trialDays = LicenseManager.getTrialDaysLeft();

            if (trialDays == -1) {
                // Trial Expired -> Show Activation Screen
                new ActivationView().setVisible(true);
            } else {
                // Still in trial OR already activated
                if (trialDays != 999 && trialDays <= 2) {
                    // Show reminder if trial is running low (2 days or less)
                    JOptionPane.showMessageDialog(null, 
                        "PERHATIAN: Masa trial Anda tersisa " + trialDays + " hari lagi. Silakan hubungi Admin untuk aktivasi.", 
                        "Peringatan Trial", JOptionPane.WARNING_MESSAGE);
                }
                new DashboardView().setVisible(true);
            }
        });
    }
}