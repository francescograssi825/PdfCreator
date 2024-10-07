package ApiPdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.util.List;


public interface PdfAPI {

    public void creaPdf(List<String> data, String outfile);
    public PDDocument getPdf();
}
