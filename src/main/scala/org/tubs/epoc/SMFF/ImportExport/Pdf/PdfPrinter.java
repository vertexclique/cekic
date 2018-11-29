package org.tubs.epoc.SMFF.ImportExport.Pdf;

import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jgraph.JGraph;
import org.tubs.epoc.SMFF.ImportExport.Pdf.Graph.Presentation;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PdfPrinter {

  public static void convertToPdf(SystemModel systemModel, String filename) {
    JGraph graph = new Presentation(systemModel).getSystemGraph().getGraph();
    int width = graph.getWidth();
    int height = graph.getHeight();
    // step 1
    Document document = new Document(new com.lowagie.text.
        Rectangle(width, height));
    try {
      // step 2
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
      // step 3
      document.open();
      // step 4
      PdfContentByte cb = writer.getDirectContent();
      PdfTemplate tp = cb.createTemplate(width, height);
      Graphics2D g2 = tp.createGraphics(width, height,
          new DefaultFontMapper());
      // doesn't matter if you use printAll, paintAll, or anything else.
      //graph.paintAll(g2);
      graph.printAll(g2);
      g2.dispose();
      cb.addTemplate(tp, 0, 0);
      // add the creation date at the bottom.
      cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA,
          BaseFont.WINANSI, false), 6.0f);
      cb.moveTo(0, height - 30);
      cb.beginText();
      cb.showText("PDF File created " + (new java.util.Date()));
      cb.endText();
    }
    catch(DocumentException de) {
      de.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // step 5
    document.close();
  }
}
