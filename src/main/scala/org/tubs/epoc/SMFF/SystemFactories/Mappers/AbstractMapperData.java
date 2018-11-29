package org.tubs.epoc.SMFF.SystemFactories.Mappers;

import org.tubs.epoc.SMFF.SystemFactories.AbstractFactoryData;

/**
 * Data factory for the mapper data. 
 *
 */
public abstract class AbstractMapperData  extends AbstractFactoryData{
	/**
	 * Seed to be used by this data factory.
	 */
  protected long seed;
  AbstractMapper mapper;
  
  /**
   * Constructs a new instance of mapper data.
   * @param seed seed to be used by this mapper data
   */
  public AbstractMapperData(long seed){
    this.seed = seed;
  }
  
  /**
   * Setter methos for the seed.
   * @param seed the new seed to be set to this mapper data factory
   */
  public void setSeed(long seed){
    this.seed = seed;
  }
  
  /**
   * Getter method for the seed.
   * @return the seed value attached to this instance
   */
  public long getSeed(){
    return this.seed;
  }
  
  /**
   * Setter method for the mapper to be attached to this mapper data
   * @param mapper mapper to be attached to this mapper data
   */
  public void setMapper(AbstractMapper mapper){
    this.mapper = mapper;
  }
  
  /**
   * Getter method for the mapper which is attached to this mapper data
   * @return the mapper that is attached to this mapper data
   */
  public AbstractMapper getMapper(){
    return mapper;
  }
}
