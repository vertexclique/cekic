package org.tubs.epoc.SMFF.ModelElements.Application;

import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractSchedulingParameter;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.SchedulingPriority;

/**
 * Class description for the TaskLink.
 * <p>
 * A task link is a link (association) between a source and target task which has a priority. 
 * It has certain properties like message size and number of
 * messages associated with it. This class is a model object which in addition to creating a task link provides certain 
 * other functionalities such as getters and setters for these properties. 
 *
 */
public class TaskLink extends SchedulableElement implements Cloneable{
	/**
	 * The identifier of the task link.
	 */
  protected TaskLinkIdentifier taskLinkId;
  /**
   * The task at the source of this task link.
   */
  protected TaskIdentifier srcTask;
  /**
   * The task this link points to.
   */
  protected TaskIdentifier trgTask;
  
  /**
   * Creates a new task link.
   *
   * @param  name name to assign to this task link
   * @param app application that this task link belongs to
   * @param linkId  link ID of the task
   * @param srcTaskId task ID for the source task of the task link
   * @param trgTaskId task ID for the target task of the task link
   */
  public TaskLink(String name, ApplicationModel app, int linkId, int srcTaskId, int trgTaskId){
    this.shortName = new String(name);
    this.taskLinkId = new TaskLinkIdentifier(app.getAppId(), app.getAppV(), linkId, srcTaskId, trgTaskId);
    this.mappedTo = null;
    this.application = app;
    this.srcTask = new TaskIdentifier(app.getAppId(), app.getAppV(), srcTaskId);
    this.trgTask = new TaskIdentifier(app.getAppId(), app.getAppV(), trgTaskId);
    this.schedParam = null;
  }
  
  /**
   * Creates a new task link.
   * <p>
   * Overloaded constructor without passing a string as name. The name is constructed from the id of source and target
   * tasks.
   * @param argLinkId  link ID of the task
   * @param app application that this task link belongs to
   * @param argSrcTaskId task ID for the source task of the task link
   * @param argTrgTaskId task ID for the target task of the task link
   */
  public TaskLink(int argLinkId, ApplicationModel app, int argSrcTaskId, int argTrgTaskId){
    this(String.valueOf(argSrcTaskId)+" - "+String.valueOf(argTrgTaskId), app, argLinkId, argSrcTaskId, argTrgTaskId);
  }
  
  /**
   * Creates a copy of this task link.
   * <p>
   * A copy of a task link consists of the following:
   * <ul>
   * <li> a shallow clone of the tasklink 
   * <li> deep clone of cloneable data extensions
   * <li> deep clone of cloneable data extensions
   * <li> clone of all lists and hashtables (list is duplicated, referenced objects are linked)
   * <li> empty list of change listeners
   * </ul>
   * @return the copy of this task link instance
   */
  @Override
  public Object clone(){
    TaskLink clone = (TaskLink) super.clone();
    return clone;
  }
  
  //---------------Model maintenance--------------------- 
    
  //---------------Generic functions---------------------

  /**
   * Getter method for the application that this task link belongs to.
   * @return the application that this task link belongs to.
   */
  public ApplicationModel getApplication() {
    return application;
  }
  
  /**
   * Getter method for the identifier of this task link.
   * 
   * @return the identifier
   */
  @Override
  public final SchedElemIdentifier getIdent(){
    return this.taskLinkId;
  }

  /**
   * Getter method for the identifier of the source task.
   * @return the ID of the source task of this task link
   */
  public int getSrcTaskId() {
    return this.taskLinkId.getSrcTaskId();
  }

  /**
   * Getter method for the identifier of the target task.
   * @return the  ID of the target task of this task link
   */
  public int getTrgTaskId() {
    return this.taskLinkId.getTrgTaskId();
  }

  /**
   * Getter method for the source task of this task link.
   * @return the source task of this task link
   */
  public TaskIdentifier getSrcTask() {
    return srcTask;
  }

  /**
   * Getter method for the target task of this task link.
   * @return the target task of this task link
   */
  public TaskIdentifier getTrgTask() {
    return trgTask;
  }

  /**
   * Returns the unique name for this task link.
   * <p>
   * A unique for the link is constructed from application id, source task id and target task id.
   * @return the unique name of this task link
   */
  @Override
  public String getUniqueName() {
    return "AppId:"+getAppId()+
    "-SrcTaskId:"+getSrcTaskId()+
    "-TrgTaskId:"+getTrgTaskId();
  }
}
