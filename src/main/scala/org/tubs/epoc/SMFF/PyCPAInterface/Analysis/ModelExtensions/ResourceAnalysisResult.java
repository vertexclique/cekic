package org.tubs.epoc.SMFF.PyCPAInterface.Analysis.ModelExtensions;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.ImportExport.XML.XMLSaveable;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceData;

public class ResourceAnalysisResult extends AbstractResourceData implements XMLSaveable{
  private Double load;
  
  
  /**
   * Initialize a new constraint at the output with the provided timing behavior value.
   * 
   * @param constraint
   *          constraint through which a PJdConstraint instance will be created.
   */
  public ResourceAnalysisResult(Double load) {
    super();
    this.load = load;
  }
  
  /**
   * new jitter constraint at the output created from a jdom XML element
   * @param element
   */
  public ResourceAnalysisResult(Element element){
    super();
    String loadString = element.getAttributeValue("load");
    if(loadString!=null){
      this.load = Double.valueOf(loadString);
    }
  }

  public Double getLoad() {
    return load;
  }

  @Override
  public Element toXML() {
    Element root = new Element("ResourceAnalysisResult");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("load", String.valueOf(this.load));
    return root;
  }

  @Override
  public boolean isCloneable() {
    return false;
  }

  @Override
  public boolean isOverwrite() {
    return true;
  }

  @Override
  public boolean isIgnoreExisiting() {
    // TODO Auto-generated method stub
    return false;
  }
}
