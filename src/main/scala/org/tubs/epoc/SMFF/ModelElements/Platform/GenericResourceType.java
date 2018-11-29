package org.tubs.epoc.SMFF.ModelElements.Platform;

import org.jdom.Element;


public class GenericResourceType extends AbstractResourceType {
	  // the below field may not contain any whitespaces (restriction from JDOM where it is saved as new element)
  public static String ResourceType = "GenericResourceType";

  /**
   * gets the resource type as string
   */
  public final String getResourcetype() {
    return ResourceType;
  }
  
  /**
   * Constructor
   */
  public GenericResourceType(){
    super();
  }
  
  /**
   * required constructor from XML (required for loading)
   * @param element
   */
  public GenericResourceType(Element element){
    super();
  }
  
  /**
   * generic function to write this resource type to XML (when saving the system model)
   * if you implement your own complex resource type you might want to overwrite this
   * function to  save all relevant data
   */
  public Element toXML() {
    Element root = new Element("ResourceType");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("name", ResourceType);
    return root;
  }
}
