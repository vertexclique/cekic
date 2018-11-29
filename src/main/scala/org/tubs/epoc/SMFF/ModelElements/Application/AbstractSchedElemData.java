package org.tubs.epoc.SMFF.ModelElements.Application;

import org.tubs.epoc.SMFF.ModelElements.AbstractDataExtension;

/**
 * Abstract Base Class of Data Objects that may be attached to 
 * Schedulable Elements. 
 * 
 * The functions for addition, removal and retrieval are defined in the 
 * AbstractSchedulableElement Interface and must be implemented
 * in corresponding classes
 * 
 * @author steffens
 *
 */
public abstract class AbstractSchedElemData extends AbstractDataExtension<SchedulableElement> implements Cloneable{
  /**
   * Creates and returns a shallow copy.
   * 
   * @see Object#clone()
   */
  @Override
  public Object clone() 
  {
	  return super.clone();
  }  
}
