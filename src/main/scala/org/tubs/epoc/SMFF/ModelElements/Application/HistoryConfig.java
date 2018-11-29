package org.tubs.epoc.SMFF.ModelElements.Application;

/**
 * Class definition for history configuration related global static variables, such as local/global analysis results.  
 *
 */
public abstract class HistoryConfig {
	/**
	 * Defines how many local analysis results are saved (0=current, 1=current+previous, ...).
	 */
  public static final int LOC_ANA_TEMP_RES = 1; 
  /**
   * Defines how many global analysis results are saved (0=current, 1=current+previous, ...).
   */
  public static final int GLO_ANA_TEMP_RES = 5; 
}
