package Documento;

import ApiPdf.PdfAPI;

import java.util.List;

public class BillPDF extends Documento{
    List<String> data;
    String outfile;
    public BillPDF(PdfAPI pdfAPI, List<String> data, String outfile) {
        super(pdfAPI);
        this.data = data;
        this.outfile = outfile;
    }

    @Override
    public void invia(String indirizzo) {
        pdfAPI.creaPdf(data, outfile);

    }
}
