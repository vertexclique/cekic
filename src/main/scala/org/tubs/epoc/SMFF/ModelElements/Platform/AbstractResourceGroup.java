package org.tubs.epoc.SMFF.ModelElements.Platform;

import org.jdom.Element;
import org.tubs.epoc.SMFF.ImportExport.XML.XMLSaveable;

abstract public class AbstractResourceGroup implements XMLSaveable{

  abstract public String getResourcegroup();
  
  /**
   * generic function to write this resource type to XML (when saving the system model)
   * if you implement your own complex resource type you might want to overwrite this
   * function to  save all relevant data
   */
  abstract public Element toXML();

  /**
   * for resource groups this is not relevant and should not be touched
   */
  final public boolean isCloneable() {
    return false;
  }

  /**
   * for resource groups this is not relevant and should not be touched
   */
  final public boolean isOverwrite() {
    return true;
  }

  /**
   * for resource groups this is not relevant and should not be touched
   */
  final public boolean isIgnoreExisiting() {
    return false;
  }

}
