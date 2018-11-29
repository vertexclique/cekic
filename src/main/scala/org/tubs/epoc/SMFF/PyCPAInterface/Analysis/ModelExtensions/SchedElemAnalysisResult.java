package org.tubs.epoc.SMFF.PyCPAInterface.Analysis.ModelExtensions;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.ImportExport.XML.XMLSaveable;
import org.tubs.epoc.SMFF.ModelElements.Application.AbstractSchedElemData;
import org.tubs.epoc.SMFF.ModelElements.Application.PJdTimingBehavior;

public class SchedElemAnalysisResult extends AbstractSchedElemData implements XMLSaveable{
  private Integer bcrt;
  private Integer wcrt;
  private PJdTimingBehavior inputBehavior;
  private PJdTimingBehavior outputBehavior;
  
  
  public SchedElemAnalysisResult(int bcrt, int wcrt, int period, int injitter, int outjitter){
    this.bcrt = bcrt;
    this.wcrt = wcrt;
    this.inputBehavior = new PJdTimingBehavior(period, injitter, 0);
    this.outputBehavior = new PJdTimingBehavior(period, outjitter, 0);
  }
  
  /**
   * new jitter constraint at the output created from a jdom XML element
   * @param element
   */
  public SchedElemAnalysisResult(Element element){
    super();
    String bcrtString = element.getAttributeValue("bcrt");
    String wcrtString = element.getAttributeValue("wcrt");
    Element inputBehaviorXML = element.getChild("InputBehavior");
    Element outputBehaviorXML = element.getChild("OutputBehavior");
    if(bcrtString!=null &&
        wcrtString!=null &&
        inputBehaviorXML!=null &&
        outputBehaviorXML!=null){
      this.bcrt = Integer.valueOf(bcrtString);
      this.wcrt = Integer.valueOf(wcrtString);
      this.inputBehavior = new PJdTimingBehavior(inputBehaviorXML.getChild("PJdTimingBehavior"));
      this.outputBehavior = new PJdTimingBehavior(outputBehaviorXML.getChild("PJdTimingBehavior"));
    }
  }

  @Override
  public Element toXML() {
    Element root = new Element("SchedElemAnalysisResult");
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("bcrt", String.valueOf(this.bcrt));
    root.setAttribute("wcrt", String.valueOf(this.wcrt));
    Element inputBehaviorXML = new Element("InputBehavior");
    inputBehaviorXML.addContent(this.inputBehavior.toXML());
    Element outputBehaviorXML = new Element("OutputBehavior");
    outputBehaviorXML.addContent(this.outputBehavior.toXML());
    root.addContent(inputBehaviorXML);
    root.addContent(outputBehaviorXML);
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
