package org.tubs.epoc.SMFF.ModelElements.Timing;

import org.jdom.Element;

public class EventActivation extends AbstractActivationPattern {
	  // the below field may not contain any whitespaces (restriction from JDOM where it is saved as new element)
  public static String ActivationPatternName = "EventActivation";
  
  
  public EventActivation() {
    super();
  }
  
  public EventActivation(Element e) {
    super();
  }
  
  @Override
  public Element toXML() {
    Element root = new Element("ActivationPattern");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("name", ActivationPatternName);
    return root;
  }
}
