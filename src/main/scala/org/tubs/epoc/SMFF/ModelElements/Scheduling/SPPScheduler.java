package org.tubs.epoc.SMFF.ModelElements.Scheduling;

import org.jdom.Element;

public class SPPScheduler extends AbstractScheduler {
	  // the below field may not contain any whitespaces (restriction from JDOM where it is saved as new element)
  public static String SchedulerName = "SPPScheduler";

  public SPPScheduler() {
    super(SchedulingPriority.class);
  }
  
  public SPPScheduler(Element e){
    this();
  }

  @Override
  public Element toXML() {
    Element root = new Element("Scheduler");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("name", SchedulerName);
    return root;
  }
}
