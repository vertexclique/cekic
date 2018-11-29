package org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.SystemFactories.AbstractFactory;

/**
 * An abstract factory to be extended by constraint factories.   
 *
 */
public abstract class AbstractConstraintFactory extends AbstractFactory{
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
  public AbstractConstraintFactory(SystemModel systemModel){
    this.setParent(systemModel);
    this.systemModel = systemModel;
  }
  
  /**
   * Abstract method implementation of which must assign priorities according to constraint generator and its 
   * parameters.
   */
  public abstract void generateConstraints();
  
  /**
   * Returns constraint factory parameters
   * @return constraint factory data
   */
  public abstract AbstractConstraintFactoryData getConstraintFactoryData();
}
