package org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.StdConstraintFactory;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.AbstractConstraintFactoryData;

/**
 * Extends AbstractConstraintFactoryData to be used for laxity constraints.
 *
 * @see AbstractConstraintFactoryData
 */
public class LaxityConstraintFactoryData extends AbstractConstraintFactoryData{
  // Parameters
  double minConstrFactor;     // minimum constraint laxity (constr/sumWcets)
  double maxConstrFactor;     // maximum constraint laxity (constr/sumWcets)

  /**
   * constructor for random priority assigner
   * @param minConstrFactor minimum constraint laxity
   * @param maxConstrFactor maximum constraint laxity
   * @param seed random seed
   * @throws IllegalArgumentException in one of the following cases:
   * <ul>
   * <li><tt>minConstrFactor</tt> &gt <tt>maxConstrFactor</tt>
   * <li><tt>minConstrFactor</tt> &lt 1
   * <li><tt>maxConstrFactor</tt> &lt 1
   * </ul>
   */
  public LaxityConstraintFactoryData(
      double minConstrFactor,
      double maxConstrFactor,
      long seed) {
    super(seed);
    
    if(minConstrFactor > maxConstrFactor) throw new IllegalArgumentException("minConstrFactor > maxConstrFactor");
    if(minConstrFactor < 1.0)             throw new IllegalArgumentException("minConstrFactor < 1.0");
    if(maxConstrFactor < 1.0)             throw new IllegalArgumentException("maxConstrFactor < 1.0");
    
    this.minConstrFactor = minConstrFactor;
    this.maxConstrFactor = maxConstrFactor ;
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
    
    root.setAttribute("minConstrFactor", String.valueOf(this.minConstrFactor));
    root.setAttribute("maxConstrFactor", String.valueOf(this.maxConstrFactor));
    
    return root;
  }
}
