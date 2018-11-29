package org.tubs.epoc.SMFF.SystemFactories.TimingFactories;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.SystemFactories.AbstractFactory;

/**
 * Timing factory deals with timing related parameters. 
 *
 */
public abstract class AbstractTimingFactory extends AbstractFactory{
  protected SystemModel systemModel;
  
  /**
   * Constructor.
   * Sets the system model and adds the factory to the system model.
   * 
   * @param systemModel system model
   */
  public AbstractTimingFactory(SystemModel systemModel){
    this.setParent(systemModel);
    this.systemModel = systemModel;
  }
  
  /**
   * Assigns timing properties according to timing factory and its parameters.
   */
  public abstract void generateTimings();
  
  /**
   * Getter method to fetch the timing factory parameters.
   * @return abstract timing factory data
   */
  public abstract AbstractTimingFactoryData getTimingFactoryData();
}
