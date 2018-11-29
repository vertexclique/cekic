package org.tubs.epoc.SMFF.PyCPAInterface.example;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelLoader;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelSaver;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.PyCPAInterface.Analysis.PyCPAAnalysis;

public class AnalysisExample {

  /**
   * @param args
   */
  public static Logger  logger = Logger.getLogger("org.tubs.epoc.SMFF");

  public static void main(String[] args) {
  logger.setLevel(Level.WARN);
  BasicConfigurator.configure();
    try {
      // Paths and files
      String systemFile = System.getProperty("user.dir")+"\\src\\org\\tubs\\epoc\\SMFF\\PyCPAInterface\\example\\smff_system.xml";
      String systemOutFile = System.getProperty("user.dir")+"\\src\\org\\tubs\\epoc\\SMFF\\PyCPAInterface\\example\\smff_system_annotated.xml";
      String pythonBinary = "C:\\Programme\\Python2.7.2\\python.exe";
      String pyCPASourcePath = "C:\\Users\\moritzn\\workspaceJava\\pycpa\\src";
      
      // create model loader
      ModelLoader modelLoader = new ModelLoader(systemFile);
      // load system model
      SystemModel model = modelLoader.generateSystem();
      // instantiate PyCPA analysis
      PyCPAAnalysis pycpa = new PyCPAAnalysis(model, pythonBinary, pyCPASourcePath);
      // analyze
      pycpa.analyze();
      //save
      new ModelSaver(systemOutFile).saveModel(model);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
