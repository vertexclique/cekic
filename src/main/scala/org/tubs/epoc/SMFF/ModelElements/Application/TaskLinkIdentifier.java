package org.tubs.epoc.SMFF.ModelElements.Application;

/**
 * Class description for a task link identifier which in turn is an implementation of a {@link SchedElemIdentifier
 * SchedElemIdentifier}. An identifier for a task link instance is identified by application id, application version, 
 * id of the link, source task id and finally the target task id.
 * @see SchedElemIdentifier
 *
 */
public class TaskLinkIdentifier implements SchedElemIdentifier{
  private int appId;
  private int appV;
  private int linkId;
  private int srcTaskId;
  private int trgTaskId;
  
  /**
   * Constructor.
   * @param appId id of the application to which this task link belongs to.
   * @param appV version of the application that this task link belongs to.
   * @param linkId id of this task link.
   * @param srcTaskId id of the source task of this link
   * @param trgTaskId id of the target task of this link
   */
  public TaskLinkIdentifier(int appId, int appV, int linkId, int srcTaskId, int trgTaskId) {
    this.appId = appId;
    this.appV = appV;
    this.linkId = linkId;
    this.srcTaskId = srcTaskId;
    this.trgTaskId = trgTaskId;
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
    if( compareTo instanceof TaskLinkIdentifier   &&
        this.appId     == ((TaskLinkIdentifier)compareTo).appId     &&
        this.appV      == ((TaskLinkIdentifier)compareTo).appV      &&
        this.linkId    == ((TaskLinkIdentifier)compareTo).linkId    &&
        this.srcTaskId == ((TaskLinkIdentifier)compareTo).srcTaskId &&
        this.trgTaskId == ((TaskLinkIdentifier)compareTo).trgTaskId ){
      return true;      
    } else{
      return false;
    }
  }
  
  /**
   * Hash code generator for this task link identifier.
   * @return the hash code.
   * @see Object#hashCode()
   * @see SchedElemIdentifier#hashCode()
   */
  @Override
  public int hashCode(){
    return appId*10000+appV*100+linkId;
  }

  /**
   * Getter method for the application id of the task link identifier.
   * @return the application id.
   */
  public int getAppId() {
    return appId;
  }

  /**
   * Getter method for the application version of the task link identifier.
   * @return the application version.
   */
  public int getAppV() {
    return appV;
  }

  /**
   * Getter method for the element id of the task link identifier.
   * @return the element id.
   */
  public int getElemId() {
    return linkId;
  }

  /**
   * Getter method for the source id of the task link.
   * @return the source task id of this link.
   */
  public int getSrcTaskId() {
    return srcTaskId;
  }

  /**
   * Getter method for the target id of the task link.
   * @return the target task id of this link.
   */
  public int getTrgTaskId() {
    return trgTaskId;
  }
  

}
