package org.tubs.epoc.SMFF.ModelElements.Scheduling;

import org.jdom.Element;
import org.tubs.epoc.SMFF.ImportExport.XML.XMLSaveable;

public abstract class AbstractSchedulingParameter implements XMLSaveable, Cloneable{
  
  /**
   * generic function to write this scheduling parameter to XML (when saving the system model)
   * if you implement your own complex resource type you might want to overwrite this
   * function to  save all relevant data
   */
  abstract public Element toXML();
  
  /**
   * 
   */
  abstract public Object clone();

  /**
   * for scheduling parameters this is not relevant and should not be touched
   */
  final public boolean isCloneable() {
    return true;
  }

  /**
   * scheduling parameters may not exist multiple times and thus should overwrite existing ones
   */
  final public boolean isOverwrite() {
    return true;
  }

  /**
   * scheduling parameters should overwrite existing parameters -> false
   */
  final public boolean isIgnoreExisiting() {
    return false;
  }

}
