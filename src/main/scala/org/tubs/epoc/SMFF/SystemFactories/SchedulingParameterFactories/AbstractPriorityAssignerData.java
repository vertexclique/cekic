package org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories;

import org.tubs.epoc.SMFF.SystemFactories.AbstractFactoryData;

/**
 * Priority assigner data factory which works in accordance with priority assigner factory.
 * 
 *  @see AbstractFactoryData
 *
 */
public abstract class AbstractPriorityAssignerData  extends AbstractFactoryData{

	/**
	 * Seed to be used for random number generation.
	 */
  protected long seed;
  private AbstractPriorityAssigner priorityAssigner;
  
  /**
   * Constructor to create an instance of priority assigner data.
   * @param seed seed to be used for random number generation
   */
  public AbstractPriorityAssignerData(long seed){
    this.seed = seed;
  }
  
  /**
   * Setter method to assign a new value for the seed.
   * @param seed the new seed value
   */
  public void setSeed(long seed){
    this.seed = seed;
  }
  
  /**
   * Getter method to fetch the seed value belonging to this abstract factory.
   * @return the seed value
   */
  public long getSeed(){
    return this.seed;
  }
  
  /**
   * Setter method to attach a priority assigner factory to this priority assigner data factory
   * @param priorityAssigner priority assigner factory
   */
  public void setPriorityAssigner(AbstractPriorityAssigner priorityAssigner){
    this.priorityAssigner = priorityAssigner;
  }
  
  /**
   * Getter method to fetch the priority assigner factory of this instance.
   * @return the priority assigner factory belonging to this instance
   */
  public AbstractPriorityAssigner getPriorityAssigner(){
    return this.priorityAssigner;
  }
}
