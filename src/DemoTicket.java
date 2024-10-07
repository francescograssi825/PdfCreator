import ApiPdf.PdfAPI;
import ApiPdf.PdfTicketAPI;
import Documento.TicketPDF;

import java.util.ArrayList;
import java.util.List;

public class DemoTicket {

    public static void main(String[] args) {
        String eventName = "nome evento";
        String date = "10 Novembre 2024";
        String ownerName = "Grassi francesco";
        String location = "Lecce";
        String imagePath = "src/resources/WhatsApp Image 2024-10-07 at 17.54.06_0754fe79.jpg"; //immagine di sfondo (immagine legata all'evento o all'artista)
        String logoPath = "src/resources/65df91835d145121850d4094.png"; // logo applicazione
        String outfile = "biglietti.pdf";

        List<String> data = new ArrayList<>();

        data.add(eventName);
        data.add(date);
        data.add(ownerName);
        data.add(location);
        data.add(imagePath);
        data.add(logoPath);
        // Creiamo un'istanza di ApiPdf.PdfAPI (PdfAPIImpl è la classe concreta che implementa ApiPdf.PdfAPI)
        PdfAPI pdfAPI = new PdfTicketAPI();

        // Creiamo un'istanza di Documento.BigliettoPDF passando l'istanza di ApiPdf.PdfAPI
        TicketPDF biglietto = new TicketPDF(pdfAPI, data, outfile);

        // Chiamare il metodo invia con l'indirizzo email o qualunque indirizzo tu voglia
        biglietto.invia("testo@esempio.com");
    }
}
