package org.tubs.epoc.SMFF.SystemFactories.SystemFactories;

import org.tubs.epoc.SMFF.SystemFactories.AbstractFactoryData;

/**
 * System factory data includes system parameters to be manipulated an work associated with system factory.
 * 
 *  @see AbstractSystemFactory
 *  @see AbstractFactoryData
 *
 */
public abstract class AbstractSystemFactoryData  extends AbstractFactoryData{

	/**
	 * Seed to be used for random number generation.
	 */
  protected long seed;
  private AbstractSystemFactory systemFactory;
  
  /**
   * Creates a new instance initializing the seed with the parameter passed.
   * @param seed seed to be seed for random number generation
   */
  public AbstractSystemFactoryData(long seed){
    this.seed = seed;
  }
  
  /**
   * Setter method to assign a new seed value to this system factory data.
   * @param seed the new seed value
   */
  public void setSeed(long seed){
    this.seed = seed;
  }
  
  /**
   * Getter method to fetch the seed value attached to this system factory dat
   * @return the seed value
   */
  public long getSeed(){
    return this.seed;
  }
  
  /**
   * Setter method to assign a system factory to this system factory data.
   * @param systemFactory system factory to be assigned to this object
   */
  public void setSystemFactory(AbstractSystemFactory systemFactory){
    this.systemFactory = systemFactory;
  }
  
  /**
   * Getter method to fetch the system factory assigned to this object
   * @return the system factory
   */
  public AbstractSystemFactory getSystemFactory(){
    return this.systemFactory;
  }
}
