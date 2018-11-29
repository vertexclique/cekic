package org.tubs.epoc.SMFF.ModelElements.Platform;

import org.jdom2.Element;

public class GenericResourceGroup extends AbstractResourceGroup {
  // the below field may not contain any whitespaces (restriction from JDOM where it is saved as new element)
  public static String ResourceGroup = "GenericResourceGroup";

  public final String getResourcegroup() {
    return ResourceGroup;
  }
  
  /**
   * Constructor
   */
  public GenericResourceGroup(){
    super();
  }
  
  /**
   * required constructor from XML (required for loading)
   * @param element
   */
  public GenericResourceGroup(Element element){
    super();
  }
  
  /**
   * generic function to write this resource type to XML (when saving the system model)
   * if you implement your own complex resource type you might want to overwrite this
   * function to  save all relevant data
   */
  public Element toXML() {
    Element root = new Element("ResourceGroup");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("name", ResourceGroup);
    return root;
  }
}
