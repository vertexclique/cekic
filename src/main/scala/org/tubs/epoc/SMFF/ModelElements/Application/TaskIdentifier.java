package org.tubs.epoc.SMFF.ModelElements.Application;

/**
 * Class description for a task identifier which in turn is an implementation of a {@link SchedElemIdentifier
 * SchedElemIdentifier}. An identifier for a task instance is created from application id, application version and task 
 * id properties of this class.
 * @see SchedElemIdentifier
 *
 */
public class TaskIdentifier implements SchedElemIdentifier{
  private int appId;
  private int appV;
  private int taskId;
  
  /**
   * Constructor.
   * @param appId id of the application to which this task belongs to.
   * @param appV version of the application that this task belongs to.
   * @param taskId id of this task.
   */
  public TaskIdentifier(int appId, int appV, int taskId){
    this.appId  = appId;
    this.appV   = appV;
    this.taskId = taskId;
  }

	/**
	 * Compares whether the passed object is same with this instance.
	 * @param compareTo object to be compared for equality
	 * @return <tt>true</tt> if passed object <tt>compareTo</tt> is same with this instance, <tt>false</tt> otherwise.
	 * @see Object#equals(Object)
	 * @see SchedElemIdentifier#equals(Object)
	 */
  @Override
  public boolean equals(Object compareTo){
  	if(compareTo == null) {
  		return false;
  	}
    if( compareTo instanceof TaskIdentifier &&
        this.appId  == ((TaskIdentifier) compareTo).appId  &&
        this.appV   == ((TaskIdentifier) compareTo).appV   &&
        this.taskId == ((TaskIdentifier) compareTo).taskId ){
      return true;      
    } else{
      return false;
    }
  }

  /**
   * Hash code generator for this task identifier.
   * @return the hash code.
   * @see Object#hashCode()
   * @see SchedElemIdentifier#hashCode()
   */
  @Override
  public int hashCode(){
    return appId*10000+appV*100+taskId;
  }

  /**
   * Getter method for the application id of the task identifier.
   * @return the application id.
   */
  public int getAppId() {
    return appId;
  }

  /**
   * Getter method for the application version of the task identifier.
   * @return the application version.
   */
  public int getAppV() {
    return appV;
  }

  /**
   * Getter method for the element id of the task identifier.
   * @return the element id.
   */
  public int getElemId() {
    return taskId;
  } 
}
