/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author noroi
 */

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {
    public static String rupiah(double nilai) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return "Rp " + nf.format(nilai);
    }
}
