package org.tubs.epoc.SMFF.ModelElements.Application;

import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceGroup;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceType;
import org.tubs.epoc.SMFF.ModelElements.Platform.GenericResourceGroup;
import org.tubs.epoc.SMFF.ModelElements.Platform.GenericResourceType;
import org.tubs.epoc.SMFF.ModelElements.Timing.AbstractActivationPattern;
import org.tubs.epoc.SMFF.ModelElements.Timing.PJActivation;

/**
 * Class description for a profile. It is defined by target resource related properties such as 
 * power used at target resource, memory needed at target resource, number of OS events needed at target resource, etc.
 * Some other properties related to a Profile are worst/best case execution time,  activation period and
 * activation jitter. 
 *
 */
public class Profile {
  private int bcet;
  private int wcet;
  private AbstractActivationPattern activationPattern;
  private AbstractResourceGroup resGroup;
  private AbstractResourceType resType;
  
  /**
   * Constructor for a profile of a task.
   * @param bcet best-case execution time
   * @param wcet worst-case execution time
   * @param actPeriod activation period
   * @param actJitter activation jitter
   */
  public Profile(
      int bcet,
      int wcet,
      int actPeriod,
      int actJitter){
    this(bcet, wcet, new PJActivation(actPeriod, actJitter));
  }
  
  /**
   * Constructor for a profile of a task.
   * @param bcet best-case execution time
   * @param wcet worst-case execution time
   * @param argActivationPattern activation pattern
   */
  public Profile(
      int bcet,
      int wcet,
      AbstractActivationPattern activationPattern){
    this(bcet, wcet, activationPattern, new GenericResourceType(), new GenericResourceGroup());
  }
  
  /**
   * Constructor for a profile of a task.
   * @param bcet best-case execution time
   * @param wcet worst-case execution time
   * @param argActivationPattern activation pattern
   */
  public Profile(
      int bcet,
      int wcet,
      AbstractActivationPattern activationPattern,
      AbstractResourceType resType,
      AbstractResourceGroup resGroup){
    this.wcet = wcet;
    this.bcet =  bcet;
    this.activationPattern = activationPattern;
    this.resGroup = resGroup;
    this.resType = resType;
  }

  /**
   * Getter method for worst case execution time.
   * @return the worst case execution time
   */
  public int getWCET() {
    return wcet;
  }
  
  /**
   * Setter method for worst case execution time.
   * @param wcet the new worst case execution time for this profile instance
   */
  public void setWCET(int wcet){
    this.wcet = wcet;
  }

  /**
   * Getter method for best case execution time.
   * @return the best case execution time
   */
  public int getBCET() {
    return bcet;
  }
  
  /**
   * Setter method for best case execution time.
   * @param bcet the new best case execution time for this profile instance
   */
  public void setBCET(int bcet){
    this.bcet = bcet;
  }

  /**
   * returns the associated activation pattern
   * @return AbstractActivationPattern
   */
  public AbstractActivationPattern getActivationPattern(){
    return this.activationPattern;
  }

  /**
   * sets the associated activation pattern
   * @param AbstractActivationPattern
   */
  public void setActivationPattern(AbstractActivationPattern activationPattern){
    this.activationPattern = activationPattern;
  }
  
  /**
   * Setter method for activation period.
   * @param activationPeriod the new activation period for this profile instance
   * @deprecated PJ activation used to be standard - this is replaced by the more
   * flexible abstract functions
   */
  public final void setActivationPeriod(int activationPeriod) {
    if(this.activationPattern instanceof PJActivation){
      ((PJActivation)this.activationPattern).setActivationPeriod(activationPeriod);
    }
  }

  /**
   * Setter method for activation jitter.
   * @param activationJitter the new activation jitter for this profile instance
   * @deprecated PJ activation used to be standard - this is replaced by the more
   * flexible abstract functions
   */
  public final void setActivationJitter(int activationJitter) {
    if(this.activationPattern instanceof PJActivation){
      ((PJActivation)this.activationPattern).setActivationJitter(activationJitter);
    }
  }

  /**
   * Getter method for activation period.
   * @return the activation period for this profile instance
   * @deprecated PJ activation used to be standard - this is replaced by the more
   * flexible abstract functions
   */
  public int getActivationPeriod() {
    if(this.activationPattern instanceof PJActivation){
      return ((PJActivation)this.activationPattern).getActivationPeriod();
    } else{
      return 0;
    }
  }

  /**
   * Getter method for activation jitter.
   * @return the activation jitter for this profile instance
   * @deprecated PJ activation used to be standard - this is replaced by the more
   * flexible abstract functions
   */
  public int getActivationJitter() {
    if(this.activationPattern instanceof PJActivation){
      return ((PJActivation)this.activationPattern).getActivationJitter();
    } else{
      return 0;
    }
  }

  /**
   * returns the group of resources this profile applies to
   * @return AbstractResourceGroup
   */
  public final AbstractResourceGroup getResGroup() {
    return resGroup;
  }

  /**
   * sets the group of resources this profile applies to
   * @param AbstractResourceGroup
   */
  public final void setResGroup(AbstractResourceGroup resGroup) {
    this.resGroup = resGroup;
  }

  /**
   * returns the type of resources this profile applies to
   * @return AbstractResourceGroup
   */
  public final AbstractResourceType getResType() {
    return resType;
  }


  /**
   * sets the type of resources this profile applies to
   * @param AbstractResourceType
   */
  public final void setResType(AbstractResourceType resType) {
    this.resType = resType;
  }
}
