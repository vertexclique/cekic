package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.TaskChainApplicationFactory;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.AbstractApplicationFactoryData;

/**
 * Implementation of the application factory data to be used for tgff.
 *
 */
public class TaskChainApplicationFactoryData extends AbstractApplicationFactoryData{      
  // options
  private int numTasks;           // number of tasks
  
  /**
   * Constructor to use the new version of TGFF.
   * @param numTasks number of tasks
   * @param seed for the number generator
   */
  public TaskChainApplicationFactoryData(
      int numTasks, long seed) {
    super(seed);
    this.numTasks = numTasks;
  }
  
  /**
   * Writes the factory data to XML.
   * @return the data as XML element.
   */
  @Override
  public Element toXML(){
    Element root = new Element("ApplicationFactory");
    
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("seed", String.valueOf(this.seed));
    
    // set parameters
    root.setAttribute("numTasks", String.valueOf(this.numTasks));
    
    return root;
  }

  /**
   * Getter method for number of tasks for this tgff application factory data.
   * @return the number of tasks
   */
  public int getNumTasks() {
    return numTasks;
  }
}
