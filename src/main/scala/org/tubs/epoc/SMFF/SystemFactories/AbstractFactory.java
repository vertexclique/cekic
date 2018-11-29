package org.tubs.epoc.SMFF.SystemFactories;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tubs.epoc.SMFF.ModelElements.AbstractSystemData;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;

/**
 * Abstract Factory class for the system model. 
 *
 */
public abstract class AbstractFactory extends AbstractSystemData{  
	private static Log logger = LogFactory.getLog(AbstractFactory.class);
  /**
   * Getter method for the attached system model.
   * @return the attached system model
   */
  public SystemModel getSystemModel(){
    if(this.parent instanceof SystemModel){
      return (SystemModel) this.parent;
    }
    else{
      logger.error("Wrong type of parent class.");
      return null;
    }
  }

  /**
   * Recreate all random number generators with the seed in the parameters.
   */
  public abstract void recreateRndGens();
}
