package util;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author noroi
 */

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dao.PenjualanDAO;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PdfCetak {
    public static void cetakBon(String nomorFaktur) {
       
        PenjualanDAO dao = new PenjualanDAO();
        PenjualanDAO.Detail transaksi = dao.findDetail(nomorFaktur);
        List<PenjualanDAO.BarisDetail> detail = dao.findBaris(nomorFaktur);

        if (transaksi == null) return;

        String namaFile = "Bon_" + nomorFaktur + ".pdf";

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(namaFile));
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            Paragraph title = new Paragraph("NOTA PENJUALAN", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Tanggal : " + transaksi.tanggal(), fontNormal));
            document.add(new Paragraph("Kepada  : " + transaksi.pelanggan(), fontNormal));
            document.add(new Paragraph("No. Bon : " + transaksi.nomorFaktur(), fontNormal));
            document.add(new Paragraph("-------------------------------------------------------------------------------------------------", fontNormal));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{1f, 4f, 2f, 2f});
            table.setWidthPercentage(100);
            
            table.addCell(new PdfPCell(new Phrase("Qty", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Nama Barang", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Harga", fontBold)));
            table.addCell(new PdfPCell(new Phrase("Jumlah", fontBold)));

            for (PenjualanDAO.BarisDetail b : detail) {
                table.addCell(new Phrase(String.valueOf(b.qty()), fontNormal));
                table.addCell(new Phrase(b.nama(), fontNormal));
                table.addCell(new Phrase(FormatUtil.rupiah(b.harga()), fontNormal));
                table.addCell(new Phrase(FormatUtil.rupiah(b.subtotal()), fontNormal));
            }
            
            document.add(table);
            document.add(new Paragraph("-------------------------------------------------------------------------------------------------", fontNormal));
            document.add(new Paragraph(" "));

            Paragraph total = new Paragraph("Jumlah Rp. " + FormatUtil.rupiah(transaksi.total()), fontBold);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Paragraph ttd = new Paragraph("Tanda Terima", fontNormal);
            ttd.setAlignment(Element.ALIGN_RIGHT);
            document.add(ttd);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("__________________", fontNormal));

            document.close();
            
            // Auto open PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(namaFile));
            }

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Gagal cetak PDF: " + e.getMessage());
        }
    }
}