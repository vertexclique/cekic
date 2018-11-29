package org.tubs.epoc.SMFF.PyCPAInterface.Analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.PJdTimingBehavior;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Timing.AbstractActivationPattern;
import org.tubs.epoc.SMFF.ModelElements.Timing.PJActivation;
import org.tubs.epoc.SMFF.PyCPAInterface.Analysis.ModelExtensions.ResourceAnalysisResult;
import org.tubs.epoc.SMFF.PyCPAInterface.Analysis.ModelExtensions.SchedElemAnalysisResult;

public class AnalysisResultParser {
  private File analyzedModel;
  private SystemModel model;

  /**
   * 
   * @param model - SMFF system model to annotate with the analysis results
   * @param analysisResults - SMFF system File containing the annotation of analysis results from PyCPA
   */
  public AnalysisResultParser(SystemModel model, File analyzedModel) {
    super();
    this.analyzedModel = analyzedModel;
    this.model = model;
  }

  /**
   * reads the analysis result to the system model
   * @param model
   * @throws IOException 
   * @throws JDOMException 
   */
  public void readResults() throws JDOMException, IOException{
    // parse the XML file using SAX
    SAXBuilder builder = new SAXBuilder();
    FileInputStream inputStream = new FileInputStream(analyzedModel);
    Document document = builder.build(inputStream);
    // retrieve the root element and store it in this factory
    Element systemElement = document.getRootElement();
    Element analysisResults = systemElement.getChild("Analysis");

    // get results of resources
    Element resourcesResults = analysisResults.getChild("Resources");
    @SuppressWarnings("unchecked")
    List<Element> resList = resourcesResults.getChildren("Resource");
    for (Element resResult : resList) {
      String resString = resResult.getAttributeValue("ID");
      String loadString = resResult.getAttributeValue("load");
      Resource res = model.getResource(Integer.valueOf(resString));

      res.addExtData(new ResourceAnalysisResult(Double.valueOf(loadString)), false, true, false);
    }
    @SuppressWarnings("unchecked")
    List<Element> cresList = resourcesResults.getChildren("CommResource");
    for (Element cresResult : cresList) {
      String cresString = cresResult.getAttributeValue("ID");
      CommResource res = model.getCommResource(Integer.valueOf(cresString));

      res.addExtData(new ResourceAnalysisResult(cresResult), false, true, false);
    }

    // get results of schedulable elements
    Element applicationsResults = analysisResults.getChild("Applications");
    @SuppressWarnings("unchecked")
    List<Element> appList = applicationsResults.getChildren("Application");
    for (Element appElem : appList) {
      String appString = appElem.getAttributeValue("appID");
      String appVString = appElem.getAttributeValue("appV");
      int appPeriod = 0;

      // get activation period of some source task (will be used for output behavior of all tasks)
      ApplicationModel app = model.getApplication(Integer.valueOf(appString));
      for(Task task : app.getTaskList().values()){
        if(task.getTrgLinkList().size()==0){
          AbstractActivationPattern actPattern = task.getActiveProfile().getActivationPattern();
          if(actPattern instanceof PJActivation){
            appPeriod = ((PJActivation)actPattern).getActivationPeriod();
            break;
          }
        }
      }

      // results of tasks
      @SuppressWarnings("unchecked")
      List<Element> taskList = appElem.getChildren("Task");
      for (Element taskResult : taskList) {
        String taskString = taskResult.getAttributeValue("id");
        TaskIdentifier taskId = new TaskIdentifier(Integer.valueOf(appString), Integer.valueOf(appVString), Integer.valueOf(taskString));
        Task task = model.getTask(taskId);


        // get bcrt and wcrt
        String bcrtString = taskResult.getAttributeValue("bcrt");
        Integer bcrt = Integer.valueOf(bcrtString);
        task.setBCRT(bcrt);
        String wcrtString = taskResult.getAttributeValue("wcrt");
        Integer wcrt = Integer.valueOf(wcrtString);
        task.setWCRT(wcrt);

        // get jitter values
        String inJitterString = taskResult.getAttributeValue("input_jitter");
        String outJitterString = taskResult.getAttributeValue("output_jitter");
        int inJitter = Integer.valueOf(inJitterString);
        task.setInputBehavior(new PJdTimingBehavior(appPeriod, inJitter, 0));
        int outJitter = Integer.valueOf(outJitterString);
        task.setOutputBehavior(new PJdTimingBehavior(appPeriod, outJitter, 0));
        
        // add schedulable element analysis result as data extension (kind of redundant to results directly in task but might come in handy)
        task.addExtData(new SchedElemAnalysisResult(bcrt, wcrt, appPeriod, inJitter, outJitter), false, true, false);
      }


      // results of taskslinks
      @SuppressWarnings("unchecked")
      List<Element> tasklinkList = appElem.getChildren("TaskLink");
      for (Element tasklinkResult : tasklinkList) {
        String tasklinkString = tasklinkResult.getAttributeValue("id");
        TaskLink tasklink = app.getTaskLink(Integer.valueOf(tasklinkString));


        // get bcrt and wcrt
        String bcrtString = tasklinkResult.getAttributeValue("bcrt");
        Integer bcrt = Integer.valueOf(bcrtString);
        tasklink.setBCRT(bcrt);
        String wcrtString = tasklinkResult.getAttributeValue("wcrt");
        Integer wcrt = Integer.valueOf(wcrtString);
        tasklink.setWCRT(wcrt);

        // get jitter values
        String inJitterString = tasklinkResult.getAttributeValue("input_jitter");
        String outJitterString = tasklinkResult.getAttributeValue("output_jitter");
        int inJitter = Integer.valueOf(inJitterString);
        tasklink.setInputBehavior(new PJdTimingBehavior(appPeriod, inJitter, 0));
        int outJitter = Integer.valueOf(outJitterString);
        tasklink.setOutputBehavior(new PJdTimingBehavior(appPeriod, outJitter, 0));
        
        // add schedulable element analysis result as data extension (kind of redundant to results directly in task but might come in handy)
        tasklink.addExtData(new SchedElemAnalysisResult(bcrt, wcrt, appPeriod, inJitter, outJitter), false, true, false);
      }
    }
    inputStream.close();
  }
}
