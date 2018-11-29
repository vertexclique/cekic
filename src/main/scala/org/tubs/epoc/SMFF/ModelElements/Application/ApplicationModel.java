package org.tubs.epoc.SMFF.ModelElements.Application;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import org.tubs.epoc.SMFF.ModelElements.ExtendibleModelElement;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.Utility.Hashing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class definition of an application. An application model is an {@link org.tubs.epoc.SMFF.ModelElements.ExtendibleModelElement
 * ExtendibleModelElement}. It is attached to a system model and is defined by an id and version. An application has
 * tasks, task links and a resource map. Certain constraints can be injected to an application too, i.e method 
 * {@link #addSysLatConstr(Task, Task, double) addSysLatConstr(...)} add a latency constraint to this application.  
 *
 */
public class ApplicationModel extends ExtendibleModelElement<AbstractAppData> implements Cloneable{
  private static Log logger = LogFactory.getLog(ApplicationModel.class); 
  private SystemModel systemModel;
  
  private int appId;
  private int appV;
  
  // Model elements
  private Hashtable<String,Task> taskList = new Hashtable<String,Task>();
  private Hashtable<String,TaskLink> taskLinkList = new Hashtable<String,TaskLink>();
  
  Hashtable<String,SysLatencyConstraint> constraints = new Hashtable<String, SysLatencyConstraint>();
  ResourceMap appResourceMap = new ResourceMap();

  /**
   * Constructor
   * @param systemModel systemModel to attach to
   * @param appId       application ID
   * @param appV        application version
   */
  public ApplicationModel(SystemModel systemModel, int appId, int appV){
    this.systemModel = systemModel;
    this.appId = appId;
    this.appV = appV;
  }
  
  /**
   * Clone the application model.
   * The clone includes a shallow clone of all parameters plus a clone of all cloneable data extensions.
   */
  @SuppressWarnings("unchecked")
  @Override
  public Object clone(){
    // shallow clone of application model plus clone of data extensions
    ApplicationModel clone = (ApplicationModel) super.clone();
    
    // recreate lists of schedulable elements, constraints and the appResourceMap
    clone.taskList = (Hashtable<String,Task>) this.taskList.clone();
    clone.taskLinkList = (Hashtable<String,TaskLink>) this.taskLinkList.clone();
    clone.appResourceMap = (ResourceMap) this.appResourceMap.clone();
    clone.constraints = (Hashtable<String,SysLatencyConstraint>) this.constraints.clone();    
    
    return clone;
  }
  
  /**
   * Clears all lists of schedulable elements, constraints, etc.
   */
  public void resetApplication(){
    this.taskList.clear();
    this.taskLinkList.clear();
    this.constraints.clear();
    this.appResourceMap.clear();
  }
  
  /**
   * Adds a data object to this application.
   * 
   * @param data the object to add
   * @param clone clone this object to LocalApplicationModel if true
   * 
   * @throws IllegalArgumentException if an object with the same identifier is already attached to this task
   */
  public void addAppData(AbstractAppData data, boolean clone) throws IllegalArgumentException {
    this.addAppData(data, clone, false, false);
  }
  
  /**
   * Adds a data object to this application.
   * 
   * @param data the object to add
   * @param clone clone this object to LocalApplicationModel if true
   * @param overwrite overwrite any existing data objects of this type if any exist
   * @param ignoreExisting ignore this call if an object of this type already exists
   * 
   * @throws IllegalArgumentException if an object with the same identifier is already attached to this task
   */
  public void addAppData(AbstractAppData data, boolean clone, boolean overwrite, boolean ignoreExisting) throws IllegalArgumentException {
    // if no list for this type exists, create it and get the list
    if(!dataObjects.containsKey(data.getClass())){
      dataObjects.put(data.getClass(), new LinkedList<AbstractAppData>());
    }
    LinkedList<AbstractAppData> list = dataObjects.get(data.getClass());

    // if call is ignored if an object of this type exists, return here
    if(ignoreExisting && list.size()>0){
      return;
    }
    // if existing object of this types should be overwritten, clear the list
    if(overwrite){
      list.clear();
    }
    
    // associate with the data object
    data.setParent(this);
    // add to internal storage
    list.add(data);
    
    // if this element should be cloned
    if(clone){
      cloneDataObjects.add(data);
    }
    return;
  }
    
  /**
   * Removes all data objects of the exact class clazz from this application.
   * @param clazz class of objects to remove
   * @return list of removed objects, null if key was not found
   */
  public LinkedList<AbstractAppData> clearAppDataByClass(Class<? extends AbstractAppData> clazz){
    // remove all elements of the class clazz from the cloneDataObjects list
    for(AbstractAppData cloneable : cloneDataObjects){
      if(cloneable.getClass() == clazz){
        cloneDataObjects.remove(cloneable);
      }
    }

    // remove the collection of this type from the data objects list
    return dataObjects.remove(clazz);
  }
  
  /**
   * Removes all data objects of the classes that inherit from clazz from this application.
   * @param clazz class of objects to remove
   * @return list of removed objects, null if key was not found
   */
  public LinkedList<AbstractAppData> clearAppDataByParentClass(Class<? extends AbstractAppData> clazz){
    // remove all elements of the class clazz from the cloneDataObjects list
    for(AbstractAppData cloneable : cloneDataObjects){
      if(cloneable instanceof AbstractAppData){
        ;
      }
      if(cloneable.getClass().isInstance(clazz)){
        cloneDataObjects.remove(cloneable);
      }
    }

    // create new list for the return value
    LinkedList<AbstractAppData> returnList = new LinkedList<AbstractAppData>(); 
    
    // go through all keys
    for(Class<? extends AbstractAppData> dataClass : dataObjects.keySet()){
      // if the key is inheriting from clazz remove it from the data objects
      if(dataClass.isInstance(clazz)){
        // remove the collection and add it to the return list
        returnList.addAll(dataObjects.remove(dataClass));
      }
    }
    
    // return the list of removed objects
    return returnList;
  }

  
  /**
   * Returns the list data object with the given class if associated with this application.
   * 
   * This function may return null if there is no such data object.
   * 
   * @param clazz class of data extension elements
   * @return list of data objects of the specified class clazz
   */
  public LinkedList<AbstractAppData> getAppDataByClass(Class<? extends AbstractAppData> clazz) {
    // simply retrieve from dataObjects
    return dataObjects.get(clazz);
  }
  
  /**
   * Returns the list of data object with the given class if associated with this task.
   * 
   * This function will return a list of size 0 if there are no such data objects.
   * 
   * @param clazz class of data extension elements
   * @return list of data objects of the specified class clazz
   */
  public LinkedList<AbstractAppData> getAppDataByParentClass(Class<? extends AbstractAppData> clazz) {
    // return list
    LinkedList<AbstractAppData> returnList = new LinkedList<AbstractAppData>();
    
    // go through all extensions and check whether they inherit from clazz
    for(Class<? extends AbstractAppData> dataClass : dataObjects.keySet()){
      if(dataClass.isInstance(clazz)){
        // get the collection and add it to the return list
        returnList.addAll(dataObjects.get(dataClass));
      }      
    }
    
    // return the list
    return returnList;
  }

  /**
   * Description of the class.
   * @return a description constructed by application id and application version.
   */
  public String toString(){
    return "AppId: "+this.appId+" AppV: "+this.appV;
  }
  
  /**
   * Getter method for the hash id of the application model.
   * @return the hash id of this application model.
   * @see Hashing
   */
  public String getHash(){
    return Hashing.getHash(this);
  }

  /**
   * Getter method for application id.
   * @return the application id of this application model.
   */
  public int getAppId() {
    return appId;
  }

  /**
   * Getter method for the application version.
   * @return the application version.
   */
  public int getAppV() {
    return appV;
  }
  
  /**
   * Getter method for task mapping.
   * @param task id of which is mapped to a resource.
   * @return the resource which is mapped to the passed task.
   */
  public AbstractResource getTaskMapping(Task task){
    return this.getTaskMapping(task.getIdent());
  }
  
  /**
   * Getter method for task mapping.
   * @param schedElemIdent identifier which is mapped to a resource.
   * @return the resource which is mapped to the passed <tt>schedElemIdent</tt>.
   */
  public AbstractResource getTaskMapping(SchedElemIdentifier schedElemIdent){
    try{
      if(schedElemIdent==null) throw new NullPointerException("Task does not exist");
      if(appResourceMap.containsKey(schedElemIdent)){
        return appResourceMap.get(schedElemIdent);
      }
      //TODO: add error handling
      else return null;
    }
    catch(NullPointerException e){
      logger.error("Error during getting the task mapping", e);
      return null;
    }
  }
  
  //-----------MODEL MODIFICATION----------
  /**
   * Adds task to application.
   * @param task task to be added to this application.
   */
  public void addTask(Task task){
    try{
      if(task==null) throw new NullPointerException("Task does not exist");
      taskList.put(task.getUniqueName(),task);
    }
    catch(NullPointerException e){
      logger.error("Error during adding a task", e);
    }
  }
  
  /**
   * Adds task link to this application.
   * @param taskLink task link to be added to this application
   */
  public void addTaskLink(TaskLink taskLink){
    try{
      if(taskLink==null) throw new NullPointerException("TaskLink does not exist");
      if(taskLink.getSrcTask()==null) throw new NullPointerException("SrcTask does not exist");
      if(taskLink.getTrgTask()==null) throw new NullPointerException("TrgTask does not exist");
      taskLinkList.put(Hashing.getTaskLinkHash(taskLink.getAppId(), taskLink.getSrcTaskId(), taskLink.getTrgTaskId()),taskLink);
      Task srcTask = systemModel.getTask(taskLink.getSrcTask());
      Task trgTask = systemModel.getTask(taskLink.getTrgTask());
      // check whether the tasks were found in the system model
      // in global systems tasks always have to be inserted first
      // when creating local version, we do not really need to add the
      // task link as the task link lists are cloned. However doing this again
      // won't hurt, as the entries are simply overwritten
      if(srcTask != null){
        srcTask.addTaskLink(taskLink);
      }
      if(trgTask != null){
        trgTask.addTaskLink(taskLink);
      }
    }
    catch(NullPointerException e){
      logger.error("Error during adding a task link", e);
    }
  } 
  
  /**
   * Maps a task to a resource.
   *
   * @param  task  task to map
   * @param resource resource to be mapped
   */
  // maps Task to Resource
  public void mapTask(Task task, Resource resource){
    try{
      if(task==null) throw new NullPointerException("task does not exist");
      if(resource==null) throw new NullPointerException("resource does not exist");
    }
    catch(NullPointerException e){
      logger.error("Error during task mapping", e);
    }
    appResourceMap.put(task.getIdent(), resource);
    task.setMappedTo(resource);
    resource.mapSchedElem(task);
  } 
  
  /**
   * Removes the mapping of a task.
   * @param task task to be removed
   * @throws NullPointerException if parameter <tt>task</tt> is null.
   */
  public void unmapTask(Task task){
    // parameter checks
    if(task==null) throw new NullPointerException("task does not exist");
    
    appResourceMap.remove(task.getIdent());
    task.getMappedTo().unmapSchedElem(task);
    task.setMappedTo(null);
  }
  
  /**
   * Maps a taskLink to a resource.
   *
   * @param  taskLink  taskLink to map
   * @param resource cResource to map to
   */
  public void mapTaskLink(TaskLink taskLink, AbstractResource resource){
    try{
      if(taskLink==null) throw new NullPointerException("TaskLink does not exist");
      if(resource==null) throw new NullPointerException("CommResource does not exist");
    }
    catch(Exception e){
      logger.error("Error during task link mapping", e);
    }
    taskLink.setMappedTo(resource);
    resource.mapSchedElem(taskLink);
  } 
  
  /**
   * Unmaps a taskLink.
   *
   * @param  taskLink  taskLink to be removed from the map.
   * @throws NullPointerException if passed parameter <tt>taskLink</tt> is null.
   */
  public void unmapTaskLink(TaskLink taskLink){
    // parameter checks
    if(taskLink==null) throw new NullPointerException("task link does not exist");
    
    taskLink.getMappedTo().unmapSchedElem(taskLink);    
    taskLink.setMappedTo(null);
  }
  
  /**
   * Remaps the task linkk to a resource and refreshes all local views.
   *
   * @param  taskLink  taskLink to remap
   * @param resource resource to remap to
   */
  public void remapTaskLink(TaskLink taskLink, AbstractResource resource){
    try{
      if(taskLink==null) throw new NullPointerException("TaskLink does not exist");
    }
    catch(Exception e){
      logger.error("Error during remapping the task link", e);
    }
    mapTaskLink(taskLink, resource);
//    systemModel.recreateLocalViews();
  }
  
  /**
   * Adds to the system a latency constraint.
   *
   * @param  startTask  First task in chain
   * @param endTask Last task in chain
   * @param latency maximum latency for this path
   */
  public void addSysLatConstr(Task startTask, Task endTask, double latency){
    SysLatencyConstraint sysLatConstr = new SysLatencyConstraint(this, startTask, endTask, latency);
    constraints.put(Hashing.getLatConstrHash(appId, startTask.getElemId(), endTask.getElemId()), sysLatConstr);
  }

  /**
   * Creates task latency constraints for all system latency constraints
   */
  public void createElemLatConstr(){
    Collection<SysLatencyConstraint> c = constraints.values();
    for(SysLatencyConstraint constraint : c){
      constraint.createElemLatConstr();
    }
  }
  
  /**
   * Sets all path constraints to the current path latency.
   * Beforehand systens need to be analyzed.
   * @return <tt>true</tt> if a constraint is changed, <tt>false</tt> otherwise.
   */
  public boolean tightenPathConstraints(){
    boolean oneConstrChanged = false;
    Collection<SysLatencyConstraint> constrCollect = this.constraints.values();
    for(SysLatencyConstraint constraint : constrCollect){
      // see if at least one constraint changed
      if(constraint.getLatencyConstr() != constraint.getPathLatencies()){
        oneConstrChanged = true;
      }
      // set the constraint to the new value
      constraint.setLatencyConstr(Math.min(Double.MAX_VALUE,constraint.getPathLatencies()));
    }
    
    return oneConstrChanged;
  }
  
  //-----------MODEL QUERY----------
  /**
   * Getter method for system model to which this application belongs.
   * @return system model
   */
  public SystemModel getSystemModel(){
    return systemModel;
  }
  
  /**
   * Getter method for task from application via task id.
   * <p>
   * Tasks are populated to an application model via task hash which has a relation with application id and task id.
   * The passed parameter <tt>taskId</tt> is used to find out this hash value. 
   * @param taskId identifier of the task to be returned.
   * @return the task which has the id <tt>taskId</tt>.
   */
  public Task getTask(int taskId){
    String taskHash = Hashing.getTaskHash(appId, taskId);
    return taskList.get(taskHash);
  }
  
  /**
   * Getter method for task from application via task hash id.
   * @param taskHash identifier of the task to be returned.
   * @return the task which has the task hash id <tt>taskHash</tt>.
   */
  public Task getTask(String taskHash){
    return taskList.get(taskHash);
  }
  
  /**
   * Task link lookup by ID.
   * 
   * @param id identifier of the task link to get
   * @return the task link with the given ID or null
   */
  public TaskLink getTaskLink(int id) {
	  for (TaskLink gtl: taskLinkList.values()) {
		  if (gtl.getElemId() == id) return gtl;
	  }
	  return null;
  }
  
  /**
   * Gets task link from application via source and target task identifiers.
   * A task link is identified by the application id, source task id and finally target task id. 
   * @param srcTaskId source target id
   * @param trgTaskId target task id
   * @return the task link from <tt>srcTaskId</tt> to <tt>trgTaskId</tt>
   */
  public TaskLink getTaskLink(int srcTaskId, int trgTaskId){
    return taskLinkList.get(Hashing.getTaskLinkHash(appId, srcTaskId, trgTaskId));
  } 
  
  /**
   * Gets task link from application via a task link identifier.
   * A task link is identified by the application id, source task id and finally target task id. 
   * @param taskLinkIdentifier task link identifier
   * @return the task link identified by the parameter <tt>taskLinkIdentifier</tt>
   */
  public TaskLink getTaskLink(TaskLinkIdentifier taskLinkIdentifier){
    return taskLinkList.get(Hashing.getTaskLinkHash(taskLinkIdentifier.getAppId(),taskLinkIdentifier.getSrcTaskId(), taskLinkIdentifier.getTrgTaskId()));
  }
  
  /**
   * Gets task link from application via a task link identifier hash.
   * @param taskLinkHash hash value for a task link
   * @return the task link which has the hash value <tt>taskLinkHash</tt>
   */
  public TaskLink getTaskLink(String taskLinkHash) {
    return taskLinkList.get(taskLinkHash);
  }
  
  /**
   * Getter method of task link from application via source and target tasks.
   * @param srcTask source task
   * @param trgTask target task
   * @return the task link from <tt>srcTask</tt> to the <tt>trgTask</tt>
   */
  public TaskLink getTaskLink(Task srcTask, Task trgTask){
    try{
      if(srcTask==null) throw new NullPointerException("SrcTask does not exist");
      if(trgTask==null) throw new NullPointerException("TrgTask does not exist");
      return taskLinkList.get(Hashing.getTaskLinkHash(appId, srcTask.getElemId(), trgTask.getElemId()));
    }
    catch(Exception e){
      logger.error("Error during getting the task link", e);
      return null;
    }
  }
  

  /**
   * Uses depth-first search to find the shortest path from task1 to task 2 and returns the length of the path.
   * @param task1 first task
   * @param task2 second task
   * @return the shortest distance from <tt>task1</tt> to <tt>task2</tt>
   */
  public int getDist(Task task1, Task task2){
    return getDist(task1, task2, new HashSet<Task>());
  }
  
  /**
   * Uses depth-first search to find the shortest path from task1 to task 2 and returns the length of the path.
   * Visited nodes are pushed to the parameter <tt>visitedNodes</tt>.
   * @param task1 first task
   * @param task2 second task
   * @param visitedNodes HashSet to log visited nodes (pass new HashSet<Task>())
   * @return length of the shortest path
   * @throws NullPointerException if the parameter <tt>visitedNodes</tt> is null.
   */
  private int getDist(Task task1, Task task2, HashSet<Task> visitedNodes){
    int minDist = Integer.MAX_VALUE;
    
    if(task1 == task2){
      return 0;
    }
    
    visitedNodes.add(task1);
    for(TaskLinkIdentifier taskLinkId : task1.getSrcLinkList().values()){
      TaskLink taskLink = this.getTaskLink(taskLinkId);
      Task task = this.getTask(taskLink.getTrgTaskId());
      if(!visitedNodes.contains(task)){
        int currentDist = getDist(task, task2, visitedNodes);
        minDist = Math.min(minDist, currentDist);
      }
    }
    for(TaskLinkIdentifier taskLinkId : task1.getTrgLinkList().values()){
      TaskLink taskLink = this.getTaskLink(taskLinkId);
      Task task = this.getTask(taskLink.getSrcTaskId());
      if(!visitedNodes.contains(task)){
        int currentDist = getDist(task, task2, visitedNodes);
        minDist = Math.min(minDist, currentDist);
      }
    }
    visitedNodes.remove(task1);
    if(minDist<Integer.MAX_VALUE){
      minDist++;
    }
    return minDist;
  }

  //-----------SYMTA INTERFACING----------
//  public void addToSymta(SymtaClient client) throws ServerDeadException{
//    this.addToSymta(client, false, true);
//  }
//  
//  public void addToSymta(SymtaClient client, boolean overestimate, boolean createPaths) throws ServerDeadException{
//    Collection<Task> c = taskList.values();
//    for(Task Task : c){
//      Task.initTaskResourceMap();
//      Task.addToSymta(client);
//      try{
//        client.sendCommand("mapTask "+Task.getSymtaName()+" "+appResourceMap.get(Task.getIdent()).getSymtaName());
//      }
//      catch(Exception e){
//        logger.error("ApplicationModel-addToSymta", e);
//      }
//    }
//    Collection<TaskLink> d =taskLinkList.values();
//    for(TaskLink TaskLink : d){
//      TaskLink.addToSymta(client, overestimate);
//    }
//    
//    if(createPaths == true){
//      //if all tasks and task links are inserted, insert constraints
//      Collection<SysLatencyConstraint> l = constraints.values();
//      for(SysLatencyConstraint constr : l){
//        constr.addToSymta(client);
//      }
//      // when paths are inserted into Symta store annotations to tasks
//      this.createElemLatConstr();
//    }
//  }
  

  /**
   * Getter method for the tasks.
   * @return the Hashtable of all the tasks belonging to this application.
   */
  public Hashtable<String, Task> getTaskList() {
    return taskList;
  }

  /**
   * Getter method for task links.
   * @return the Hashtable of all the task links belonging to this application.
   */
  public Hashtable<String, TaskLink> getTaskLinkList() {
    return taskLinkList;
  }

  /**
   * Getter method for the constraints of this application.
   * @return the Hashtable of all the constraints belonging to this application.
   */
  public Hashtable<String, SysLatencyConstraint> getConstraints() {
    return constraints;
  }

  /**
   * Getter method for application resource map.
   * @return the application resource map.
   */
  public ResourceMap getAppResourceMap() {
    return appResourceMap;
  }
  
  /**
   * Sets the application resource map of this application.
   * This function is intended to set visible application maps only
   *
   * @param  appResourceMap to be set to this application
   */  
  public void setAppResourceMap(ResourceMap appResourceMap) {
    this.appResourceMap = appResourceMap;
  }
}
