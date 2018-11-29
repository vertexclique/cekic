package org.tubs.epoc.SMFF.ModelElements.DataExtensions;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.ImportExport.XML.XMLSaveable;
import org.tubs.epoc.SMFF.ModelElements.Application.AbstractSchedElemData;
import org.tubs.epoc.SMFF.ModelElements.Application.PJdTimingBehavior;

/**
 * Schedulable element data extension to provide jitter constraints.
 * 
 * A PJdConstraint is defined by a timing behavior which is made available to the instance during constructing the
 * instance.
 * 
 * @author moritzn
 * 
 */
public class PJdConstraint extends AbstractSchedElemData implements XMLSaveable, Cloneable {
	// constraint on jitter at the output of this sched elem
	protected PJdTimingBehavior constraint;

	/**
	 * Initialize a new constraint at the output with the provided timing behavior value.
	 * 
	 * @param constraint
	 *          constraint through which a PJdConstraint instance will be created.
	 */
	public PJdConstraint(PJdTimingBehavior constraint) {
		super();
		this.constraint = constraint;
	}
	
  /**
   * new jitter constraint at the output created from a jdom XML element
   * @param element
   */
  public PJdConstraint(Element element){
    super();
    String pConstraintString = element.getAttributeValue("PConstraint");
    String jConstraintString = element.getAttributeValue("JConstraint");
    String dConstraintString = element.getAttributeValue("dConstraint");
    if( pConstraintString!=null &&
        jConstraintString!=null &&
        dConstraintString!=null){
      this.constraint = new PJdTimingBehavior(Integer.valueOf(pConstraintString), Integer.valueOf(jConstraintString), Integer.valueOf(dConstraintString));
    }
  }

  /**
   * returns a JDOM element representing this object in XML
   */
  @Override
  public Element toXML() {
    Element root = new Element("SaveablePJdConstraint");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("PConstraint", String.valueOf(this.constraint.getPeriod()));
    root.setAttribute("JConstraint", String.valueOf(this.constraint.getJitter()));
    root.setAttribute("dConstraint", String.valueOf(this.constraint.getDmin()));
    return root;
  }
  
  @Override
  public boolean isCloneable() {
    return true;
  }

  @Override
  public boolean isIgnoreExisiting() {
    return false;
  }

  @Override
  public boolean isOverwrite() {
    return false;
  }

	/**
	 * Getter method for returning the constraint value related with the timing behavior.
	 * 
	 * @return the constraint for this instance.
	 */
	public final PJdTimingBehavior getConstraint() {
		return constraint;
	}
}
