package ApiPdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.util.List;

public class PdfBillAPI implements PdfAPI {
    List<List<String>> itemData;
    PDDocument document;

    public PdfBillAPI(List<List<String>> itemData) {
        this.itemData = itemData;
    }

    @Override
    public void creaPdf(List<String> headerData, String outfile) {
        PDRectangle pageSize = PDRectangle.A4; // Dimensioni per la pagina della fattura (A4)

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(pageSize);
            doc.addPage(page);

            PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                // Disegna il logo
                drawLogo(contents, headerData.get(0), pageSize, doc);

                // Informazioni fornitore
                drawSupplierInfo(contents, headerData, pageSize);

                // Informazioni cliente
                drawCustomerInfo(contents, headerData);

                // Dati fattura (Numero fattura e Data)
                drawInvoiceInfo(contents, headerData, pageSize);

                // Tabella articoli
                drawTableHeader(contents);
                drawTableItems(contents, itemData);

                // Totale fattura
                drawTotal(contents, headerData.get(8), pageSize);
            }

            // Salva il documento PDF
            doc.save(outfile);
            document = doc;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Funzione per disegnare il logo
    private void drawLogo(PDPageContentStream contents, String logoPath, PDRectangle pageSize, PDDocument doc) throws IOException {
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoPath, doc);
        float logoWidth = 50;
        float logoHeight = 50;
        float logoX = 50;
        float logoY = pageSize.getHeight() - 100; // In alto a sinistra
        contents.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight);
    }

    // Funzione per disegnare le informazioni del fornitore
    private void drawSupplierInfo(PDPageContentStream contents, List<String> headerData, PDRectangle pageSize) throws IOException {
        PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        float textX = 50;
        float textY = pageSize.getHeight() - 130; // Sotto il logo

        contents.setNonStrokingColor(0, 0, 0); // Colore nero
        contents.setFont(fontBold, 12);

        contents.beginText();
        contents.newLineAtOffset(textX, textY);
        contents.showText("Fornitore: " + headerData.get(1)); // Nome fornitore
        contents.endText();

        contents.beginText();
        contents.newLineAtOffset(textX, textY - 15);
        contents.showText("Indirizzo: " + headerData.get(2)); // Indirizzo fornitore
        contents.endText();

        contents.beginText();
        contents.newLineAtOffset(textX, textY - 30);
        contents.showText("P.IVA: " + headerData.get(3)); // Partita IVA fornitore
        contents.endText();
    }

    // Funzione per disegnare le informazioni del cliente
    private void drawCustomerInfo(PDPageContentStream contents, List<String> headerData) throws IOException {
        PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        float textX = 50;
        float textY = 620; // Posizione sotto le informazioni del fornitore

        contents.setFont(fontBold, 12);

        contents.beginText();
        contents.newLineAtOffset(textX, textY);
        contents.showText("Cliente: " + headerData.get(4)); // Nome cliente
        contents.endText();

        contents.beginText();
        contents.newLineAtOffset(textX, textY - 15);
        contents.showText("Indirizzo Cliente: " + headerData.get(5)); // Indirizzo cliente
        contents.endText();
    }

    // Funzione per disegnare le informazioni della fattura (Numero e Data)
    private void drawInvoiceInfo(PDPageContentStream contents, List<String> headerData, PDRectangle pageSize) throws IOException {
        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        contents.setFont(font, 12);

        float infoX = pageSize.getWidth() - 250;
        float infoY = pageSize.getHeight() - 100; // In alto a destra

        contents.beginText();
        contents.newLineAtOffset(infoX, infoY);
        contents.showText("Numero Fattura: " + headerData.get(6)); // Numero fattura
        contents.endText();

        contents.beginText();
        contents.newLineAtOffset(infoX, infoY - 15);
        contents.showText("Data: " + headerData.get(7)); // Data fattura
        contents.endText();
    }

    // Funzione per disegnare l'intestazione della tabella
    private void drawTableHeader(PDPageContentStream contents) throws IOException {
        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        contents.setFont(font, 10);

        float tableX = 50;
        float tableY = 480; // Posizionamento della tabella

        contents.beginText();
        contents.newLineAtOffset(tableX, tableY);
        contents.showText("Descrizione Articolo");
        contents.newLineAtOffset(100, 0);
        contents.showText("Quantità");
        contents.newLineAtOffset(100, 0);
        contents.showText("Prezzo Unitario");
        contents.newLineAtOffset(100, 0);
        contents.showText("Totale");
        contents.endText();
    }

    // Funzione per disegnare gli articoli nella tabella
    private void drawTableItems(PDPageContentStream contents, List<List<String>> itemData) throws IOException {
        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        contents.setFont(font, 10);

        float tableX = 50;
        float tableY = 465; // Posizione degli articoli sotto l'intestazione
        float rowHeight = 15;
        float colWidth = 100;

        for (List<String> item : itemData) {
            contents.beginText();
            contents.newLineAtOffset(tableX, tableY);
            contents.showText(item.get(0)); // Descrizione
            contents.newLineAtOffset(colWidth, 0);
            contents.showText(item.get(1)); // Quantità
            contents.newLineAtOffset(colWidth, 0);
            contents.showText(item.get(2)); // Prezzo unitario
            contents.newLineAtOffset(colWidth, 0);
            contents.showText(item.get(3)); // Totale
            contents.endText();

            tableY -= rowHeight;
        }
    }

    // Funzione per disegnare il totale della fattura
    private void drawTotal(PDPageContentStream contents, String total, PDRectangle pageSize) throws IOException {
        PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        contents.setFont(fontBold, 12);

        float totalX = pageSize.getWidth() - 150;
        float totalY = 380; // Posizionamento sotto la tabella

        contents.beginText();
        contents.newLineAtOffset(totalX, totalY);
        contents.showText("Totale: " + total); // Totale fattura
        contents.endText();
    }

    @Override
    public PDDocument getPdf() {
        return document;
    }
}
