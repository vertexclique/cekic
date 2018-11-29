package org.tubs.epoc.SMFF.ImportExport.XML;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.tubs.epoc.SMFF.ModelElements.AbstractDataExtension;
import org.tubs.epoc.SMFF.ModelElements.ExtendibleModelElement;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Profile;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceGroup;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceType;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.GenericResourceGroup;
import org.tubs.epoc.SMFF.ModelElements.Platform.GenericResourceType;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Platform.ResourceGraph;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractScheduler;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractSchedulingParameter;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.SPPScheduler;
import org.tubs.epoc.SMFF.ModelElements.Timing.AbstractActivationPattern;
import org.tubs.epoc.SMFF.ModelElements.Timing.PJActivation;

/**
 * This class generates a {@link org.tubs.epoc.SMFF.ModelElements.SystemModel SystemModel} from an xml file.
 * 
 * @see SystemModel
 * 
 */
public class ModelLoader {
  private static Log logger = LogFactory.getLog(ModelLoader.class);

  private File file;
  private Element systemElement;

  private HashMap<String, HashMap<String, Class<?>>> defaultClassMap;

  public ModelLoader(String fileName) throws IOException {
    this.file = new File(fileName);
    this.systemElement = null;

    // try to open the supplied file
    try {
      openFile();
    } catch (IOException e) {
      // don't do anything, just don't open the file
      logger.error("Error during oping file", e);
    }
  }

  public ModelLoader(URI fileName) throws IOException {
    this.file = new File(fileName);
    this.systemElement = null;

    // try to open the supplied file
    try {
      openFile();
    } catch (IOException e) {
      // don't do anything, just don't open the file
      logger.error("Error during oping file", e);
    }
  }

  /**
   * Opens the file with the name given in fileName and stores a JDOM element representing the XML content in the
   * systemElement field
   * 
   * @throws IOException
   *           if problems with the files occur
   */
  private void openFile() throws IOException {
    try {
      // parse the XML file using SAX
      SAXBuilder builder = new SAXBuilder();
      Document document = builder.build(file);
      // retrieve the root element and store it in this factory
      systemElement = document.getRootElement();
    } catch (JDOMException e) {
      throw new IOException(e.getMessage());
    } catch (NullPointerException e) {
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Generates a system model from the xml file passed to this instance through its constructor.
   * <p>
   * A valid xml description must include extension information, as well as platform specific ad application specific
   * information.
   * 
   * @return an instance of {@link org.tubs.epoc.SMFF.ModelElements.SystemModel SystemModel} .
   * @throws Exception 
   */
  public SystemModel generateSystem() throws Exception {
    SystemModel systemModel;
    defaultClassMap = new HashMap<String, HashMap<String, Class<?>>>();

    // if systemElement is not assigned - give it another try to load
    if (systemElement == null) {
      try {
        openFile();
      } catch (IOException e) {
        logger.error("Cannot generate the system", e);
      }
    }

    // if loading was not successful - bail out
    if (systemElement == null)
      throw new IllegalStateException("Could not open file to load from");

    // load default class map
    this.genDefaultClassMap();

    // create sytem model Object
    systemModel = new SystemModel();

    // generate extensions
    insertExtensions(systemElement, systemModel);

    // four- stage loading process
    // each stage could be replaced by using a factory
    // to automatically create the specific part of the model
    // 1 - load the platform description
    insertPlatform(systemModel);
    // 2 - load the application description - this includes the mapping
    insertApplications(systemModel);

    // done - return complete model
    return systemModel;
  }

  @SuppressWarnings("unchecked")
  private void genDefaultClassMap(){
    Element configurationXML = systemElement.getChild("Configuration");
    // iterate over all configured default class groups
    Collection<Element> classGroups = (Collection<Element>) configurationXML.getChildren();
    for(Element classGroup : classGroups){
      // get the name of this class group
      String classGroupName = classGroup.getName();
      // if no map for this class group exists yet, create one
      if(defaultClassMap.get(classGroupName)==null){
        defaultClassMap.put(classGroupName, new HashMap<String, Class<?>>());
      }
      HashMap<String, Class<?>> classGroupMap = defaultClassMap.get(classGroupName);
      Collection<Element> classGroupDefaults = (Collection<Element>) classGroup.getChildren();
      // insert all default classes for this class group
      for(Element defaultClassMap : classGroupDefaults){
        String name = defaultClassMap.getName();//AttributeValue("name");
        String defaultClassString = (String) defaultClassMap.getAttributeValue("classname");
        Class<?> defaultClass;
        try {
          defaultClass = Class.forName(defaultClassString);
          classGroupMap.put(name, defaultClass);
        } catch (ClassNotFoundException e) {
          logger.error("Class "+defaultClassString+" not found.");
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void insertConstraints(Element appE, ApplicationModel app) throws NumberFormatException,
  IllegalStateException {
    Element constraints = appE.getChild("Constraints");
    if (constraints == null) {
      return;
    }
    // unchecked cast
    // for all subElements named Constraint -- the latency constraints of the application
    for (Element constraint : (Collection<Element>) constraints.getChildren("Constraint")) {

      // retrieve the string values found in the XML file -- may be null
      // if not supplied or empty if no value is assigned
      String startTaskStr = constraint.getAttributeValue("StartTaskId");
      String endTaskStr = constraint.getAttributeValue("EndTaskId");
      String latencyConstrStr = constraint.getAttributeValue("Constraint");

      // the int representations we are actually interested in
      int startTaskId;
      int endTaskId;
      double latencyConstr;

      if (startTaskStr != null) {
        startTaskId = Integer.parseInt(startTaskStr);
      } else {
        throw new IllegalStateException("Constraint must have start task");
      }

      if (endTaskStr != null) {
        endTaskId = Integer.parseInt(endTaskStr);
      } else {
        throw new IllegalStateException("Constraint must have end task");
      }

      if (latencyConstrStr != null) {
        latencyConstr = Double.parseDouble(latencyConstrStr);
      } else {
        throw new IllegalStateException("Constraint must have constraint value");
      }

      // try to get the tasks that the link will be connected to
      Task startTask = app.getTask(startTaskId);
      Task endTask = app.getTask(endTaskId);

      if (startTask == null || endTask == null) {
        throw new IllegalStateException("Cannot find tasks of the constraint");
      }

      // generate system latency constraint and add to application
      app.addSysLatConstr(startTask, endTask, latencyConstr);
    }
  }

  @SuppressWarnings("unchecked")
  private void insertApplications(SystemModel systemModel) throws Exception {
    Element applicationsElement = systemElement.getChild("Applications");
    // unchecked cast
    Collection<Element> applications = (Collection<Element>) applicationsElement.getChildren("Application");
    for (Element appE : applications) {
      String appIDString = appE.getAttributeValue("appID");
      String appVString = appE.getAttributeValue("appV");

      int appID;
      int appV;

      // TODO: default values
      if (appIDString != null) {
        appID = Integer.parseInt(appIDString);
      } else {
        throw new IllegalStateException("appID must be given for all applications");
      }

      if (appVString != null) {
        appV = Integer.parseInt(appVString);
      } else {
        throw new IllegalStateException("Every Application must have an appV");
      }

      // instantiate global application model
      ApplicationModel app = new ApplicationModel(systemModel, appID, appV);
      // first insert into system, then start to add tasks and task links
      systemModel.addApplication(app);

      // generate extensions
      insertExtensions(appE, app);

      // insert all tasks into the global model
      insertTasks(appE, app);

      // insert all task links into the global model
      insertTaskLinks(appE, app);

      // apply mapping
      applyMapping(systemModel, appE, app);
      
      // insert scheduling parameters
      insertSchedParams(appE, app);

      // insert all path latency constraint
      insertConstraints(appE, app);
    }

  }

  /**
   * @param systemModel
   * @param appE
   * @param app
   * @throws IllegalStateException
   * @throws NumberFormatException
   */
  @SuppressWarnings("unchecked")
  private void applyMapping(SystemModel systemModel, Element appE, ApplicationModel app) throws IllegalStateException,
  NumberFormatException {

    // parse mapping info
    Element mappingElement = appE.getChild("Mapping");

    if (mappingElement == null) {
      throw new IllegalStateException("No mapping defined!");
    }

    for (Element mapE : (Collection<Element>) mappingElement.getChildren("mapTask")) {
      String tid = mapE.getAttributeValue("tid");
      String rid = mapE.getAttributeValue("rid");

      if (tid == null || rid == null) {
        throw new IllegalStateException("Mapping must provide task and resource id (tid and rid)");
      }

      int taskID = Integer.parseInt(tid);
      int resID = Integer.parseInt(rid);

      Task task = app.getTask(taskID);
      Resource resource = systemModel.getResource(resID);

      if (task == null || resource == null) {
        throw new IllegalStateException("Did not find task or resource to map");
      }
      logger.info("Mapping Task " + tid + " to " + rid);
      app.mapTask(task, resource);
    }

    for (Element mapE : (Collection<Element>) mappingElement.getChildren("mapLink")) {
      String lid = mapE.getAttributeValue("lid");
      String rid = mapE.getAttributeValue("rid");
      String crid = mapE.getAttributeValue("crid");

      if (lid == null) {
        throw new IllegalStateException("Mapping must provide task link");
      } else if ((rid == null && crid == null) || (rid != null && crid != null)) {
        throw new IllegalStateException("Mapping must provide either a resource ID or comm resource ID (rid XOR crid)");
      }

      int taskLinkID = Integer.parseInt(lid);
      TaskLink tasklink = app.getTaskLink(taskLinkID);

      int resID;
      AbstractResource resource = null;
      if (rid != null) {
        resID = Integer.parseInt(rid);
        resource = systemModel.getResource(resID);
      } else if (crid != null) {
        resID = Integer.parseInt(crid);
        resource = systemModel.getCommResource(resID);
      }

      if (tasklink == null || resource == null) {
        throw new IllegalStateException("Did not find task link or resource to map");
      }

      app.mapTaskLink(tasklink, resource);
    }
  }

  /**
   * @param appE
   * @param app
   * @throws NumberFormatException
   * @throws IllegalStateException
   */
  @SuppressWarnings("unchecked")
  private void insertTaskLinks(Element appE, ApplicationModel app) throws NumberFormatException, IllegalStateException {
    // unchecked cast
    // for all subElements named TaskLink -- the task links of the application
    for (Element taskLink : (Collection<Element>) appE.getChildren("TaskLink")) {

      // retrieve the string values found in the XML file -- may be null
      // if not supplied or empty if no value is assigned
      String shortName = taskLink.getAttributeValue("shortName");
      String linkIDStr = taskLink.getAttributeValue("ID");
      String srcTStr = taskLink.getAttributeValue("src");
      String trgTStr = taskLink.getAttributeValue("trgt");

      // the int representations we are actually interested in
      int linkId;
      int srcTaskId;
      int trgTaskId;
      int wcet;
      int bcet;

      // TODO: default values
      if (linkIDStr != null) {
        linkId = Integer.parseInt(linkIDStr);
      } else {
        throw new IllegalStateException("Task Link must have ID");
      }

      if (srcTStr != null) {
        srcTaskId = Integer.parseInt(srcTStr);
      } else {
        throw new IllegalStateException("Task Link must have source task");
      }

      if (trgTStr != null) {
        trgTaskId = Integer.parseInt(trgTStr);
      } else {
        throw new IllegalStateException("Task link must have target task");
      }
      
      // try to get the tasks that the link will be connected to
      Task sourceTask = app.getTask(srcTaskId);
      Task targetTask = app.getTask(trgTaskId);

      if (sourceTask == null || targetTask == null) {
        throw new IllegalStateException("Cannot find tasks at the end of task link");
      }

      // generate task link
      TaskLink tl = new TaskLink(shortName, app, linkId, srcTaskId, trgTaskId);
      // add to source Task
      sourceTask.addTaskLink(tl);
      // add to target Task
      targetTask.addTaskLink(tl);
      
      // generate Profiles

      // add all profiles
      for (Element profE : (List<Element>) taskLink.getChildren("Profile")) {
        String bcetStr = profE.getAttributeValue("bcet");
        String wcetStr = profE.getAttributeValue("wcet");
        String activeStr = profE.getAttributeValue("active");

        Element actPatternXML = profE.getChild("ActivationPattern");
        AbstractActivationPattern actPattern = null;
        if(actPatternXML != null){
          try{
            actPattern = (AbstractActivationPattern) XMLFactory.fromXML(actPatternXML, defaultClassMap);
          } catch (Exception exception){
            logger.error("error isntantiating activation pattern. class not found");
          }
        }

        if (wcetStr != null) {
          wcet = Integer.parseInt(wcetStr);
        } else {
          throw new IllegalStateException("WCET must be defined in all profiles");
        }

        if (bcetStr != null) {
          bcet = Integer.parseInt(bcetStr);
        } else {
          // default to bcet = wcet
          bcet = wcet;
        }

        AbstractResourceType resType;
        AbstractResourceGroup resGroup;
        try{
          Element allowedResTypeXML = (Element) profE.getChild("ResourceType").getChildren().get(0);
          Element allowedResGroupXML = (Element) profE.getChild("ResourceGroup").getChildren().get(0);
          resType = (AbstractResourceType) XMLFactory.fromXML(allowedResTypeXML, defaultClassMap);
          resGroup = (AbstractResourceGroup) XMLFactory.fromXML(allowedResGroupXML, defaultClassMap);
        } catch(Exception e){
          // if these elements are not in the model (e.g. for older format models) just use the generic types
          resType = new GenericResourceType();
          resGroup = new GenericResourceGroup();
        }

        // ignore most parameters for now
        // TODO: add XML fields for the other parameters (defaulting to 0)
        Profile prof = new Profile(bcet, wcet, actPattern, resType, resGroup);
        // add to possible resource definition around it.
        tl.addProfile(prof);

        // if this profile is active, mark it in the task          
        if(activeStr!=null && Boolean.valueOf(activeStr)){
          tl.setActiveProfile(prof);
        }
      }

      // generate extensions
      insertExtensions(taskLink, tl);

      // add to application
      app.addTaskLink(tl);
    }
  }

  @SuppressWarnings("unchecked")
  private void insertTasks(Element appE, ApplicationModel app) throws Exception {
    // unchecked cast
    // get all subElements named Task -the tasks of the application
    for (Element task : (Collection<Element>) appE.getChildren("Task")) {
      String taskIDString = task.getAttributeValue("ID");
      String prioString = task.getAttributeValue("prio");

      int taskID;

      // TODO: default values?
      if (taskIDString != null) {
        taskID = Integer.parseInt(taskIDString);
      } else {
        throw new IllegalStateException("Task must be assigned a taskID");
      }

      // may be null - but that's OK
      String shortName = task.getAttributeValue("shortName");

      // generate Task
      Task gTask = new Task(shortName, app, taskID);

      // generate extensions
      insertExtensions(task, gTask);

      // add all profiles
      for (Element profE : (List<Element>) task.getChildren("Profile")) {
        String bcetStr = profE.getAttributeValue("bcet");
        String wcetStr = profE.getAttributeValue("wcet");
        String activeStr = profE.getAttributeValue("active");

        Element actPatternXML = profE.getChild("ActivationPattern");
        AbstractActivationPattern actPattern = null;
        if(actPatternXML != null){
          try{
            actPattern = (AbstractActivationPattern) XMLFactory.fromXML(actPatternXML, defaultClassMap);
          } catch (Exception exception){
            logger.error("error isntantiating activation pattern. class not found");
          }
        }
        if(actPattern==null){
          if (prioString != null) {
            String pStr = profE.getAttributeValue("period");
            String jStr = profE.getAttributeValue("jitter");
            actPattern = new PJActivation(Integer.valueOf(pStr), Integer.valueOf(jStr));
          } else {
            throw new IllegalStateException("Task must have a priority assignment");
          }
        }


        int bcet;
        int wcet;

        if (wcetStr != null) {
          wcet = Integer.parseInt(wcetStr);
        } else {
          throw new IllegalStateException("WCET must be defined in all profiles");
        }

        if (bcetStr != null) {
          bcet = Integer.parseInt(bcetStr);
        } else {
          // default to bcet = wcet
          bcet = wcet;
        }

        AbstractResourceType resType;
        AbstractResourceGroup resGroup;
        try{
          Element allowedResTypeXML = (Element) profE.getChild("ResourceType").getChildren().get(0);
          Element allowedResGroupXML = (Element) profE.getChild("ResourceGroup").getChildren().get(0);
          resType = (AbstractResourceType) XMLFactory.fromXML(allowedResTypeXML, defaultClassMap);
          resGroup = (AbstractResourceGroup) XMLFactory.fromXML(allowedResGroupXML, defaultClassMap);
        } catch(Exception e){
          // if these elements are not in the model (e.g. for older format models) just use the generic types
          resType = new GenericResourceType();
          resGroup = new GenericResourceGroup();
        }

        // ignore most parameters for now
        // TODO: add XML fields for the other parameters (defaulting to 0)
        Profile prof = new Profile(bcet, wcet, actPattern, resType, resGroup);
        // add to possible resource definition around it.
        gTask.addProfile(prof);

        // if this profile is active, mark it in the task          
        if(activeStr!=null && Boolean.valueOf(activeStr)){
          gTask.setActiveProfile(prof);
        }
      }

      // if task did not get an active profile, just set the first one
      if(gTask.getActiveProfile()==null){
        gTask.setActiveProfile(gTask.getProfileList().getFirst());
      }

      // add task to application model
      app.addTask(gTask);
    }
  }


  @SuppressWarnings("unchecked")
  private void insertSchedParams(Element appE, ApplicationModel app) {
    // unchecked cast
    // get all subElements named Task -the tasks of the application
    for (Element task : (Collection<Element>) appE.getChildren("Task")) {
      String taskIDString = task.getAttributeValue("ID");
      Task gTask = app.getTask(Integer.valueOf(taskIDString));

      Element schedParamXML = task.getChild("SchedulingParameter");
      AbstractSchedulingParameter schedParam = null;
      if(schedParamXML != null){
        try{
          schedParam = (AbstractSchedulingParameter) XMLFactory.fromXML(schedParamXML, defaultClassMap);
        } catch (Exception exception){
          logger.error("error instantiating scheduling parameter. class not found");
        }
        gTask.setSchedulingParameter(schedParam);
      }
    }    
    for (Element taskLink : (Collection<Element>) appE.getChildren("TaskLink")) {
      String taskLinkIDString = taskLink.getAttributeValue("ID");
      TaskLink gTaskLink = app.getTaskLink(Integer.valueOf(taskLinkIDString));

      Element schedParamXML = taskLink.getChild("SchedulingParameter");
      AbstractSchedulingParameter schedParam = null;
      if(schedParamXML != null){
        try{
          schedParam = (AbstractSchedulingParameter) XMLFactory.fromXML(schedParamXML, defaultClassMap);
        } catch (Exception exception){
          logger.error("error instantiating scheduling parameter. class not found");
        }
        gTaskLink.setSchedulingParameter(schedParam);
      }
    }    
  }

  @SuppressWarnings("unchecked")
  private void insertPlatform(SystemModel systemModel) {
    // retrieve the platform part of the model
    Element platformElement = systemElement.getChild("Platform");
    // retrieve all comm resources
    // unchecked conversion to element but should be fine
    Collection<Element> commResources = (Collection<Element>) (platformElement.getChildren("CommResource"));
    for (Element e : commResources) {
      // required fields
      int resID = Integer.parseInt(e.getAttributeValue("resID"));
      // required children
      Element resTypeXML = e.getChild("ResourceType");
      Element resGroupXML = e.getChild("ResourceGroup");
      AbstractResourceType resType;
      AbstractResourceGroup resGroup;
      try{
        resType = (AbstractResourceType) XMLFactory.fromXML(resTypeXML, defaultClassMap);
      } catch (Exception exception){
        resType = new GenericResourceType();
      }
      try{
        resGroup = (AbstractResourceGroup) XMLFactory.fromXML(resGroupXML, defaultClassMap);
      } catch (Exception exception){
        resGroup = new GenericResourceGroup();
      }
      //			String resType = Integer.parseInt(e.getAttributeValue("resType"));
      //			String resGroup = Integer.parseInt(e.getAttributeValue("resGroup"));
      Element schedulerXML = e.getChild("Scheduler");
      AbstractScheduler scheduler;
      try{
        scheduler = (AbstractScheduler) XMLFactory.fromXML(schedulerXML, defaultClassMap);
      } catch (Exception exception){
        // in case of error just assume spp scheduling (for backwards compatibility with older files)
        scheduler = new SPPScheduler();
      }

      // may return null if not given- but this is consistent with constructor call
      String shortName = e.getAttributeValue("shortName");

      // create comm resource
      CommResource cRes = new CommResource(systemModel, shortName, resID, resType, resGroup);
      cRes.setScheduler(scheduler);

      // generate extensions
      insertExtensions(e, cRes);

      // add to system model
      systemModel.addResource(cRes);
    }

    // retrieve all regular resources
    List<Element> regularResources = platformElement.getChildren("Resource");
    for (Element e : regularResources) {

      String resIDString = e.getAttributeValue("resID");

      // values for Resource constructor
      int resID;

      // required children
      Element resTypeXML = e.getChild("ResourceType");
      Element resGroupXML = e.getChild("ResourceGroup");
      AbstractResourceType resType;
      AbstractResourceGroup resGroup;
      try{
        resType = (AbstractResourceType) XMLFactory.fromXML(resTypeXML);
      } catch (Exception exception){
        resType = new GenericResourceType();
      }
      try{
        resGroup = (AbstractResourceGroup) XMLFactory.fromXML(resGroupXML);
      } catch (Exception exception){
        resGroup = new GenericResourceGroup();
      }
      //      String resType = Integer.parseInt(e.getAttributeValue("resType"));
      //      String resGroup = Integer.parseInt(e.getAttributeValue("resGroup"));
      Element schedulerXML = e.getChild("Scheduler");
      AbstractScheduler scheduler;
      try{
        scheduler = (AbstractScheduler) XMLFactory.fromXML(schedulerXML);
      } catch (Exception exception){
        // in case of error just assume spp scheduling (for backwards compatibility with older files)
        scheduler = new SPPScheduler();
      }

      // check if all attributes are present and assign if so
      // TODO: add default values if not present instead of throwing exception
      if (resIDString != null) {
        resID = Integer.parseInt(resIDString);
      } else {
        throw new IllegalStateException("A resource must have a resource ID");
      }

      // may return null if not given- but this is consistent with constructor call
      String shortName = e.getAttributeValue("shortName");

      // create resource
      Resource resource = new Resource(systemModel, shortName, resID, resType, resGroup);
      resource.setScheduler(scheduler);

      // generate extensions
      insertExtensions(e, resource);

      // add to system model
      systemModel.addResource(resource);

      // connect with communication Resources
      for (Element linkElement : (Collection<Element>) e.getChildren("attachedTo")) {
        // get ID of communication resource this resource is
        // attached to
        int comResId = Integer.parseInt(linkElement.getAttributeValue("ID"));
        // retrieve the resource from the system
        CommResource cRes = systemModel.getCommResource(comResId);
        // generate link between the two
        cRes.addLink(resource);
      }
    }

    // create resource model
    systemModel.setResModel(new ResourceGraph(systemModel));
  }

  /**
   * generates all data extensions at rootModel, that are specified in rootXML
   * 
   * @param rootXML
   * @param rootModel
   */
  @SuppressWarnings("unchecked")
  private void insertExtensions(Element rootXML, @SuppressWarnings("rawtypes") ExtendibleModelElement rootModel) {
    // get type of allowed data extensions
    @SuppressWarnings("rawtypes")
    Class<? extends AbstractDataExtension> dataExtensionType = (Class<? extends AbstractDataExtension>) rootModel
    .getDataExtensionType();
    // get the list of extensions saved in this xml
    List<Element> extList = rootXML.getChildren("DataExtensions");
    for (Element dataExt : extList) {
      Collection<Element> extensions = extList.get(0).getChildren();

      // iterate over all saved extensions
      for (Element extension : extensions) {
        XMLSaveable modelExtension;
        // generate model extension object from XML
        try {
          modelExtension = XMLFactory.fromXML(extension);
          boolean isClone = Boolean.valueOf(extension.getAttributeValue("isCloneable"));
          boolean isIgnoreExisting = Boolean.valueOf(extension.getAttributeValue("isIgnoreExisting"));
          boolean isOverwrite = Boolean.valueOf(extension.getAttributeValue("isOverwrite"));
          rootModel.addExtData(dataExtensionType.cast(modelExtension), isClone, isOverwrite, isIgnoreExisting);
        } catch (Exception e) {
          // if generation failed, print message
          logger.error("Cannot insert extensions", e);
        }
      }
    }
  }
}
