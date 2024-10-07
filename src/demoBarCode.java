import java.io.IOException;
import BarCodeGenerator.BarcodeGenerator;
import com.google.zxing.WriterException;

public class demoBarCode {

    public static void main(String[] args) {
        try {
            String barcodeData = "123456789"; // Dati da codificare nel codice a barre
            String filePath = "barcode.png"; // Percorso per salvare l'immagine
            BarcodeGenerator.generateBarcode(barcodeData, filePath);
            System.out.println("Codice a barre generato: " + filePath);
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }

}
