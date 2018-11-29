package org.tubs.epoc.SMFF.ModelElements.Timing;

import org.jdom2.Element;

public class PJActivation extends AbstractActivationPattern {
	  // the below field may not contain any whitespaces (restriction from JDOM where it is saved as new element)
  public static String ActivationPatternName = "PJActivation";
  
  private int activationPeriod;
  private int activationJitter; 
  
  public PJActivation(int activationPeriod, int activationJitter) {
    super();
    this.activationPeriod = activationPeriod;
    this.activationJitter = activationJitter;
  }
  
  public PJActivation(Element e) {
    super();
    this.activationPeriod = Integer.valueOf(e.getAttributeValue("activationPeriod"));
    this.activationJitter = Integer.valueOf(e.getAttributeValue("activationJitter"));
  }
   
  public final void setActivationPeriod(int activationPeriod) {
    this.activationPeriod = activationPeriod;
  }

  public final void setActivationJitter(int activationJitter) {
    this.activationJitter = activationJitter;
  }

  public final int getActivationPeriod() {
    return activationPeriod;
  }
  public final int getActivationJitter() {
    return activationJitter;
  }
  
  @Override
  public Element toXML() {
    Element root = new Element("ActivationPattern");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("name", ActivationPatternName);
    root.setAttribute("activationPeriod", String.valueOf(activationPeriod));
    root.setAttribute("activationJitter", String.valueOf(activationJitter));
    return root;
  }
}
