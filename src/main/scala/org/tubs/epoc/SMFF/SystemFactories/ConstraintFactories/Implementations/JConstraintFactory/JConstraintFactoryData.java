package org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.JConstraintFactory;

import org.jdom.Element;
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.AbstractConstraintFactoryData;

/**
 * Extends AbstractConstraintFactoryData to be used for jitter.
 *
 * @see AbstractConstraintFactoryData
 */
public class JConstraintFactoryData extends AbstractConstraintFactoryData {
  
	double jitterMinLaxity; // minimum factor between actual output jitter and constraint
	double jitterMaxLaxity; // maximum factor between actual output jitter and constraint
  
  /**
   * Creates a new instance of jitter constraint data factory.
   * @param jitterMinLaxity jitter minimum latency
   * @param jitterMaxLaxity jitter maximum latency
   * @param seed seed to be used for random number generation
   * @throws IllegalArgumentException if minimum or maximum latency &lt 1, or  <tt>jitterMinLaxity</tt> &lt 
   * <tt>jitterMaxLaxity</tt>
   */
  public JConstraintFactoryData(double jitterMinLaxity, double jitterMaxLaxity, long seed) {
    super(seed);
    
    if(jitterMinLaxity > jitterMaxLaxity) throw new IllegalArgumentException("jitterMinLaxity > jitterMaxLaxity");
    if(jitterMinLaxity < 1.0)             throw new IllegalArgumentException("jitterMinLaxity < 1.0");
    if(jitterMaxLaxity < 1.0)             throw new IllegalArgumentException("jitterMaxLaxity < 1.0");
    
    this.jitterMinLaxity = jitterMinLaxity;
    this.jitterMaxLaxity = jitterMaxLaxity;
  }
  
  /**
   * Writes the factory data to XML.
   * @return the data as XML element.
   */
  @Override
  public Element toXML(){
    Element root = new Element("ConstraintFactory");
    
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("seed", String.valueOf(this.seed));
    
    root.setAttribute("jitterMinLaxity", String.valueOf(this.jitterMinLaxity));
    root.setAttribute("jitterMaxLaxity", String.valueOf(this.jitterMaxLaxity));
    
    return root;
  }

}
