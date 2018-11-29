package org.tubs.epoc.SMFF.ModelElements.Application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

/**
 * Class definition of a latency constraint for a schedulable element. A schedulable element is a node in a path which
 * has its own latency constraint along with the latency constraint assigned to the path.
 *
 */
public class ElementLatencyConstraint {
  private static Log logger = LogFactory.getLog(ElementLatencyConstraint.class);

  private SchedulableElement thisElem;
  private Path path;  // name of Symta path that this constraint belongs to
  
  private SchedulableElement predecessorElem;
  private SchedulableElement successorElem;
  private double pathLatency[] = new double[HistoryConfig.GLO_ANA_TEMP_RES+1];  //
  private SysLatencyConstraint sysLatConstr;
  
  /**
   * Constructor.
   * @param owner owner of the constraint.
   * @param predecessor predecessor of the constraint owner.
   * @param successor successor of  the owner constraint.
   * @param pathLatency path latency as integer.
   * @param sysLatConstr system latency constraint.
   * @param path path of the constraint.
   */
  public ElementLatencyConstraint(SchedulableElement owner, SchedulableElement predecessor, SchedulableElement successor, int pathLatency, SysLatencyConstraint sysLatConstr, Path path){
    thisElem = owner;
    predecessorElem = predecessor;
    successorElem = successor;
    this.pathLatency[0] = pathLatency;
    for(int i=1;i<HistoryConfig.GLO_ANA_TEMP_RES;i++){
      this.pathLatency[i]=Integer.MAX_VALUE; 
    }
    this.sysLatConstr = sysLatConstr;
    this.path = path;
  }


  /**
   * Getter method for the current slack (as percentage of the constraint).
   *
   * @return  the slack percentage as double (<0 = violated).
   */
  public double getSlackPercentage(){
    return getSlackPercentage(0);
  }

  /**
   * Getter method for the slack (as percentage of the constraint) for the given point in history.
   *
   * @param history point in history (0=current).
   * @return  slack percentage (<0 = violated) as double value.
   */
  public double getSlackPercentage(int history){
    return ((double)(thisElem.getResponseTime())-(double)pathLatency[history])/(double)this.getLatencyConstr();
  }

  /**
   * Getter method for the current path latency.
   *
   * @return  path latency
   */
  public double getPathLatency(){
    return this.getPathLatency(0);
  }

  /**
   * Getter method for the path latency for the given point in history.
   *
   * @param history point in history (0=current)
   * @return   path latency
   */
  public double getPathLatency(int history){
    try{
      if(history>HistoryConfig.GLO_ANA_TEMP_RES){
        throw new Exception("history larger than temporal resolution");
      }
    } catch(Exception e){
      logger.error("Error during getting the path llatency", e);
    }
    return pathLatency[history];
  }
  
  /**
   * Getter method for the sum of all schedulable elements' worst case execution time along this path.
   * @return the sum of all schedulable elements' worst case execution time along this path.
   */
  public int getSumWcets(){
    return this.path.getSumWcets();
  }
  
  /**
   * Getter method for the latency constraint.
   * @return the latency constraint.
   */
  public double getLatencyConstr(){
    return sysLatConstr.getLatencyConstr();
  }

  /**
   * Setter method for path latency.
   * @param pathLatency path latency for this latency constraint.
   */
  public void setPathLatency(double pathLatency) {
    for(int i=HistoryConfig.GLO_ANA_TEMP_RES;i>0;i--){
      this.pathLatency[i] = this.pathLatency[i-1];
    }
    this.pathLatency[0] = pathLatency;
  }
  
  
  /**
   * Calculates the violation for this constraint. 
   * <p> A violation is the difference of path latency and latency constraint.  
   * @return the violation value for this constraint ( <tt>pathLatency - latencyConstr</tt>).
   */
  public double getViolation(){
    return this.getPathLatency()-this.getLatencyConstr();
  }

  /**
   * Propagates the latency value of this element to its predecessor.
   *
   */
  public void propagateLatency(){
    predecessorElem.getLatencyConstraint(path.getSymtaName()).setPathLatency(this.pathLatency[0]);
  }

  /**
   * Propagates the latency value of this element to all its predecessor (the entire path).
   *
   */
  public void propagateLatencyAll(){
    predecessorElem.getLatencyConstraint(path.getSymtaName()).setPathLatency(this.pathLatency[0]);
    if(predecessorElem!=thisElem){
      predecessorElem.getLatencyConstraint(path.getSymtaName()).propagateLatencyAll();
    }
  }

  /**
   * getter method for the owner of this constraint.
   * @return the owner of this constraint.
   */
  public SchedulableElement getThisElem() {
    return thisElem;
  }
  
  /**
   * Getter method for the path of this constraint.
   * @return the path of the constraint.
   */
  public Path getPath() {
    return path;
  }
  
  /**
   * Getter method for the name of the path.
   * @return the name of this path.
   */
  public String getPathName(){
    return path.getSymtaName();
  }
  
  /**
   * Getter method the index of the constraint owner in the path.
   * @return the index of the constraint owner in the path.
   */
  public int getPathPosition(){
    return path.getElemConstrList().indexOf(this);
  }

  /**
   * Getter method for the predecessor of the constraint owner.
   * @return the the predecessor of the constraint owner.
   */
  public SchedulableElement getPredecessorElem() {
    return predecessorElem;
  }

  /**
   * Finds the element id of the predecessor of the constraint owner.
   * @return the element id of the predecessor of the constraint owner.
   */
  public SchedElemIdentifier getPredecessorElemId() {
    return predecessorElem.getIdent();
  }

  /**
   * Getter method for the successor of the constraint owner.
   * @return the the successor of the constraint owner.
   */
  public SchedulableElement getSuccessorElem() {
      return successorElem;
  }
  
  /**
   * Finds the element id of the successor of the constraint owner.
   * @return the element id of the successor of the constraint owner.
   */
  public SchedElemIdentifier getSuccessorElemId() {
    return successorElem.getIdent();
  }

  /**
   * Getter method for the system latency constraint.
   * @return the system latency constraint.
   */
  public SysLatencyConstraint getSysLatConstr() {
    return sysLatConstr;
  }
  
  
}
