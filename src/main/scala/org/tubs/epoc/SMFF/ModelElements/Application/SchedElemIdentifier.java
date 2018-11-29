package org.tubs.epoc.SMFF.ModelElements.Application;

/**
 * Interface description for a scheduled element identifier. 
 *
 */
public interface SchedElemIdentifier {

	/**
	 * Compares whether the passed object is same with this instance.
	 * @param compareTo object to be compared for equality
	 * @return <tt>true</tt> if passed object <tt>compareTo</tt> is same with this instance, <tt>false</tt> otherwise.
	 * @see Object#equals(Object)
	 */
  @Override
  public boolean equals(Object compareTo);

  /**
   * Hash code generator for this scheduled element identifier.
   * @return the hash code.
   * @see Object#hashCode()
   */
  @Override
  public int hashCode();

  /**
   * Getter method for the application id of the scheduled element identifier.
   * @return the application id.
   */
  public int getAppId();

  /**
   * Getter method for the application version of the scheduled element identifier.
   * @return the application version.
   */
  public int getAppV();
  
  /**
   * Getter method for the element id of the scheduled element identifier.
   * @return the element id.
   */
  public int getElemId();
}
