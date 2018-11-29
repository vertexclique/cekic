package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory;

import org.jdom2.Element;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.AbstractApplicationFactoryData;

/**
 * Implementation of the application factory data to be used for tgff.
 *
 */
public class StdTgffApplicationFactoryData extends AbstractApplicationFactoryData{  
  // TGFF algorithm version to use - false->old, true->new
  private boolean newAlgorithm;
  
  // allow cyclic graphs
  private boolean cyclicGraph;
    
  // TGFF options
  private int numTasks;           // number of tasks
  private int diffNumTasks;       // maximum difference to specified number of tasks
  private int taskMaxDegrIn;      // maximum in-degree of tasks
  private int taskMaxDegrOut;     // maximum out-degree of tasks
  
  // parameters for new algorithm version
  private boolean mustRejoin;     // if true only task has in-degree=0 
  private float forkOut;          // if mustRejoin=false gives chance of series not rejoining
  private int seriesLen;          // length if series
  private int diffSeriesLen;      // maximum difference to specified series length
  private int parallelSeries;     // number of series
  private int diffParallelSeries; // maximum difference to specified number of series
  
  /**
   * Constructor to use the new version of TGFF.
   * @param cyclicGraph flag to be used for allowing cyclic graphs 
   * @param numTasks number of tasks
   * @param diffNumTasks maximum difference to specify number of tasks
   * @param taskMaxDegrIn maximum in-degree of tasks
   * @param taskMaxDegrOut maximum out-degree of tasks
   * @param seriesLen length of series
   * @param diffSeriesLen maximum difference to specify series length
   * @param parallelSeries number of series
   * @param diffParallelSeries maximum difference to specify number of series
   * @param forkOut if <tt>mustRejoin=false</tt> gives chance of series not rejoining
   * @param mustRejoin if <tt>true</tt> only task has in-degree=0 
   * @param seed for the number generator
   */
  public StdTgffApplicationFactoryData(boolean cyclicGraph, int numTasks,
      int diffNumTasks, int taskMaxDegrIn, int taskMaxDegrOut, int seriesLen,
      int diffSeriesLen, int parallelSeries, int diffParallelSeries,
      float forkOut, boolean mustRejoin, long seed) {
    super(seed);
    
    this.newAlgorithm = true;
    this.cyclicGraph = cyclicGraph;
    this.numTasks = numTasks;
    this.diffNumTasks = diffNumTasks;
    this.taskMaxDegrIn = taskMaxDegrIn;
    this.taskMaxDegrOut = taskMaxDegrOut;
    this.seriesLen = seriesLen;
    this.diffSeriesLen = diffSeriesLen;
    this.parallelSeries = parallelSeries;
    this.diffParallelSeries = diffParallelSeries;
    this.forkOut = forkOut;
    this.mustRejoin = mustRejoin;
  }

  /**
   * Overloaded constructor to use the old version of TGFF.
   * @param numTasks number of tasks
   * @param diffNumTasks maximum difference to specify number of tasks
   * @param taskMaxDegrIn maximum in-degree of tasks
   * @param taskMaxDegrOut maximum out-degree of tasks
   * @param seed for the number generator
   */
  public StdTgffApplicationFactoryData(
      int numTasks,
      int diffNumTasks,
      int taskMaxDegrIn,
      int taskMaxDegrOut,
      long seed){
    super(seed);
    
    this.newAlgorithm = false;
    this.cyclicGraph = false;
    this.numTasks = numTasks;
    this.diffNumTasks = diffNumTasks;
    this.taskMaxDegrIn = taskMaxDegrIn;
    this.taskMaxDegrOut = taskMaxDegrOut;
    this.seriesLen = 1;
    this.diffSeriesLen = 1;
    this.parallelSeries = 1;
    this.diffParallelSeries = 1;
    this.forkOut = 1.0f;
    this.mustRejoin = false;
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
    
    // set old tgff parameters
    root.setAttribute("numTasks", String.valueOf(this.numTasks));
    root.setAttribute("diffNumTasks", String.valueOf(this.diffNumTasks));
    root.setAttribute("taskMaxDegrIn", String.valueOf(this.taskMaxDegrIn));
    root.setAttribute("taskMaxDegrOut", String.valueOf(this.taskMaxDegrOut));
    
    // set custom parameter
    root.setAttribute("cyclicGraph", String.valueOf(this.cyclicGraph));

    // for new algorithm
    if(newAlgorithm){
      root.setAttribute("mustRejoin", String.valueOf(this.mustRejoin));
      root.setAttribute("forkOut", String.valueOf(this.forkOut));
      root.setAttribute("seriesLen", String.valueOf(this.seriesLen));
      root.setAttribute("diffSeriesLen", String.valueOf(this.diffSeriesLen));
      root.setAttribute("parallelSeries", String.valueOf(this.parallelSeries));
      root.setAttribute("diffParallelSeries", String.valueOf(this.diffParallelSeries));      
    }
    return root;
  }

  /**
   * TGFF algorithm version to use.
   * <p>
   * if <tt>false</tt> then old algorithm will be used, otherwise the new algorithm will be used.
   * The utilization of the old or new algorithm depends upon the constructor that is called. 
   * @return <tt>true</tt> for the new algorithm, <tt>false</tt> otherwise
   */
  public boolean isNewAlgorithm() {
    return newAlgorithm;
  }

  /**
   * Getter method for allowance of cyclic graphs.
   * @return <tt>true</tt> if cyclic graphs are allowed, <tt>false</tt> otherwise.
   */
  public boolean isCyclicGraph() {
    return cyclicGraph;
  }

  /**
   * Getter method for number of tasks for this tgff application factory data.
   * @return the number of tasks
   */
  public int getNumTasks() {
    return numTasks;
  }

  /**
   * Getter method for maximum difference to specify number of tasks.
   * @return the  maximum difference to specify number of tasks
   */
  public int getDiffNumTasks() {
    return diffNumTasks;
  }

  /**
   * Getter method for maximum in-degree of tasks.
   * @return the maximum in-degree of tasks
   */
  public int getTaskMaxDegrIn() {
    return taskMaxDegrIn;
  }

  /**
   * Getter method for maximum out-degree of tasks.
   * @return the maximum out-degree of tasks
   */
  public int getTaskMaxDegrOut() {
    return taskMaxDegrOut;
  }

  /**
   * Queries the must rejoin option of this application factory data.
   * <p>
   * if true only task has in-degree=0.
   * @return <tt>true</tt> if only task has in-degree=0, <tt>false</tt> otherwise. 
   */
  public boolean isMustRejoin() {
    return mustRejoin;
  }

  /**
   * Getter method for fork out value.
   * <p>
   * if mustRejoin=false gives chance of series not rejoining.
   * @return the value of fork out for this instance of application factory data.
   */
  public float getForkOut() {
    return forkOut;
  }

  /**
   * Getter method for length of series.
   * @return the  length of series
   */
  public int getSeriesLen() {
    return seriesLen;
  }

  /**
   * Getter method for maximum difference to specify series length.
   * @return the  maximum difference to specify series length
   */
  public int getDiffSeriesLen() {
    return diffSeriesLen;
  }

  /**
   * Getter method for number of series.
   * @return the  number of series
   */
  public int getParallelSeries() {
    return parallelSeries;
  }

  /**
   * Getter method for maximum difference to specify number of series.
   * @return the maximum difference to specify number of series
   */
  public int getDiffParallelSeries() {
    return diffParallelSeries;
  }
}
