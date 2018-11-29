package org.tubs.epoc.SMFF.ModelElements.Application;

import java.util.LinkedList;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class definition for a path which consists of schedulable elements and latency constraints for the elements. 
 * A path has constraints such as actual worst-case latency of the path in addition to the relation with a system latency constraint.
 * It has some helper functions such as the worst case execution time of the path which in turn is the sum of worst
 * case execution time of each element in the path.
 *
 */
public class Path {
  private static Log logger = LogFactory.getLog(Path.class);

  private String symtaName;
  private SysLatencyConstraint sysLatConstr;
  private double pathLatency; // actual worst-case latency of this path
  private int sumWcets = 0; // sum of all sched elems wcets along this path
  private LinkedList<SchedulableElement> schedElems = new LinkedList<SchedulableElement>();
  private LinkedList<ElementLatencyConstraint> elemLatencyConstraints = new LinkedList<ElementLatencyConstraint>();
  
  //visibility information for drawing path charts
  private boolean visibleLat    = true;
  private boolean visibleStat   = true;
  private boolean visibleConstr = true;
  private int seriesIndexLat    = -1;
  private int seriesIndexStat   = -1;
  private int seriesIndexConstr = -1;
  
  /**
   * Constructor.
   * @param symtaName name of the symta.
   * @param sysLatConstr system latency constraint.
   * @param pathLatency path latency.
   */
  public Path(String symtaName, SysLatencyConstraint sysLatConstr, int pathLatency){
    this.symtaName = symtaName;
    this.sysLatConstr = sysLatConstr;
    this.pathLatency = pathLatency;
  }
  
  /**
   * Compares whether two paths are identical (have the same system latency constraint and schedulable elements.
   * @param o path to be compared for equality check.
   */
  @Override
  public boolean equals(Object o){
  	if(o == null) {
  		return false;
  	}
    if(o instanceof Path){
      Path compareTo = (Path) o;
      
      //comparison
      if( compareTo.sysLatConstr == this.sysLatConstr &&
          compareTo.schedElems.equals(this.schedElems)){
        return true;
      } else{
        return false;
      }
    } else {
    return false;
    }
  }
  
  /**
   * Adds a schedulable element to the list.
   * @param elem schedulable element to be added to the list
   */
  public void addSchedElem(SchedulableElement elem){
    schedElems.add(elem);
    sumWcets += elem.getWCET();
  }
  
  /**
   * Adds an element latency constraint to the list.
   * @param elc element latency constraint to be added to the list.
   */
  public void addElemLatConstr(ElementLatencyConstraint elc){
    elemLatencyConstraints.add(elc);
  }
  
  /**
   * Getter method for the latency along this path (actual worst-case latency of this path).
   * @return pathLatency the latency along this path.
   */
  public double getLatency() {
    return pathLatency;
  }
  
  /**
   * returns the sum of all schedulable elements' worst case execution time along this path.
   * @return the sum of worst case execution time of the schedulable elements along this path.
   */
  public int getSumWcets(){
    return this.sumWcets;
  }
  
  /**
   * Getter method for the constraint on this path.
   * @return path constraint (latency).
   */
  public double getConstraint(){
    return sysLatConstr.getLatencyConstr();
  }

  /**
   * Setter method for the path latency in the path and the last element
   * of the path (where it is naturally available).
   * Note: element latency constraints need to be created before calling.
   *
   * @param  pathLatency  path latency to be set to the path.
   */
  public void setLatency(double pathLatency) {    
    // add new latency
    this.pathLatency = pathLatency;
    
    try{
      if(elemLatencyConstraints.size()==0) throw new Exception("Element latency constraint list empty");
      elemLatencyConstraints.getLast().setPathLatency(pathLatency);
      elemLatencyConstraints.getLast().propagateLatencyAll(); //propagate latency through entire path
    }
    catch(Exception e){
      logger.error("Error during setting the latency", e);
    }
  }

  /**
   * Getter method for the Symta name of this path.
   * @return symta name
   */
  public String getSymtaName() {
    return symtaName;
  }
  
  /**
   * Changes the symta name and returns the old name.
   * @param newName new symta name to be set to the path.
   * @return the old symta name. 
   */
  public String setSymtaName(String newName){
    String temp = this.symtaName;
    this.symtaName = newName;
    return temp;
  }
  
  /**
   * Getter method for the system model to which this path belongs to.
   * @return the system model to which this path belongs to.
   */
  public SystemModel getSystemModel(){
    return this.sysLatConstr.getSystemModel();
  }
  
  /**
   * Returns all schedulable elements along this path as a linked list.
   * @return all schedulable elements along this path as a linked list.
   */
  public LinkedList<SchedulableElement> getElemList(){
    return schedElems;
  }
  
  /**
   * Returns all element latency constraints associated with this path.
   * @return the element latency constraint as a list
   */
  public LinkedList<ElementLatencyConstraint> getElemConstrList(){
    return elemLatencyConstraints;
  }

  /**
   * Getter method for visibility information of the latency for drawing path charts.
   * @return <tt>true</tt> if latency is visible for the path charts, <tt>false</tt> otherwise.   
   */
  public boolean isVisibleLat() {
    return visibleLat;
  }

  /**
   * Setter method for visibility information of the latency for drawing path charts.
   * @param visibleLat visibility of the latency for the charts to be drawn. 
   */
  public void setVisibleLat(boolean visibleLat) {
    this.visibleLat = visibleLat;
  }

  /**
   * Getter method for visibility information of the path statistics for drawing path charts.
   * @return <tt>true</tt> if path statistics is visible for the path charts, <tt>false</tt> otherwise.   
   */
  public boolean isVisibleStat() {
    return visibleStat;
  }

  /**
   * Setter method for visibility information of the path statistics for drawing path charts.
   * @param visibleStat visibility of the path statistics for the charts to be drawn. 
   */
  public void setVisibleStat(boolean visibleStat) {
    this.visibleStat = visibleStat;
  }

  /**
   * Getter method for visibility information of the path constraint for drawing path charts.
   * @return <tt>true</tt> if path constraint is visible for the path charts, <tt>false</tt> otherwise.   
   */
  public boolean isVisibleConstr() {
    return visibleConstr;
  }

  /**
   * Setter method for visibility information of the path constraint for drawing path charts.
   * @param visibleConstr visibility of the path constraint for the charts to be drawn. 
   */
  public void setVisibleConstr(boolean visibleConstr) {
    this.visibleConstr = visibleConstr;
  }

  /**
   * Getter method for series of index latency to be used during drawing charts.
   * @return the series of index latency.
   */
  public int getSeriesIndexLat() {
    return seriesIndexLat;
  }

  /**
   * Setter method for series of index latency to be used during drawing charts.
   * @param seriesIndexLat index latency series value to be set.
   */
  public void setSeriesIndexLat(int seriesIndexLat) {
    this.seriesIndexLat = seriesIndexLat;
  }

  /**
   * Getter method for series of index statistics to be used during drawing charts.
   * @return the series of index statistics for the charts.
   */
  public int getSeriesIndexStat() {
    return seriesIndexStat;
  }

  /**
   * Setter method for series of index statistics to be used during drawing charts.
   * @param seriesIndexStat index statistics series value to be set.
   */
  public void setSeriesIndexStat(int seriesIndexStat) {
    this.seriesIndexStat = seriesIndexStat;
  }

  /**
   * Getter method for series of index constraints to be used during drawing charts.
   * @return the series of index constraints for the charts.
   */
  public int getSeriesIndexConstr() {
    return seriesIndexConstr;
  }

  /**
   * Setter method for series of index constraints to be used during drawing charts.
   * @param seriesIndexConstr index constraint series value to be set.
   */
  public void setSeriesIndexConstr(int seriesIndexConstr) {
    this.seriesIndexConstr = seriesIndexConstr;
  }
}
