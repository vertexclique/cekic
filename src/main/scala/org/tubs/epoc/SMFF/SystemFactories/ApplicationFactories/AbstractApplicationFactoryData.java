package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories;

import org.tubs.epoc.SMFF.SystemFactories.AbstractFactoryData;


/**
 * Class definition for the application factory data element.
 * 
 * @see AbstractFactoryData
 *
 */
public abstract class AbstractApplicationFactoryData extends AbstractFactoryData{
	/**
	 * Random seed. 
	 */
  protected long seed;

  /**
   * Attached application factory.
   */
  private AbstractApplicationFactory applicationFactory;
  
  /**
   * Constructor.
   * Creates the application factory data element.
   * @param applicationFactory attached application factory
   * @param seed random seed
   */
  public AbstractApplicationFactoryData(AbstractApplicationFactory applicationFactory, long seed){
    this.applicationFactory = applicationFactory;
    this.seed = seed;
  }
  
  /**
   * Constructor.
   * Creates the application factory data element.
   * Overloaded constructor without an attached application factory.
   * @param seed random seed
   */
  public AbstractApplicationFactoryData(long seed){
    this.seed = seed;
  }
  
  /**
   * Setter method for the random seed.
   * @param seed the new random seed
   */
  public void setSeed(long seed){
    this.seed = seed;
  }
  
  /**
   * Getter method for the random seed.
   * @return the random seed
   */
  public long getSeed(){
    return this.seed;
  }
  
  /**
   * Setter method for the attached application factory.
   * @param applicationFactory the (new) application factory to attach
   */
  public void setApplicationFactory(AbstractApplicationFactory applicationFactory){
    this.applicationFactory = applicationFactory;
  }
  
  /**
   * Getter method for the attached application factory.
   * @return the attached application factory
   */
  public AbstractApplicationFactory getApplicationFactory(){
    return this.applicationFactory;
  }
}
