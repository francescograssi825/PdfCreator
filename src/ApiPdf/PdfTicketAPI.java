package ApiPdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import java.io.IOException;
import java.util.List;

public class PdfTicketAPI implements PdfAPI {
    PDDocument document;

    @Override
    public void creaPdf(List<String> data, String outfile) {
        PDRectangle ticketSize = new PDRectangle(600, 300); // Dimensioni per un biglietto rettangolare

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(ticketSize);
            doc.addPage(page);

            PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                // Disegna l'immagine di sfondo
                drawBackgroundImage(contents, data.get(4), ticketSize, doc);

                // Aggiungi sfumatura nera trasparente
                drawGradient(contents, ticketSize);

                // Ripristina l'opacità a 100% per il testo
                resetOpacity(contents);

                // Aggiungi il titolo (evento)
                drawEventTitle(contents, data.get(0), ticketSize, font);

                // Aggiungi i dettagli dell'evento (data, possessore, luogo)
                drawEventDetails(contents, data, ticketSize, font);

                // Aggiungi il logo
                drawLogo(contents, data.get(5), ticketSize, doc);
            }

            // Salva il documento PDF
            doc.save(outfile);
            document = doc;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Funzione per disegnare l'immagine di sfondo
    private void drawBackgroundImage(PDPageContentStream contents, String imagePath, PDRectangle ticketSize, PDDocument doc) throws IOException {
        PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
        float imageX = 0;
        float imageY = 0;
        float imageWidth = ticketSize.getWidth();
        float imageHeight = ticketSize.getHeight();
        contents.drawImage(pdImage, imageX, imageY, imageWidth, imageHeight);
    }

    // Funzione per aggiungere la sfumatura nera trasparente
    private void drawGradient(PDPageContentStream contents, PDRectangle ticketSize) throws IOException {
        int gradientSteps = 100;
        float gradientWidth = ticketSize.getWidth();
        float gradientHeight = ticketSize.getHeight();

        for (int i = 0; i < gradientSteps; i++) {
            float opacity = 1.0f - (i / (float) gradientSteps);  // Opacità decrescente da 1 a 0
            float stepWidth = gradientWidth / gradientSteps;

            // Imposta l'opacità
            PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
            graphicsState.setNonStrokingAlphaConstant(opacity);
            contents.setGraphicsStateParameters(graphicsState);

            // Imposta il colore nero
            contents.setNonStrokingColor(0, 0, 0);

            // Disegna un rettangolo nero con opacità variabile
            contents.addRect(i * stepWidth, 0, stepWidth, gradientHeight);
            contents.fill();
        }
    }

    // Funzione per ripristinare l'opacità al 100% per il testo
    private void resetOpacity(PDPageContentStream contents) throws IOException {
        PDExtendedGraphicsState opaqueState = new PDExtendedGraphicsState();
        opaqueState.setNonStrokingAlphaConstant(1.0f);
        contents.setGraphicsStateParameters(opaqueState);
    }

    // Funzione per disegnare il titolo dell'evento
    private void drawEventTitle(PDPageContentStream contents, String title, PDRectangle ticketSize, PDFont font) throws IOException {
        contents.setNonStrokingColor(1.0f, 1.0f, 1.0f);  // Colore del testo bianco

        contents.beginText();
        contents.setFont(font, 18);  // Font più grande per il titolo
        float titleX = 50;  // Allinea il testo a sinistra (50 punti dal bordo sinistro)
        float titleY = ticketSize.getHeight() - 60;  // Inizia a circa 60 punti dal bordo superiore
        contents.newLineAtOffset(titleX, titleY);
        contents.showText(title);  // Nome dell'evento
        contents.endText();
    }

    // Funzione per disegnare i dettagli dell'evento (data, possessore, luogo)
    private void drawEventDetails(PDPageContentStream contents, List<String> data, PDRectangle ticketSize, PDFont font) throws IOException {
        contents.setNonStrokingColor(1.0f, 1.0f, 1.0f);  // Colore del testo bianco

        contents.beginText();
        contents.setFont(font, 12);  // Font più piccolo per i dettagli
        float detailsX = 50;  // Allinea i dettagli a sinistra
        float detailsY = ticketSize.getHeight() - 110;  // Posiziona sotto il titolo
        contents.newLineAtOffset(detailsX, detailsY);
        contents.showText("Data: " + data.get(1));
        contents.newLineAtOffset(0, -20);  // Sposta il testo di 20 punti sotto
        contents.showText("Possessore: " + data.get(2));
        contents.newLineAtOffset(0, -20);  // Sposta il testo di 20 punti sotto
        contents.showText("Luogo: " + data.get(3));
        contents.endText();
    }

    // Funzione per disegnare il logo
    private void drawLogo(PDPageContentStream contents, String logoPath, PDRectangle ticketSize, PDDocument doc) throws IOException {
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoPath, doc);
        float logoMargin = 50;
        float logoWidth = 75;  // Larghezza del logo
        float logoHeight = 75;  // Altezza del logo
        float logoX = logoMargin;
        float logoY = logoMargin - 20;  // Margine dal bordo inferiore
        contents.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight);
    }

    @Override
    public PDDocument getPdf() {
        return document;
    }
}
