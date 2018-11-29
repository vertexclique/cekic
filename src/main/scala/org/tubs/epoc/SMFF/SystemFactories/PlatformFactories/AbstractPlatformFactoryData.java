package org.tubs.epoc.SMFF.SystemFactories.PlatformFactories;

import org.tubs.epoc.SMFF.SystemFactories.AbstractFactoryData;

/**
 * Abstract data factory for the platform.
 *
 */
public abstract class AbstractPlatformFactoryData  extends AbstractFactoryData{

	/**
	 * Seed to be used for random number generation.
	 */
  protected long seed;
  private AbstractPlatformFactory platformFactory;
  
  /**
   * Constructs a platform factory with the specified seed.
   * @param seed seed to be used for random number generation
   */
  public AbstractPlatformFactoryData(long seed){
    this.seed = seed;
  }
  
  /**
   * Setter method for the seed.
   * @param seed the new seed value to be set for this platform data factory
   */
  public void setSeed(long seed){
    this.seed = seed;
  }
  
  /**
   * Getter method for the seed.
   * @return the seed
   */
  public long getSeed(){
    return this.seed;
  }
  
  /**
   * Setter method for the platform factory attached to this platform data factory.
   * @param platformFactory platform factory to be attached to this platform data factory
   */
  public void setPlatformFactory(AbstractPlatformFactory platformFactory){
    this.platformFactory = platformFactory;
  }
  
  /**
   * Getter method for the platform factory attached to this platform data factory.
   * @return platform factory that is attached to this platform data factory
   */
  public AbstractPlatformFactory getPlatformFactory(){
    return this.platformFactory;
  }
}
