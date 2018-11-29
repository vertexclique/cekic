package org.tubs.epoc.SMFF.ModelElements.Application;

import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractSchedulingParameter;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.SchedulingPriority;

/**
 *  Class description for Task.
 *  <p>
 *  A task is a {@link SchedulableElement SchedulableElement}, which adds additional properties to it, such as task id, 
 *  resource specification, position of profiles to use and source target tasks.
 *  @see SchedulableElement
 *
 */
public class Task extends SchedulableElement implements Cloneable{
  private static Log logger = LogFactory.getLog(Task.class);

  /**
   * The task identifier of this task.
   */
  protected TaskIdentifier taskId;
  
  /**
   * Source task links.
   */
  protected Hashtable<String,TaskLinkIdentifier> srcLinkList = new Hashtable<String,TaskLinkIdentifier>();
  /**
   * Target task links.
   */
  protected Hashtable<String,TaskLinkIdentifier> trgLinkList = new Hashtable<String,TaskLinkIdentifier>();


  /**
   * Creates a new task instance.
   *
   * @param name  name of this task
   * @param app application that this task belongs to
   * @param taskId  Task ID
   */
  public Task(String name, ApplicationModel app, int taskId){
    this.shortName  = name;
    this.application  = app;
    this.taskId       = new TaskIdentifier(app.getAppId(), app.getAppV(), taskId);
    this.schedParam   = null;
    this.activeProfile   = null;

    this.outputBehavior = new PJdTimingBehavior(0,0,0);
    
  }

  /**
   * Creates a new task instance.
   * <p>
   * Name of the task is created from the task and application ids.
   *
   * @param app application that this task belongs to
   * @param taskId  Task ID
   */
  public Task(ApplicationModel app, int taskId){
    this("App "+String.valueOf(app.getAppId())+" - Task "+String.valueOf(taskId), app, taskId);
  }  
  
  /**
   * Creates a clone of the task. 
   * <p>
   * This clone  which consists of:
   * <ul>
   * <li> shallow clone of the task
   * <li> deep clone of cloneable data extensions
   * <li> deep clone of cloneable data extensions
   * <li> clone of all lists and hashtables (-> list is duplicated, referenced objects are linked)
   * <li> empty list of change listeners
   * </ul>
   * @return the clone of the task.
   */
  @Override
  public Object clone(){
    Task clone = (Task) super.clone();
    clone.profileList = new LinkedList<Profile>();
    clone.profileList.addAll(profileList);
    clone.srcLinkList = new Hashtable<String, TaskLinkIdentifier>();
    clone.srcLinkList.putAll(srcLinkList);
    clone.trgLinkList = new Hashtable<String, TaskLinkIdentifier>();
    clone.trgLinkList.putAll(trgLinkList);    
    return clone;
  }

  //---------------Model maintenance--------------------- 
  /**
   * Adds a task link to the appropriate list in the task.
   *
   * @param  taskLink  task link to add
   */
  public void addTaskLink(TaskLink taskLink){
    try{
      if(taskLink == null) throw new NullPointerException("TaskLink does not exist");
      if(taskLink.getSrcTask().equals(this.taskId)){
        //SystemModel
        srcLinkList.put(taskLink.getUniqueName(),(TaskLinkIdentifier) taskLink.getIdent());
      }
      else if(taskLink.getTrgTask().equals(this.taskId)){
        //SystemModel
        trgLinkList.put(taskLink.getUniqueName(),(TaskLinkIdentifier) taskLink.getIdent());
      }
      else{
        throw new Exception("Tried to insert TaskLink to task it is not connected to");
      }
    }
    catch(Exception e){
      logger.error("Exception during adding a tasklink", e);
    }
  }
  
  /**
   * Getter method for the all source links.
   * @return the hashtable of source links
   */
  public Hashtable<String,TaskLinkIdentifier> getSrcLinkList(){
    return this.srcLinkList;
  }
  
  /**
   * Getter method for the all target links.
   * @return hashtable of target links
   */
  public Hashtable<String,TaskLinkIdentifier> getTrgLinkList(){
    return this.trgLinkList;
  }

  //---------------utility functions--------------------- 
  /**
   * Gets the identifier of the task.
   * 
   * @return the identifier.
   */
  @Override
  public final SchedElemIdentifier getIdent(){
    return this.taskId;
  }
  
  
  /**
   * Unique name for the task.
   * @return the unique name of the task.
   */
  @Override
  public String getUniqueName() {
    return "AppId:"+getIdent().getAppId()+"-TaskId:"+getIdent().getElemId();
  }
}
