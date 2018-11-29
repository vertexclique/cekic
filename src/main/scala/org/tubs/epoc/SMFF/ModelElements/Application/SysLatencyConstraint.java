package org.tubs.epoc.SMFF.ModelElements.Application;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.Utility.Hashing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for the path latency constraint. It is a model object which enables to query the path and related constraint,
 * as well as the violation.
 *
 */
public class SysLatencyConstraint {
  private static Log logger = LogFactory.getLog(SysLatencyConstraint.class);

  private ApplicationModel app; // application this constraint belongs to
  private Task startTask;
  private Task endTask;
  private double latencyConstr; 
  private double latency;
  private HashMap<String,Path> Paths = new HashMap<String,Path>(); // uninit'ed until added to Symta
  private boolean violated;
  
  /**
   * Constructor.
   * @param app application to which this latency constraint belongs.
   * @param startTask start task
   * @param endTask end task
   * @param latencyConstr path latency constraint as double
   */
  public SysLatencyConstraint(ApplicationModel app, Task startTask, Task endTask, double latencyConstr){
    try{
      if(startTask.getAppId()!=endTask.getAppId()) throw new Exception("Tasks for path from different applications");
    }
    catch(Exception e){
      logger.error("Error in the constructor.", e);
    }
    
    this.app = app;
    this.startTask = startTask;
    this.endTask = endTask;
    this.latencyConstr = latencyConstr;
    this.violated = false;
  }
  
  /**
   * Getter method for the application model of latency constraint.
   * @return the application model that this latency constraint belongs to.
   */
  public ApplicationModel getApp() {
	  return app;
  }
    
  /**
   * Changes the constraint to the specified value (change is automatically reflected in
   * task latency constraints).
   * @param latencyConstr
   */
  public void setLatencyConstr(double latencyConstr){
    this.latencyConstr = latencyConstr;
  }
  
  /**
   * Adds the constraint as task latency constraint to tasks.
   * <p> 
   * This method may only be called after constraint has been added to Symta
   * (paths need to be extracted with Symta).
   *
   */
  public void createElemLatConstr(){
    Collection<Path> paths;
    Iterator<Path> it;
    Path CurrentPath;
    ElementLatencyConstraint CurrentElemConstr;
    LinkedList<SchedulableElement> PathElemList;
    SchedulableElement Predecessor;
    SchedulableElement CurrentElem;
    SchedulableElement Successor;

    paths = Paths.values();
    it = paths.iterator();
    while(it.hasNext()){
      CurrentPath = it.next();
      // copy paths to task latency constraints
      PathElemList = CurrentPath.getElemList();
      CurrentElem = PathElemList.getFirst();
      Successor = CurrentElem;
      for(int i=0; i<PathElemList.size(); i++){
        Predecessor = CurrentElem;
        CurrentElem = PathElemList.get(i);
        if(i<PathElemList.size()-1){
          Successor = PathElemList.get(i+1);
        }
        CurrentElemConstr = CurrentElem.addConstraint(Predecessor, Successor, 0, this, CurrentPath);
        CurrentPath.addElemLatConstr(CurrentElemConstr);
      }
    }
  }
  
  /**
   * Returns a hashmap of all paths.
   * 
   * @return paths
   */
  public HashMap<String,Path> getPaths(){
    return Paths;
  }

  /**
   * Setter method for the path latency.
   *
   * @param latency the path latency value.
   */
  public void setPathLatencies(double latency){
    this.latency = latency;
    if(this.latency>this.latencyConstr){
      violated = true;
    } else{
      violated = false;
    }
  }

  /**
   * Getter method for the path latency from last call to SymtaClient.
   *
   * @return path latency value.
   */
  public double getPathLatencies(){
    return this.latency;
  }
  
// getters and setters
  
  /**
   * Getter method for the system model this path belongs to.
   * @return system model of the application that this path belongs to.
   */
  public SystemModel getSystemModel(){
    return this.app.getSystemModel();
  }
  
  /**
   * Getter method for the start task.
   * @return start task.
   */
  public Task getStartTask() {
    return startTask;
  }

  /**
   * Getter method for the end task.
   * @return the end task
   */
  public Task getEndTask() {
    return endTask;
  }

  /**
   * Getter method for the path latency constraint.
   * @return the path latency constraint
   */
  public double getLatencyConstr() {
    return latencyConstr;
  }
  
  /**
   * Check whether the path latency constraint is violated. 
   * <p>
   * A violation occurs when the path latency is greater than the constraint (latency &gt latency constraint).
   * @return <tt>true</tt> if the constraint is violated, <tt>false</tt> otherwise.
   */
  public boolean getViolated(){
    return violated;
  }
  
  /**
   * Get the hash id for the constraint.
   * @return the hash id
   * @see Hashing 
   */
  public String getHash(){
    return Hashing.getLatConstrHash(startTask.getAppId(), startTask.getElemId(), endTask.getElemId());
  }
  
  /**
   * Get the unique name for the constraint.
   * <p>
   * Unique name is constructed by concatenating the unique names of start and end tasks. 
   * @return the unique name
   */
  public String getUniqueName(){
    return startTask.getUniqueName()+"-"+endTask.getUniqueName();
  }
}
