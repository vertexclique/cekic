package org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.StdPriorityAssigner;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
 * @author moritzn
 *
 */
public class StdPriorityAssigner extends AbstractPriorityAssigner{
	/**
	 * ID of this factory.
	 */
  public static final String IDENTIFIER = "SystemData StdPriorityAssigner";
  
  private StdPriorityAssignerData priorityAssignerData;
  private Random rnd;

  /**
   * Creates an instance of this class.
   * @param systemModel system model
   * @param priorityAssignerData priority assigner data
   */
  public StdPriorityAssigner(SystemModel systemModel, StdPriorityAssignerData priorityAssignerData){
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
      List<Integer> usedPrios = new LinkedList<Integer>();
      List<Integer> freePrios = new LinkedList<Integer>();
      List<SchedulableElement> priolessElems = new LinkedList<SchedulableElement>();  
      int numElems = 0;

      // init lists
      // go through all applications in the system
      for(ApplicationModel app : systemModel.getApplications()){
        // go through all task in the system
        if(res instanceof Resource){
          for(Task task : app.getTaskList().values()){
            // if the task is mapped to this resource
            if(task.getMappedTo() == res){
              AbstractSchedulingParameter schedParam = task.getSchedulingParameter();
              if(schedParam != null && schedParam instanceof SchedulingPriority){
                usedPrios.add(((SchedulingPriority)schedParam).getPriority());
              } else {
                priolessElems.add(task);
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
                priolessElems.add(taskLink);
              }
              numElems++;
            }
          }
        }
      }

      // assemble list of free priorities
      int j=1;
      while(freePrios.size()<priolessElems.size()){
        if(!usedPrios.contains(j)){
          freePrios.add(j);
        }
        j++;
      }

      // assign random priority from free priority list to prioless tasks
      for(SchedulableElement schedElem : priolessElems){
        int index;
        if(freePrios.size()>1){
          index = rnd.nextInt(freePrios.size()-1);
        } else{
          index = 0;
        }
        // pick random priority from the list
        int priority = freePrios.get(index);
        // remove it from the list
        freePrios.remove(index);
        // assign it to the task
        schedElem.setSchedulingParameter(new SchedulingPriority(priority));
      }
    }
  }

  /**
   * Getter method to fetch the priority assigner data attached to this factory.
   * 
   * @return the priority assigner data attached to this factory
   */
  public StdPriorityAssignerData getPriorityAssignerData() {
    return priorityAssignerData;
  }

  /**
   * Recreate random generator with this seed.
   */
  public void recreateRndGens() {
    rnd = new Random(priorityAssignerData.getSeed());
  }
}
