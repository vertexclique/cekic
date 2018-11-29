package org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.TaskChainPriorityAssigner;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.AbstractPriorityAssignerData;

/**
 * Assigner data associated with the assigner factory.
 * 
 *  @see TaskChainPriorityAssigner
 */
public class TaskChainPriorityAssignerData extends AbstractPriorityAssignerData{

  /**
   * Constructor for random priority assigner.
   * @param seed random seed
   */
  public TaskChainPriorityAssignerData(long seed) {
    super(seed);
  }
    
  /**
   * Writes the factory data to XML.
   * @return the data as XML element.
   */
  @Override
  public Element toXML(){
    Element root = new Element("PriorityAssigner");
    
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("seed", String.valueOf(this.seed));
    return root;
  }
}
