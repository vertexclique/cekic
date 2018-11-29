package org.tubs.epoc.SMFF.PyCPAInterface.Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.jdom.JDOMException;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelSaver;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;

public class PyCPAAnalysis implements Callable<Object>{
  private SystemModel model;
  private String pythonBinary;
  private String pyCPAPath;

  public PyCPAAnalysis(SystemModel model, String pythonBinary, String pyCPAPath) {
    super();
    this.model = model;
    this.pythonBinary = pythonBinary;
    this.pyCPAPath = pyCPAPath;
  }


  /**
   * Callable interface of the PyCPAAnalysis which allows to run the analysis in a multi-threaded manner.
   * This can be used e.g. to submit several PyCPA instances into a Java ExecutorService and collect results
   * afterwards.
   */
  @Override
  public Object call() throws Exception {
    analyze();
    return null;
  }

  public void analyze() throws IOException{
    // create a temporary file to store the system model and receive analysis results from PyCPA
    File tempSystemFile;
    String tempSystemFileName;
    tempSystemFile = File.createTempFile("SMFFsystem", ".xml");
    tempSystemFileName = tempSystemFile.getAbsolutePath();
    new ModelSaver(tempSystemFile).saveModel(model);

    // call pyCPA with the given system model
    String examplePath = pyCPAPath+"\\..\\tools\\smff_loader.py";
    String pathArgs[] = {new String("PYTHONPATH="+pyCPAPath)};
    String command = pythonBinary +" "+ examplePath + " -f "+tempSystemFileName + " -of "+tempSystemFileName;
    System.out.println(command+", "+pathArgs[0]);
    try {
      // execute pyCPA
      Process proc = Runtime.getRuntime().exec(command, pathArgs);
      InputStream output = proc.getInputStream();
      InputStream error = proc.getErrorStream();
      BufferedReader _output_ = new BufferedReader(new InputStreamReader(output));
      BufferedReader _error_ = new BufferedReader(new InputStreamReader(error));
      String line = null;
      while((line=_output_.readLine())!=null)System.out.println(line);
      while((line=_error_.readLine())!=null) System.out.println(line);
      proc.waitFor();
      // read results from XML and save them in the system model
      new AnalysisResultParser(model, tempSystemFile).readResults();
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally{
      // delete the temporary file afterwards
      tempSystemFile.delete();
    }
  }
}
