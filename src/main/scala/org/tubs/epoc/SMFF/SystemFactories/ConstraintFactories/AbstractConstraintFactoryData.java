package org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories;

import org.tubs.epoc.SMFF.SystemFactories.AbstractFactoryData;

/**
 * Abstract constraint data factory.
 * 
 *  @see AbstractFactoryData
 *
 */
public abstract class AbstractConstraintFactoryData extends AbstractFactoryData{

	/**
	 * Seed for random number generation-
	 */
  protected long seed;
  private AbstractConstraintFactory constraintFactory;
  
  /**
   * Creates an instance of constraint data factory.
   * @param seed seed to be used for random number generation
   */
  public AbstractConstraintFactoryData(long seed){
    this.seed = seed;
  }
  
  /**
   * Setter method for the seed of this data factory.
   * @param seed the new seed for this data factory
   */
  public void setSeed(long seed){
    this.seed = seed;
  }
  
  /**
   * Getter method for the seed.
   * @return the seed of this factory.
   */
  public long getSeed(){
    return this.seed;
  }
  
  /**
   * Setter method for the constraint factory of this data factory.
   * @param constraintFactory the constraint factory of this data factory
   */
  public void setConstraintFactory(AbstractConstraintFactory constraintFactory){
    this.constraintFactory = constraintFactory;
  }
  
  /**
   * Getter method for the constraint factory of this data factory.
   * @return the constraint factory of this data factory
   */
  public AbstractConstraintFactory getConstraintFactory(){
    return this.constraintFactory;
  }
}
