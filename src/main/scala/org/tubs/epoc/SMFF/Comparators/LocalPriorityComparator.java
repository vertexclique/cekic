package org.tubs.epoc.SMFF.Comparators;

import java.util.Comparator;

import org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.SchedulingPriority;

/**
 * A comparator implementation which uses the class {@link org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement 
 * SchedulableElement} as a type parameter to the {@link java.util.Comparator Comparator} interface.
 * 
 * The class {@link org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement SchedulableElement} contains 
 * the priority information of an element. Depending upon this information, 
 * {@link #compare(SchedulableElement, SchedulableElement) compare()} method will favor the element with
 * greater priority. 
 * 
 * @see org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement
 *
 */
public class LocalPriorityComparator implements Comparator<SchedulableElement>{
  public LocalPriorityComparator(){
    //empty constructor
  }
  
  /**
   * Compares the WCET/Latency ratio of the passed SchedElements
   * and returns the larger one (violated paths have priority)
   * in case of multiple paths for one SchedElem the largest ratio is considered.
   *
   * @param  schedElem1  First Element to compare
   * @param  schedElem2  Second Element to compare
   * @return 1 if <tt>schedElem1</tt> has a greater priority, 0 if both <tt>schedElem1</tt> and <tt>schedElem2</tt> have 
   * 				the same priority, -1 otherwise.
   * 
   * @throws java.lang.NullPointerException if any of the parameters to be compared is null.
   * 
   * @see org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement
   */
  public int compare(SchedulableElement schedElem1, SchedulableElement schedElem2){
    int prio1 = -1;
    int prio2 = -1;
    
    if(schedElem1.getSchedulingParameter() instanceof SchedulingPriority){
      prio1 = ((SchedulingPriority) schedElem1.getSchedulingParameter()).getPriority();
    }
    if(schedElem2.getSchedulingParameter() instanceof SchedulingPriority){
      prio1 = ((SchedulingPriority) schedElem2.getSchedulingParameter()).getPriority();
    }
    
    if(prio1>prio2){
      return 1;
    } else if(prio1<prio2){
      return -1;
    } else{
      return 0;
    }
  }
}
