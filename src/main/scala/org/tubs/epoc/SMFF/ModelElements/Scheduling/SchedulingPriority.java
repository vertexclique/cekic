package org.tubs.epoc.SMFF.ModelElements.Scheduling;

import org.jdom.Element;

public class SchedulingPriority extends AbstractSchedulingParameter{
	  // the below field may not contain any whitespaces (restriction from JDOM where it is saved as new element)
  public static String SchedulingParameterName = "SchedulingPriority";
  
  private int priority;
  
  public SchedulingPriority(int priority){
    super();
    this.priority = priority;
  }
  
  public SchedulingPriority(Element e){
    this(Integer.valueOf(e.getAttributeValue("priority")));
  }

  public final int getPriority() {
    return priority;
  }

  public final void setPriority(int priority) {
    this.priority = priority;
  }

  @Override
  public Element toXML() {
    Element root = new Element("SchedulingParameter");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("name", SchedulingParameterName);
    root.setAttribute("priority", String.valueOf(priority));
    return root;
  }

  @Override
  public Object clone() {
    return new SchedulingPriority(this.priority);
  }
}
