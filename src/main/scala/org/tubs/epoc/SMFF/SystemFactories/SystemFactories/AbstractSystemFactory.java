package org.tubs.epoc.SMFF.SystemFactories.SystemFactories;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;

/**
 * Abstract factory intended to be extended by the system factories which will manipulate the system model
 * and parameters.  
 *
 */
public abstract class AbstractSystemFactory{
  protected SystemModel systemModel;
  
  /**
   * Constructor.
   */
  protected AbstractSystemFactory(){
    systemModel = new SystemModel();
  }
  
  /**
   * Generates an application with parameters from application factory data.
   * 
   * @return the system model
   */
  public abstract SystemModel generateSystem();
  
  /**
   * Getter method to fetch the system factory parameters.
   * @return the system factory data 
   */
  public abstract AbstractSystemFactoryData getSystemFactoryData();
  
  /**
   * Recreate all random number generators with the seed in the parameters.
   */
  public abstract void recreateRndGens();
}
