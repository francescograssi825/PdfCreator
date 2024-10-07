import ApiPdf.PdfAPI;
import ApiPdf.PdfBillAPI;
import Documento.BillPDF;

import java.util.Arrays;
import java.util.List;

public class DemoBill {

    public static void main(String[] args) {
        PdfAPI pdfAPI = getPdfAPI();

        List<String> headerData = Arrays.asList(
                "src/resources/65df91835d145121850d4094.png",         // Path dell'immagine del logo aziendale
                "Azienda Srl",                    // Nome del venditore/azienda
                "Via Roma 1, 20100 Milano, Italia", // Indirizzo del venditore/azienda
                "IT123456789",                    // Partita IVA del venditore/azienda
                "Mario Rossi",                    // Nome del cliente
                "Via Verdi 2, 80100 Napoli, Italia", // Indirizzo del cliente
                "0001",                           // Numero della fattura
                "01/10/2024",                     // Data della fattura
                "€ 2290.00"                       // Totale complessivo della fattura
        );

        BillPDF fattura = new BillPDF(pdfAPI, headerData, "fatture.pdf");
        fattura.invia("testo@esempio.com");
    }

    private static PdfAPI getPdfAPI() {
        List<List<String>> itemData = Arrays.asList(  //dati relativi all'ordine
                Arrays.asList("Computer portatile", "2", "€ 700.00", "€ 1400.00"),
                Arrays.asList("Mouse Wireless", "5", "€ 20.00", "€ 100.00"),
                Arrays.asList("Monitor 24 pollici", "3", "€ 150.00", "€ 450.00"),
                Arrays.asList("Servizio di installazione", "1", "€ 100.00", "€ 100.00"),
                Arrays.asList("Software Office", "3", "€ 80.00", "€ 240.00")
        );

        return new PdfBillAPI(itemData);
    }
}

