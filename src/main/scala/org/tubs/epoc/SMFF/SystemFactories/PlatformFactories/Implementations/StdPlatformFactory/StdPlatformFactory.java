package org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory;

import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Platform.ResourceGraph;
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.AbstractPlatformFactory;

/**
 * This is the standard platform factory. It allows to specify the number of processors and the number of
 * communication media as percentage of the number of processors. This allows to control the degree of
 * "connectivity" of the platform.
 * @author moritzn
 *
 */
public class StdPlatformFactory extends AbstractPlatformFactory{
	private static Log logger = LogFactory.getLog(StdPlatformFactory.class);
	/**
	 * Identifier of the platform factory.
	 */
  public static final String IDENTIFIER = "SystemData StdPlatformFactory";
  
  private StdPlatformFactoryData platformFactoryData;
  private Random rnd;
  
  /**
   * Constructs an instance of standard platform factory
   * @param systemModel system model
   * @param platformFactoryData platform data factory to be attached to this platform factory
   */
  public StdPlatformFactory(SystemModel systemModel, StdPlatformFactoryData platformFactoryData){
    super(systemModel);
    this.platformFactoryData = platformFactoryData;
    this.platformFactoryData.setPlatformFactory(this);
    this.recreateRndGens();
  }

  /**
   * Generates resources in systemModel according to systemSpecification in the factory. 
   */
  public void generatePlatform(){
    //TODO: reset platform first
    //this.resetPlatform();
    // generates the adjacency matrix and inserts it into the system model
    createModel();
    // generates the resources from the adjacency matrix
    buildResources();
  }



  /**
   * This method creates the resource model.
   * <p>
   * It creates resources and commresources and connect them to a graph.
   * There a some conditions to the graph:
   * <ul>
   * <li> one connected component
   * <li> Resource not adjunct to Resource
   * <li> CommResource not adjunct to CommResources
   * <li> 1 CommResource adjunct to >=2 resources
   * <li> 1 Resource adjunct to 1..n CommResources
   * </ul>
   */
  private void createModel() {
    // adjacency matrix to generate
    boolean[][] adjMatrix;    
    // resource graph to link into system model
    ResourceGraph resGraph;
    // convenience variables
    int numResources     = platformFactoryData.getNumRes();
    int numCommResources = platformFactoryData.getNumCommRes();    

    // create a 2dim array:
    // rows are CommResources
    // columns are Resources
    adjMatrix = null;
    adjMatrix = new boolean[numCommResources][numResources];

    // initialize: all false
    for(int i = 0; i < adjMatrix.length; i++){
      for(int j = 0; j < adjMatrix[i].length; j++){
        adjMatrix[i][j] = false;
      }
    }

    logger.info("start:");
    // first part: 
    // connect two CommResources with one Resource
    for(int i = 0; i < adjMatrix.length-1; i++){
      int usedRes = rnd.nextInt(numResources);
      adjMatrix[i][usedRes] = true;
      adjMatrix[i+1][usedRes] = true;
      logger.info("iteration " + i + ":");
      printAdjacencyMatrix(adjMatrix);
    }


    logger.info("first:");
    printAdjacencyMatrix(adjMatrix);
    // second part:
    // run through all rows and test if 2x true, if not choose a
    // random position and set it up to true
    for(int i = 0; i < numCommResources; i++){
      int numOfTrue = 0;
      // test
      for(int j = 0; j < numResources; j++){
        if(adjMatrix[i][j] == true){
          numOfTrue+= 1;
        }
      }
      if(numOfTrue < 2){
        boolean test = true;
        int k = -1;

        while(test == true){
          k = rnd.nextInt(numResources-1)+1;	
          test = adjMatrix[i][k];
        }

        adjMatrix[i][k] = true;
      }
    }

    logger.info("second:");
    printAdjacencyMatrix(adjMatrix);

    // third part:
    // run through all columns and if there only false,
    // connect that Resource by setting a random edge
    for(int i = 0; i <numResources; i++){
      boolean test = false;
      for(int j = 0; j < adjMatrix.length; j++){
        if(adjMatrix[j][i] == true){
          test = true;
          break;
        }
      }
      if(test == false){
        if(numCommResources == 1){
          int connectTo = 0;
          adjMatrix[connectTo][i] = true;
        }
        else{
          int connectTo = rnd.nextInt(numCommResources-1)+1;
          adjMatrix[connectTo][i] = true;
        }

      }

    }

    logger.info("third:");
    printAdjacencyMatrix(adjMatrix);

    // fourth part
    // to increase variety search for communicationResources with 
    // >= 3 Resources and delete a random resource, on condition of
    // the resource is connected to more than this commres
    // only if res - commRes = 1
    if((numResources-numCommResources) == 1){
      // run though all comm Res
      for(int i = 0; i < numCommResources; i++){
        // run through all Resources
        int res = 0;
        LinkedList<Integer> l = new LinkedList<Integer>();
        for(int j = 0; j < numResources; j++){
          if(adjMatrix[i][j] == true){
            res++;
            l.add(j);
          }
        }
        // if res > 3

        if(res >= 3){
          int del = rnd.nextInt(l.size());

          // if Resource connected to >= 2 commRes delete this link
          for(int r = 0; r < l.size(); r++){
            int check = 0;
            for(int j = 0; j < numCommResources; j++){
              if(adjMatrix[j][del] == true){
                check++;
              }
            }
            if(check>=2){
              adjMatrix[i][del] = false;
            }
          }
        }

      }
    }
    logger.info("fourth:");
    printAdjacencyMatrix(adjMatrix);

    resGraph = new ResourceGraph(systemModel, adjMatrix);

    // save resModel in SystemModel
    systemModel.setResModel(resGraph);
  }


  /**
   * Create the Resources and CommResources and
   * connect them by using the resModel array.
   */
  private void buildResources() {
    // convenience variables
    int numResources     = platformFactoryData.getNumRes();
    int numCommResources = platformFactoryData.getNumCommRes();
    boolean[][] adjMatrix = systemModel.getResModel().getAdjMatrix();

    // create resources
    for( int i=0; i<numResources; i++){
      Resource res = new Resource(systemModel,i);

      // add to functional block
      systemModel.addResource(res);
    }

    // create CommResources
    for(int j=0; j<numCommResources;j++){
      String name = "CommRes" + j; 
      CommResource commRes = new CommResource(systemModel,name,j);

      // add to functional block
      systemModel.addResource(commRes);
    }

    // connect them
    for(int i = 0; i < adjMatrix.length; i++){
      for(int j = 0; j < adjMatrix[i].length; j++){
        if(adjMatrix[i][j] == true){
          Resource resource = systemModel.getResource(j);
          systemModel.getCommResource(i).addLink(resource);
        }
      }
    }
    
    // recreate the adjacency matrix in the system model to update the maps
    systemModel.recreateResGraph();
  }

  // test method - prints the ResourceModel to get an idea how to 
  // map the tasks in a good way
  private void printAdjacencyMatrix(boolean[][] adjMatrix){
  	
    for(int i = 0; i < adjMatrix.length; i++){
    	StringBuilder sb = new StringBuilder(150);
      for(int j = 0; j < adjMatrix[i].length; j++){
        sb.append(adjMatrix[i][j] + " | ");
      }
      logger.info(sb);
    }
  }

  /**
   * Returns platform factory data for this factory.
   * @return platform factory data for this factory
   */
  public StdPlatformFactoryData getPlatformFactoryData() {
    return platformFactoryData;
  }

  /**
   * Seeds the random number generator.
   */
  public void recreateRndGens() {
    rnd = new Random(platformFactoryData.getSeed());
  }
}
