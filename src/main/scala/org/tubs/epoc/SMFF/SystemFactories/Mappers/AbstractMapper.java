package org.tubs.epoc.SMFF.SystemFactories.Mappers;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.SystemFactories.AbstractFactory;

/**
 * An abstract mapper class to be used to map an application to the system model. 
 *
 */
abstract public class AbstractMapper extends AbstractFactory{
  protected SystemModel systemModel;
  
  /**
   * Constructor.
   * Sets the system model and adds the factory to the system model.
   * 
   * @param systemModel system model
   */
  public AbstractMapper(SystemModel systemModel){
    this.setParent(systemModel);
    this.systemModel = systemModel;
  }
  
  /**
   * Resets the mapping for this application.
   * @param app application
   */
  public void resetMapping(ApplicationModel app){
    for(Task task : app.getTaskList().values()){
      app.unmapTask(task);
    }
    for(TaskLink taskLink : app.getTaskLinkList().values()){
      app.unmapTaskLink(taskLink);
    }
  }
  
  /**
   * Maps application to the system model using the rules of this mapper and its parameter set.
   * @param app apllication to be mapped to the system model of this mapper
   */
  public abstract void map(ApplicationModel app);
  
  /**
   * Returns mapper parameters.
   * @return abstract mapper data
   */
  public abstract AbstractMapperData getMapperData();
}
