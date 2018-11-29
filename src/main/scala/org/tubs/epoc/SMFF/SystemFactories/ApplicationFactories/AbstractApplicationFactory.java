package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.SystemFactories.AbstractFactory;

/**
 * Factory class for applications which is extended from system factories.
 * 
 * @see AbstractFactory
 *
 */
public abstract class AbstractApplicationFactory extends AbstractFactory{  
	/**
	 * System model this factory is attached to.
	 */
  protected SystemModel systemModel;
  
  /**
   * Constructor.
   * Sets the system model and adds the factory to the system model
   * 
   * @param systemModel system model
   */
  public AbstractApplicationFactory(SystemModel systemModel){
    this.setParent(systemModel);
    this.systemModel = systemModel;
  }
  
  /**
   * Generates an application with parameters from application factory data.
   * @return generated application model
   */
  public abstract ApplicationModel generateApplication();
  
  /**
   * Gets platform factory parameters
   * @return current parameter set
   */
  public abstract AbstractApplicationFactoryData getApplicationFactoryData();
}
