package org.tubs.epoc.SMFF.ModelElements.Platform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;

/**
 * Builds a Resource Graph.
 */
public class ResourceGraph {
  private static Log logger = LogFactory.getLog(ResourceGraph.class);
  // linked system model
  private SystemModel systemModel;
  // associated adjacency matrix with comm resources on rows and resources on columns
  private boolean[][] adjMatrix;
  // maps from columns to resources and rows to comm resources
  private HashMap<Resource, Integer> colMap = new HashMap<Resource, Integer>();
  private HashMap<Integer, Resource> invColMap = new HashMap<Integer, Resource>();
  private HashMap<CommResource, Integer> rowMap = new HashMap<CommResource, Integer>();
  private HashMap<Integer, CommResource> invRowMap = new HashMap<Integer, CommResource>();
  // resource distance table
  private int[][] distMatrix;
  
  /**
   * constructor
   * 
   * @param systemModel system model to add resource graph to
   * @param adjMatrix adjacency matrix of the platform (is just linked and not checked)
   */
  public ResourceGraph(SystemModel systemModel, boolean[][] adjMatrix){
    this.systemModel = systemModel;
    this.adjMatrix = adjMatrix;
  }
  
  /**
   * constructor
   * 
   * @param systemModel system model to extract the adjacency matrix for (automatically extracted)
   */
  public ResourceGraph(SystemModel systemModel){
    int i=0;
    int j=0;
    
    // link the system model
    this.systemModel = systemModel;
    
    adjMatrix = new boolean[systemModel.getCommResourceTable().size()][systemModel.getResourceTable().size()];
    
    // initialize matrix
    for(int k=0; k<adjMatrix.length; k++){
      for(int l=0; l<adjMatrix[k].length; l++){
        adjMatrix[k][l]=false;
      }
    }
    
    // init all maps
    for(Resource res : systemModel.getResourceTable().values()){
      // fill in map
      colMap.put(res, j);
      invColMap.put(j, res);
      j++;
    }
    for(CommResource cRes : systemModel.getCommResourceTable().values()){
      // fill in map
      rowMap.put(cRes, i);
      invRowMap.put(i, cRes);
      
      // fill in values for connectivity
      for(AbstractResource res : cRes.getNeighbors()){
        adjMatrix[rowMap.get(cRes)][colMap.get(res)] = true;
      }
      
      i++;
    }
    
    // recreate distance matrix
    this.recreateDistanceMatrix();
  }
  
  /**
   * Creates a distance matrix of the attached system model.
   */
  private void recreateDistanceMatrix(){
    int numRes = systemModel.getResourceTable().size();
    distMatrix = new int[numRes][numRes];
    
    for(int i=numRes-1; i>=0; i--){
      for(int j=numRes-1; j>=i; j--){
        int resDist = systemModel.getDist(invColMap.get(i), invColMap.get(j));
        distMatrix[i][j]=resDist;
        distMatrix[j][i]=resDist;
      }
    }
  }
  
  /**
   * Getter method for the distance between both resources.
   * @param res1
   * @param res2
   * @return distance between both resources
   */
  public int getDistance(Resource res1, Resource res2){
    return distMatrix[colMap.get(res1)][colMap.get(res2)];
  }
  
  /**
   * Getter method for the adjacency matrix of the resource graph.
   * 
   * @return adjacency matrix of type boolean[][] (rows=comm resources, columns = resources)
   */
  public boolean[][] getAdjMatrix() {
    return adjMatrix;
  }

  /**
   * Prints the adjacency matrix of the resource graph.
   */
  public void printAdjMatrix(){
    StringBuilder sb = new StringBuilder(150);
    for(int i = 0; i < adjMatrix.length; i++){
      for(int j = 0; j < adjMatrix[i].length; j++){
        sb.append(adjMatrix[i][j] + " | ");
      }
      logger.info(sb);
    }
  }
  
  /**
   * Getter method for a list of resource ids within a distance of dist to the specified resource
   * 
   * @param id id of resource which neighborhood is searched
   * @param dist maximal distance
   * @return list of integers which presents the resource-ID's
   */
  public LinkedList<Integer> getLocalityResources(int id, int dist){
    int rounds = 1;
    LinkedList<Integer> resourceIDs = new LinkedList<Integer>();
    
    // do it for distance 1:
    LinkedList<Integer> neighbors = getLocalityResourcesDistanceOne(id);
    
    // add neighbors to resourceIDs
    for(int i = 0; i < neighbors.size(); i++){
      resourceIDs.add(neighbors.get(i));
    }
    
    // if there is a need to have a greater distance than one
    // repeat that for all resourceID's from distance one
    // which are in the resourceIDs list
    while(rounds < dist){
      
      LinkedList<Integer> furtherNeighbors = new LinkedList<Integer>();
      
      for(int k = 0; k < resourceIDs.size(); k++){
        
        LinkedList<Integer> further = getLocalityResourcesDistanceOne(resourceIDs.get(k));
        
        // add new neighbors to furtherNeighbors
        for(int l = 0; l < further.size(); l++){
          if(!furtherNeighbors.contains(further.get(l))){
            furtherNeighbors.add(further.get(l));
          }
        }
      }
      
      // add furtherNeighbors to resourceIDs
      for(int l = 0; l < furtherNeighbors.size(); l++){
        if(!resourceIDs.contains(furtherNeighbors.get(l))){
          resourceIDs.add(furtherNeighbors.get(l));
        }
      }
      
      rounds += 1;
    }
    
    return resourceIDs;
  }
  
  /**
   * Getter method for all neighbors which are directly connected to this
   * 
   * @param id
   * @return list of neighbors Resources
   */
  private LinkedList<Integer> getLocalityResourcesDistanceOne(int id){
    LinkedList<Integer> resourceIDs = new LinkedList<Integer>();
    resourceIDs.add(id);
    
    // get neighbors... run through all columns
    for(int i = 0; i < adjMatrix.length; i++){
      // if the id is "true" in this column, 
      // add all other "trues" to the array
      // so you get all neighbors
      if(adjMatrix[i][id] == true){
        for(int neighbor = 0; neighbor < adjMatrix[i].length; neighbor ++){
          if(adjMatrix[i][neighbor] == true && !resourceIDs.contains(neighbor)){
            resourceIDs.add(neighbor);
          }
        }
      }
    }
    
    return resourceIDs;
  }

  /**
   * Getter method for communication resource between two adjacent resources.
   * 
   * @param res1 first resource
   * @param res2 second resource
   * @param seed seed for random selection in case of multiple possible communication resources
   * @return communication resource between adjacent resources
   */
  public CommResource getCommResBetween2Resources(Resource res1, Resource res2, int seed){
    // list of comm resources that connect the two resources
    LinkedList<CommResource> posComm = new LinkedList<CommResource>();
    // random number generator to select comm resource if multiple exist
    Random rndComm;

    // create random number generator with appropriate seed
    if(seed < 0){
      rndComm = new Random();
    } else{
      rndComm = new Random(seed);
    }

    // collect all comm resources that connect the two resources
    for(int i = 0; i < adjMatrix.length; i++){
      if(adjMatrix[i][colMap.get(res1)] && adjMatrix[i][colMap.get(res2)]){
        posComm.add(invRowMap.get(i));
      }
    }

    // get a comm resource from the list according to rndComm
    int c = rndComm.nextInt(posComm.size());
    logger.info("posComm: " + posComm + " c:" + c);
    return posComm.get(c);
  }

  /**
   * Getter method for the system model that this graph belongs to.
   * @return the system model
   */
  public SystemModel getSystemModel() {
    return systemModel;
  }
}

