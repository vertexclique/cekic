package org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.UUniFast;

import org.jdom.Element;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.AbstractTimingFactoryData;

/**
 * Timing factory data works with timing factory. 
 * <p>
 * This specific class is a helper class for timing factory class which uses UUniFast algorithm.
 * 
 * @see AbstractTimingFactoryData
 * @see UUniFastTimingFactory
 *
 */
public class UUniFastTimingFactoryData extends AbstractTimingFactoryData{
  // Parameters
  double minResU;         // minimum resource utilization
  double maxResU;         // maximum resource utilization
  int minActPeriod;       // minimum activation period
  int maxActPeriod;       // maximum activation period
  double bcetPercentage;  // bcet as percentage of wcet (has to be <=1.0

  /**
   * Constructor for random priority assigner.
   * @param minResU minimum resource utilization
   * @param maxResU maximum resource utilization
   * @param minActPeriod minimum activation period
   * @param maxActPeriod maximum activation period
   * @param seed random seed
   */
  public UUniFastTimingFactoryData(
      double minResU,
      double maxResU,
      int minActPeriod,
      int maxActPeriod,
      double bcetPercentage,
      long seed) {    
    super(seed); 

    if(minResU > maxResU) throw new IllegalArgumentException("minResU is greater than maxResU");
    if(minActPeriod > maxActPeriod) throw new IllegalArgumentException("minActPeriod is greater than maxActPeriod");
    
    this.minResU = minResU;
    this.maxResU = maxResU;
    this.minActPeriod = minActPeriod;
    this.maxActPeriod = maxActPeriod;
    this.bcetPercentage = bcetPercentage;
  }
    
  /**
   * Writes the factory data to XML.
   * @return the data as XML element.
   */
  @Override
  public Element toXML(){
    Element root = new Element("TimingFactory");
    
    root.setAttribute("classname", this.getClass().getName());
    root.setAttribute("seed", String.valueOf(this.seed));
    
    root.setAttribute("minResU", String.valueOf(this.minResU));
    root.setAttribute("maxResU", String.valueOf(this.maxResU));
    root.setAttribute("minActPeriod", String.valueOf(this.minActPeriod));
    root.setAttribute("maxActPeriod", String.valueOf(this.maxActPeriod));
    root.setAttribute("bcetPercentage", String.valueOf(this.bcetPercentage));
    
    return root;
  }

  public final double getMinResU() {
    return minResU;
  }

  public final double getMaxResU() {
    return maxResU;
  }

  public final int getMinActPeriod() {
    return minActPeriod;
  }

  public final int getMaxActPeriod() {
    return maxActPeriod;
  }

  public final double getBcetPercentage() {
    return bcetPercentage;
  }
}
