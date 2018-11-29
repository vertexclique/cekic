package org.tubs.epoc.SMFF.SystemFactories.TimingFactories;

import org.tubs.epoc.SMFF.SystemFactories.AbstractFactoryData;

/**
 * This class includes timing related parameters / data and is associated with timing factory.
 *
 * @see AbstractFactoryData
 * @see AbstractTimingFactory
 */
public abstract class AbstractTimingFactoryData  extends AbstractFactoryData{

	/**
	 * Random seed parameter.
	 */
  protected long seed;
  private AbstractTimingFactory timingFactory;
  
  /**
   * Creates an instance of timing factory data.
   * @param seed the random seed parameter
   */
  public AbstractTimingFactoryData(long seed){
    this.seed = seed;
  }
  
  /**
   * The setter method which assigns a seed value to the timing factory.
   * @param seed the new seed value
   */
  public void setSeed(long seed){
    this.seed = seed;
  }
  
  /**
   * Getter method to fetch the seed value attached to this object.
   * @return the seed value
   */
  public long getSeed(){
    return this.seed;
  }
  
  /**
   * Setter method to assign a timing factory to this object.
   * @param timingFactory timing factory
   */
  public void setTimingFactory(AbstractTimingFactory timingFactory){
    this.timingFactory = timingFactory;
  }
  
  /**
   * Getter method to fetch the timing factory attached to this timing factory data. 
   * @return the timing factory of this object.
   */
  public AbstractTimingFactory getTimingFactory(){
    return this.timingFactory;
  }
}
