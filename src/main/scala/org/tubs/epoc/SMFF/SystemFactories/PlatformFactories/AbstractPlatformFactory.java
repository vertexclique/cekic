package org.tubs.epoc.SMFF.SystemFactories.PlatformFactories;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.SystemFactories.AbstractFactory;

/**
 * Abstract platform factory definition.
 *
 */
public abstract class AbstractPlatformFactory extends AbstractFactory{
	/**
	 * System model.
	 */
  protected SystemModel systemModel;
  
  /**
   * Constructor.
   * Sets the system model and adds the factory to the system model.
   * 
   * @param systemModel system model
   */
  public AbstractPlatformFactory(SystemModel systemModel){
    this.setParent(systemModel);
    this.systemModel = systemModel;
  }
  
  /**
   * Generates a platform with parameters from platform factory data.
   */
  public abstract void generatePlatform();
  
  /**
   * Returns platform factory parameters.
   * @return abstract platform factory data
   */
  public abstract AbstractPlatformFactoryData getPlatformFactoryData();
}
