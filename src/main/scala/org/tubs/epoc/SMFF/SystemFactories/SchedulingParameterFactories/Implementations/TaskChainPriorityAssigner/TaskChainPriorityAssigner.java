package org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.TaskChainPriorityAssigner;

import java.util.LinkedList;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractSchedulingParameter;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.SchedulingPriority;
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.AbstractPriorityAssigner;

/**
 * This priority assigner assigns random priorities to all task/task links on their resources if
 * they did not receive a priority yet. If gaps in priority assignments exist (due to existing
 * priority assignments of other applications) the gaps are filled
 * This priority assigner only works for task chain and only if the task/link ids are increasing from source to sink
 * @author moritzn
 *
 */
public class TaskChainPriorityAssigner extends AbstractPriorityAssigner{
	/**
	 * ID of this factory.
	 */
  public static final String IDENTIFIER = "SystemData StdPriorityAssigner";
  
  private TaskChainPriorityAssignerData priorityAssignerData;
  private Random rnd;

  /**
   * Creates an instance of this class.
   * @param systemModel system model
   * @param priorityAssignerData priority assigner data
   */
  public TaskChainPriorityAssigner(SystemModel systemModel, TaskChainPriorityAssignerData priorityAssignerData){
    super(systemModel);
    this.priorityAssignerData = priorityAssignerData;
    this.recreateRndGens();
  }

  /**
   * Randomly assigns priorities to task/task links without a valid priority.
   */
  public void assignPriorities(){
    // first go through all resources to assign priorities for tasks
    for(AbstractResource res : systemModel.getAllRes()){
      SortedSet<Integer> usedPrios = new TreeSet<Integer>();
      SortedSet<Integer> freePrios = new TreeSet<Integer>();
      int numPriolessElems = 0;
      SortedMap<Integer, SortedMap<Integer,SchedulableElement>> appPriolessSchedElemMap = new TreeMap<Integer, SortedMap<Integer,SchedulableElement>>();
      int numElems = 0;

      // init lists
      // go through all applications in the system
      for(ApplicationModel app : systemModel.getApplications()){
        SortedMap<Integer,SchedulableElement> priolessElemsInApp = new TreeMap<Integer,SchedulableElement>();
        // go through all task in the system
        if(res instanceof Resource){
          for(Task task : app.getTaskList().values()){
            // if the task is mapped to this resource
            if(task.getMappedTo() == res){
              AbstractSchedulingParameter schedParam = task.getSchedulingParameter();
              if(schedParam != null && schedParam instanceof SchedulingPriority){
                usedPrios.add(((SchedulingPriority)schedParam).getPriority());
              } else {
                priolessElemsInApp.put(task.getElemId(),task);
                numPriolessElems++;
              }
              numElems++;
            }
          }
        } else if(res instanceof CommResource){
          // go through all task link in the system
          for(TaskLink taskLink : app.getTaskLinkList().values()){
            // if the task link is mapped to this resource
            if(taskLink.getMappedTo() == res){
              AbstractSchedulingParameter schedParam = taskLink.getSchedulingParameter();
              if(schedParam != null && schedParam instanceof SchedulingPriority){
                usedPrios.add(((SchedulingPriority)schedParam).getPriority());
              } else {
                priolessElemsInApp.put(taskLink.getElemId(),taskLink);
                numPriolessElems++;
              }
              numElems++;
            }
          }
        }
        if(priolessElemsInApp.size()>0){
          appPriolessSchedElemMap.put(app.getAppId(), priolessElemsInApp);
        }        
      }

      // assemble list of free priorities
      int j=1;
      while(freePrios.size()<numPriolessElems){
        if(!usedPrios.contains(j)){
          freePrios.add(j);
        }
        j++;
      }

      // assign random priority from free priority list to prioless tasks
      while(appPriolessSchedElemMap.size()>0){
        // create list of apps (in order to be able to pick a random element)
        LinkedList<Integer> appList = new LinkedList<Integer>(appPriolessSchedElemMap.keySet());
        // create random index
        int index = appPriolessSchedElemMap.size()==1 ? 0 : rnd.nextInt(appPriolessSchedElemMap.size());
        // get the app id of the randomly picked app
        int assignToApp = appList.get(index);
        // get the highest available prio
        Integer prio = freePrios.first();
        
        // get the map of prioless schedulable elements of this app on this resource
        SortedMap<Integer, SchedulableElement> thisAppPriolessSchedElems = appPriolessSchedElemMap.get(assignToApp);
        // get the id of the first sched elem in the chain on this resource
        Integer lastSchedElem = thisAppPriolessSchedElems.firstKey();
        // get the according sched elem
        SchedulableElement schedElem = thisAppPriolessSchedElems.get(lastSchedElem);
        // set the priority
        schedElem.setSchedulingParameter(new SchedulingPriority(prio));
        
        // remove the sched elem from the list of this app's prioless sched elems
        thisAppPriolessSchedElems.remove(lastSchedElem);
        // if the list is empty remove it from the prioless apps
        if(thisAppPriolessSchedElems.isEmpty()){
          appPriolessSchedElemMap.remove(assignToApp);
        }
        
        // remove the prio from the list of free prios
        freePrios.remove(prio);
      }
    }
  }

  /**
   * Getter method to fetch the priority assigner data attached to this factory.
   * 
   * @return the priority assigner data attached to this factory
   */
  public TaskChainPriorityAssignerData getPriorityAssignerData() {
    return priorityAssignerData;
  }

  /**
   * Recreate random generator with this seed.
   */
  public void recreateRndGens() {
    rnd = new Random(priorityAssignerData.getSeed());
  }
}
