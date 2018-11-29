package org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory;

import org.jdom.Element;
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.AbstractPlatformFactoryData;

/**
 * Standard extension of platform data factory.
 * 
 * @see AbstractPlatformFactoryData
 *
 */
public class StdPlatformFactoryData extends AbstractPlatformFactoryData{
	private int numRes;     // number of computational resources
	private int numCommRes; // number of communication resources

  /**
   * Constructs an instance of standard platform data factory.
   * @param numRes number of computational resources
   * @param numCommRes number of communication resources
   * @param seed seed to be used for random number generation
   */
  public StdPlatformFactoryData(
      int numRes,
      int numCommRes,
      long seed) {
    super(seed);
    
    this.numRes = numRes;
    this.numCommRes = numCommRes;
  }
    
  /**
   * Writes the factory data to XML.
   * @return the data as XML element.
   */
  @Override
  public Element toXML(){
    Element root = new Element("PlatformFactory");
    
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("seed", String.valueOf(this.seed));
    
    root.setAttribute("numRes", String.valueOf(this.numRes));
    root.setAttribute("numCommRes", String.valueOf(this.numCommRes));
    
    return root;
  }

  /**
   * Getter method for number of computational resources.
   * @return the number of computational resources
   */
  public int getNumRes() {
    return numRes;
  }

  /**
   * Getter method for number of communication resources.
   * @return the number of communication resources
   */
  public int getNumCommRes() {
    return numCommRes;
  }

}
