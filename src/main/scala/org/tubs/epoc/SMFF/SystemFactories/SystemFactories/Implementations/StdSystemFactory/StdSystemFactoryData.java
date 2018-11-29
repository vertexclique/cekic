package org.tubs.epoc.SMFF.SystemFactories.SystemFactories.Implementations.StdSystemFactory;

import org.jdom.Element;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory.StdTgffApplicationFactoryData;
import org.tubs.epoc.SMFF.SystemFactories.Mappers.Implementations.StdMapper.SensActMapperData;
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory.StdPlatformFactoryData;
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.StdPriorityAssigner.StdPriorityAssignerData;
import org.tubs.epoc.SMFF.SystemFactories.SystemFactories.AbstractSystemFactoryData;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.StdTimingFactory.TaskLoadTimingFactoryData;

/**
 * This class includes parameters related with system factory.
 * 
 *  @see AbstractSystemFactoryData
 *
 */
public class StdSystemFactoryData extends AbstractSystemFactoryData{
	// Platform
	private StdPlatformFactoryData platformFactoryData;
	
	// Application
	private int numApps;
	private StdTgffApplicationFactoryData appFactoryData;
	
	// Timing
	private TaskLoadTimingFactoryData timingFactoryData;
	
	// Mapper
	private SensActMapperData mapperData;
	
	// Priority Assigner
	private StdPriorityAssignerData priorityAssignerData;
	
	/**
	 * Creates a complete set of system parameters to generate a system model.
	 * 
	 * @param numRes platform parameter indicating number of computational resources
	 * @param numCommRes platform parameter indicating number of communication resources
	 * @param numOfApplications number of applications
	 * @param cyclicGraph allow cyclic graphs
	 * @param algoVersion version of the algorithm that is used 
	 * @param numTasks tgff parameter indicating number of tasks
	 * @param taskMul tgff parameter indicating  maximum difference to specify number of tasks
	 * @param taskDegrIn tgff parameter indicating aximum in-degree of tasks
	 * @param taskDegrOut tgff parameter indicating that maximum out-degree of tasks
	 * @param mustRejoin if true only task has in-degree=0
	 * @param forkOut if <tt>mustRejoin=false</tt> gives chance of series not rejoining
	 * @param parallelSeries number of series
	 * @param parallelSeriesMult maximum difference to specified number of series
	 * @param seriesLen maximum difference to specified series length
	 * @param seriesLenMult maximum difference to specified series length
	 * @param minActivPeriod timing parameter indicating minimum activation period
	 * @param maxActivPeriod timing parameter indicating maximum activation period
	 * @param minTaskLoad timing parameter indicating minimum load per task
	 * @param maxTaskLoad timing parameter indicating maximum load per task
	 * @param bcetPercentage timing parameter indicating best time execution time
	 * @param seed random seed to be used for random number generation
	 */
	//TODO[mervan]: Unused parameters: numOfApplications, cyclicGraph, algoVersion, mustRejoin, forkOut, parallelSeries, parallelSeriesMult
	//seriesLen, seriesLenMult
	public StdSystemFactoryData(	    
	    // Platform parameters
      int numRes,
      int numCommRes,
      // Application parameters
      int numOfApplications,      
	    int cyclicGraph,
	    // TGFF parameters
      int algoVersion,
      int numTasks,
      int taskMul,
      int taskDegrIn,
      int taskDegrOut,
      int mustRejoin,
	    float forkOut,
      int parallelSeries,
      int parallelSeriesMult,
      int seriesLen,
      int seriesLenMult,
      // timing parameters
      int minActivPeriod,
      int maxActivPeriod,
      int minTaskLoad,
      int maxTaskLoad,
      float bcetPercentage,
      int seed) {
    super(seed);

    platformFactoryData = new StdPlatformFactoryData(
        numRes,
        numCommRes,
        seed);
    appFactoryData = new StdTgffApplicationFactoryData(
        numTasks,
        taskMul,
        taskDegrIn,
        taskDegrOut,
        seed);
    timingFactoryData = new TaskLoadTimingFactoryData(
        minActivPeriod,
        maxActivPeriod,
        minTaskLoad,
        maxTaskLoad,
        bcetPercentage,
        seed);    
    mapperData = new SensActMapperData(
        seed);
    priorityAssignerData = new StdPriorityAssignerData(
        seed);
  }
    
	 /**
   * Writes the factory data to XML.
   * @return the data as XML element.
   */
  @Override
  public Element toXML(){
    Element root = new Element("SystemFactory");

    root.addContent(platformFactoryData.toXML());
    root.addContent(appFactoryData.toXML());
    root.addContent(timingFactoryData.toXML());
    root.addContent(mapperData.toXML());
    root.addContent(priorityAssignerData.toXML());
    
    return root;
  }

  /**
   * Getter method to fetch the platform factory data
   * @return the platform factory data
   */
  public StdPlatformFactoryData getPlatformFactoryData() {
    return platformFactoryData;
  }

  /**
   * Getter method to fetch the application factory data
   * @return the application factory data
   */
  public StdTgffApplicationFactoryData getAppFactoryData() {
    return appFactoryData;
  }

  /**
   * Getter method to fetch the number of applications
   * @return the number of applications
   */
  public int getNumApps() {
    return numApps;
  }

  /**
   * Getter method to fetch the timing parameters.
   * @return the timing parameters
   */
  public TaskLoadTimingFactoryData getTimingFactoryData() {
    return timingFactoryData;
  }

  /**
   * Getter method to fetch the mapper data associated to this system
   * @return the mapper data of this object
   */
  public SensActMapperData getMapperData() {
    return mapperData;
  }

  /**
   * Getter method to fetch the priority assigner data
   * @return the priority assigner data
   */
  public StdPriorityAssignerData getPriorityAssignerData() {
    return priorityAssignerData;
  }
}
