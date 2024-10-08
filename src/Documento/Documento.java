package Documento;

import ApiPdf.PdfAPI;

public abstract class Documento {

    protected PdfAPI pdfAPI;

    public Documento(PdfAPI pdfAPI) {
        this.pdfAPI = pdfAPI;
    }

    public abstract void invia(String indirizzo);

}
