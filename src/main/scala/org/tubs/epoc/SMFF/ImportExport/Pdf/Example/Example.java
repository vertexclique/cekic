package org.tubs.epoc.SMFF.ImportExport.Pdf.Example;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tubs.epoc.SMFF.ImportExport.Pdf.PdfPrinter;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelLoader;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;

/***
 * This Example demonstrates how to print the graph of a given system model as pdf. This
 * comes quite handy when you generate a couple of (especially larger) testcase systems
 * and want to find out what the generated topology actually looks like.
 * 
 * @author moritzn
 *
 */
public class Example {
  public static Logger  logger = Logger.getLogger("org.tubs.epoc.SMFF");

  public static void main(String[] args) {
  logger.setLevel(Level.WARN);
  BasicConfigurator.configure();

    // create a new and empty system model
    SystemModel systemModel = new SystemModel();

    // specify the path to which the testcases are to be stored
    String inputFile = "C://Testcases//System.xml";
    String outputPath = "C://Testcases//";
    new File(outputPath).mkdirs();

    //-------------------------------------
    // LOAD A SYSTEM MODEL FROM AN XML FILE
    // (you could as well just use the
    //  system factories to generate one)
    //-------------------------------------
    // save the model to an XML file
    try {
      systemModel = new ModelLoader(inputFile).generateSystem();
    } catch (Exception e) {
      logger.error("Error loading system model");
      return;
    }

    //-------------------------------------
    // PRINT THE SYSTEM MODEL AS PDF
    //-------------------------------------
    PdfPrinter.convertToPdf(systemModel, outputPath+"sampleGraph.pdf");
  }
}