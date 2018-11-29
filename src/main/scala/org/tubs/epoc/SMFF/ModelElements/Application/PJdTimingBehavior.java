package org.tubs.epoc.SMFF.ModelElements.Application;

import org.jdom.Element;
import org.tubs.epoc.SMFF.ImportExport.XML.XMLSaveable;

/**
 * PJdTimingBehavior is a timing behavior implementation which is extended from 
 * {@link AbstractTimingBehavior AbstractTimingBehavior} and may be attached to 
 * {@link SchedulableElement SchedulableElement}. PJD stands for period, jitter and dmin.
 * These values define a valid PJdTimingBehavior.
 * 
 * @see AbstractTimingBehavior
 *
 */
public class PJdTimingBehavior extends AbstractTimingBehavior implements XMLSaveable{  
  private int period;
  private int jitter;
  private int dmin;
  
  /**
   * Constructor which sets all the fields.
   * @param period period of this timing behavior 
   * @param jitter jitter of this timing behavior 
   * @param dmin dmin of this timing behavior 
   */
  public PJdTimingBehavior(int period, int jitter, int dmin){
    this.setAssertion(period, jitter, dmin);
  }
  
  /**
   * Default constructor is allowed. All the values of this timing behavior (period, jitter, and
   * dmin) will be set to 0, in case objects are created with this constructor. 
   */
  public PJdTimingBehavior(){
    this.setAssertion(0, 0, 0);
  }  
  

  public PJdTimingBehavior(Element element){
    super();
    String periodString = element.getAttributeValue("period");
    String jitterString = element.getAttributeValue("jitter");
    String dminString = element.getAttributeValue("dmin");
    if(periodString!=null &&
       jitterString!=null &&
       dminString!=null){
      this.period = Integer.valueOf(periodString);
      this.jitter = Integer.valueOf(jitterString);
      this.dmin = Integer.valueOf(dminString);
    }
  }

  @Override
  public Element toXML() {
    Element root = new Element("PJdTimingBehavior");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("period", String.valueOf(this.period));
    root.setAttribute("jitter", String.valueOf(this.jitter));
    root.setAttribute("dmin", String.valueOf(this.dmin));
    return root;
  }
  
  /**
   * Setter and manipulator value for the certain properties of the timing bahavior.
   * @param period new period value of this timing behavior 
   * @param jitter new jitter value of this timing behavior
   * @param dmin new dmin value of this timing behavior
   */
  public void setAssertion(int period, int jitter, int dmin){
    this.period = period;
    this.jitter = jitter;
    this.dmin   = dmin;
  }
  
  /**
   * Getter method for the period of this timing behavior. 
   * @return the period of this timing behavior. 
   */
  public int getPeriod() {
    return period;
  }
  
  /**
   * Setter method for the period of this timing behavior.
   * @param period the new period value to be set.
   */
  public void setPeriod(int period) {
    this.period = period;
  }
  
  /**
   * Getter method for the jitter value of this timing behavior.
   * @return the jitter value of this timing behavior.
   */
  public int getJitter() {
    return jitter;
  }
  
  /**
   * Setter method for the jitter of this timing behavior.
   * @param jitter the new jitter value to be set to this timing behavior.
   */
  public void setJitter(int jitter) {
    this.jitter = jitter;
  }
  
  /**
   * Getter method for the dmin value of this timing behavior.
   * @return the dmin value of this timing behavior.
   */
  public int getDmin() {
    return dmin;
  }
  
  /**
   * Setter method for the dmin of this timing behavior.
   * @param dmin the new dmin value to be set to this timing behavior.
   */
  public void setDmin(int dmin) {
    this.dmin = dmin;
  }
  
  /**
   * Description of this class which gives information about period, jitter and dmin value of this timing behavior.
   * @return the decription of the class. 
   */
  public String toString(){
    return "P:"+period+" J:"+jitter+" D_min:"+dmin;
  }

  @Override
  public boolean isCloneable() {
    return true;
  }

  @Override
  public boolean isOverwrite() {
    return true;
  }

  @Override
  public boolean isIgnoreExisiting() {
    return false;
  }
}
