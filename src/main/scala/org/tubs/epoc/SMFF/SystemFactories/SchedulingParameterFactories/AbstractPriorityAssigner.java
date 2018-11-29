package org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.SystemFactories.AbstractFactory;

/**
 * Abstract factory which manipulates the priorities.  
 *
 */
public abstract class AbstractPriorityAssigner extends AbstractFactory{
	/**
	 * System model to be added to the factory.
	 */
  protected SystemModel systemModel;
  
  /**
   * Constructor.
   * Sets the system model and adds the factory to the system model.
   * 
   * @param systemModel system model to be added to the factory
   */
  public AbstractPriorityAssigner(SystemModel systemModel){
    this.setParent(systemModel);
    this.systemModel = systemModel;
  }
  
  /**
   * Resets the all priorities in the system model.
   */
  public void resetPriorities(){
    for(ApplicationModel app : systemModel.getApplications()){
      for(Task task : app.getTaskList().values()){
        task.setPrio(-1);
      }
      for(TaskLink taskLink : app.getTaskLinkList().values()){
        taskLink.setPrio(-1);
      }
    }
  }
  
  /**
   * Abstract method to be implemented in order to 
   * assign priorities according to priority assigner and its parameters.
   */
  public abstract void assignPriorities();
  
  /**
   * Reassigns all priorities.
   */
  public void reassignPriorities(){
    resetPriorities();
    assignPriorities();
  }
  
  /**
   * Abstract method which will return platform factory parameters.
   * @return abstract priority assigner data.
   */
  public abstract AbstractPriorityAssignerData getPriorityAssignerData();
}
