package org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.StdTimingFactory;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.AbstractTimingFactoryData;

/**
 * Timing factory data includes timing related parameters and works with timing factory.
 * 
 *  @see AbstractTimingFactoryData
 * @see TaskLoadTimingFactory
 */
public class TaskLoadTimingFactoryData extends AbstractTimingFactoryData{
  // Parameters
	int minActPeriod;       // minimum activation period
	int maxActPeriod;       // maximum activation period
	double minTaskLoad;         // minimum load per task
	double maxTaskLoad;         // maximum load per task
	double bcetPercentage;      // bcet as percentage of wcet (has to be <=1.0

  /**
   * Constructor for task load timing factory data.
   * @param minActPeriod minimum activation period
   * @param maxActPeriod maximum activation period
   * @param bcetPercentage best case execution time percentage
   * @param seed random seed
   */
  public TaskLoadTimingFactoryData(
      int minActPeriod,
      int maxActPeriod,
      double minTaskLoad,
      double maxTaskLoad,
      double bcetPercentage,
      long seed) {    
    super(seed); 

    if(minTaskLoad > maxTaskLoad) throw new IllegalArgumentException("minTaskLoad is greater than maxTaskLoad");
    if(minActPeriod > maxActPeriod) throw new IllegalArgumentException("minActPeriod is greater than maxActPeriod");

    this.minActPeriod = minActPeriod;
    this.maxActPeriod = maxActPeriod;
    this.minTaskLoad = minTaskLoad;
    this.maxTaskLoad = maxTaskLoad;
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
    
    root.setAttribute("minActPeriod", String.valueOf(this.minActPeriod));
    root.setAttribute("maxActPeriod", String.valueOf(this.maxActPeriod));
    root.setAttribute("minTaskLoad", String.valueOf(this.minTaskLoad));
    root.setAttribute("minTaskLoad", String.valueOf(this.minTaskLoad));
    root.setAttribute("minTaskLoad", String.valueOf(this.minTaskLoad));
    
    return root;
  }
}
