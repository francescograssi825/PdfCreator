package Documento;

import ApiPdf.PdfAPI;

import java.util.List;

public class TicketPDF extends Documento
{

    List<String> data;
    String outfile;

    public TicketPDF(PdfAPI pdfAPI, List<String> data, String outfile) {
        super(pdfAPI);
        this.data = data;
        this.outfile = outfile;
    }



    @Override
    public void invia(String indirizzo) {

        pdfAPI.creaPdf(data, outfile);
        System.out.printf(indirizzo + "\n");

    }
}