package org.tubs.epoc.SMFF.ModelElements.Timing;

import org.jdom.Element;
import org.tubs.epoc.SMFF.ImportExport.XML.XMLSaveable;

public abstract class AbstractActivationPattern implements XMLSaveable{
  
  /**
   * generic function to write this scheduler to XML (when saving the system model)
   * if you implement your own complex resource type you might want to overwrite this
   * function to  save all relevant data
   */
  abstract public Element toXML();

  /**
   * for schedulers this is not relevant and should not be touched
   */
  final public boolean isCloneable() {
    return false;
  }

  /**
   * for schedulers this is not relevant and should not be touched
   */
  final public boolean isOverwrite() {
    return true;
  }

  /**
   * for schedulers this is not relevant and should not be touched
   */
  final public boolean isIgnoreExisiting() {
    return false;
  }
}
