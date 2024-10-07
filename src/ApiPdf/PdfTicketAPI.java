package ApiPdf;

import BarCodeGenerator.BarcodeGenerator;
import com.google.zxing.WriterException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix; // Importa la classe Matrix

import java.io.IOException;
import java.util.List;

import static BarCodeGenerator.BarcodeGenerator.generateQRCode;

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

                // Genera e aggiungi il codice a barre
                //String barcodeFilePath = data.get(6);
                //BarcodeGenerator.generateBarcode(data.get(2), barcodeFilePath);
                //drawBarcode(contents, barcodeFilePath, ticketSize, doc); // Passa il documento

                // Genera e aggiungi il codice QR
                String qrCodeFilePath = data.get(6); // Assicurati che questo sia il percorso corretto per il QR code
                generateQRCode(data.get(2), qrCodeFilePath); // Usa i dati necessari
                drawQRCode(contents, qrCodeFilePath, ticketSize, doc); // Passa il documento

            } catch (WriterException e) {
                throw new RuntimeException(e);
            }



            // Salva il documento PDF
            doc.save(outfile);
            document = doc;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Funzione per disegnare il codice a barre a sinistra
    private void drawBarcode(PDPageContentStream contents, String barcodePath, PDRectangle ticketSize, PDDocument doc) throws IOException {
        PDImageXObject barcodeImage = PDImageXObject.createFromFile(barcodePath, doc);
        System.out.println("Dimensioni codice a barre: " + barcodeImage.getWidth() + "x" + barcodeImage.getHeight());

        // Larghezza e altezza originali del codice a barre
        float barcodeWidth = barcodeImage.getWidth();
        float barcodeHeight = barcodeImage.getHeight();

        // Calcola la posizione per centrare verticalmente e posizionare a destra
        float barcodeX = ticketSize.getWidth() - 10; // Margine di 10 dal bordo destro
        float barcodeY = (ticketSize.getHeight() - barcodeWidth) / 2; // Centra verticalmente (usando la larghezza per l'altezza dopo la rotazione)

        // Inizia un nuovo gruppo grafico per le trasformazioni
        contents.saveGraphicsState();

        // Crea la matrice di trasformazione per ruotare l'immagine di 90 gradi in senso orario
        Matrix matrix = new Matrix();
        matrix.rotate(Math.toRadians(90)); // Ruota di 90 gradi in senso orario
        matrix.translate(barcodeX, barcodeY); // Trasla l'immagine nella posizione corretta

        // Applica la trasformazione
        contents.transform(matrix);

        // Disegna l'immagine del codice a barre ruotata
        // Inverti larghezza e altezza poiché l'immagine è ruotata
        contents.drawImage(barcodeImage, 0, 0, barcodeHeight, barcodeWidth); // Usa altezza e larghezza invertite

        // Ripristina lo stato grafico
        contents.restoreGraphicsState();
    }

    // Funzione per disegnare il QR Code
    private void drawQRCode(PDPageContentStream contents, String qrCodePath, PDRectangle ticketSize, PDDocument doc) throws IOException {
        PDImageXObject qrCodeImage = PDImageXObject.createFromFile(qrCodePath, doc);

        // Imposta le dimensioni desiderate per il QR Code
        float qrCodeScaleFactor = 0.40f; // Fattore di scala per ridurre le dimensioni (40%)
        float qrCodeWidth = qrCodeImage.getWidth() * qrCodeScaleFactor;
        float qrCodeHeight = qrCodeImage.getHeight() * qrCodeScaleFactor;

        // Posizione a destra
        float qrCodeX = ticketSize.getWidth() - qrCodeWidth - 10; // Margine di 10 dal bordo destro

        // Posiziona il QR Code più in basso, allineato con il logo
        float logoY = 20; // Distanza dal fondo del biglietto dove si trova il logo
        float qrCodeY = logoY; // Posiziona il QR Code alla stessa altezza del logo

        // Disegna il QR Code
        contents.drawImage(qrCodeImage, qrCodeX, qrCodeY, qrCodeWidth, qrCodeHeight);
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
