package BarCodeGenerator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter; // Importa dalla libreria client.j2se
import com.google.zxing.MultiFormatWriter; // Usa MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BarcodeGenerator {
    public static String generateBarcode(String data, String filePath) throws WriterException, IOException {
        MultiFormatWriter barcodeWriter = new MultiFormatWriter(); // Usa MultiFormatWriter
        BitMatrix bitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_128, 200, 100);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Salva l'immagine come file
        ImageIO.write(image, "png", new File(filePath));
        return filePath;
    }

    public static void generateQRCode(String data, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Crea una mappa per le impostazioni
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Livello di correzione (opzionale)
        hints.put(EncodeHintType.MARGIN, 0); // Imposta il margine a zero

        // Crea il QR Code con le impostazioni
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200, hints);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", java.nio.file.Paths.get(filePath));
    }
}

