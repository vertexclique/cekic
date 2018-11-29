package org.tubs.epoc.SMFF.SystemFactories.Mappers.Implementations.StdMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLinkIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.SystemFactories.Mappers.AbstractMapper;
import org.tubs.epoc.SMFF.Utility.RandomSelection;

/**
 * This mapper maps task chains only (no forks or joins allowed). The parameters allow to control
 * how tasks attract/repell each other. This allows to control whether applications should be clustered
 * (many tasks on one resource) or distributed. Furthermore it only maps tasks to the same resource if
 * they are each other's predecessor/successor in the application model. Thus, the task chain never
 * "revisits" a resource.
 * 
 * @author moritzn
 * 
 * @see AbstractMapper
 *
 */
public class SensActMapper extends AbstractMapper{
  public static final String IDENTIFIER = "SystemData SensActMapper";
  
  private SensActMapperData mapperData;
  private Random rnd;
  
  /**
   * Constructs a new instance of mapper. 
   * @param systemModel system model
   * @param mapperData mapper data to be attached to this mapper
   */
  public SensActMapper(SystemModel systemModel, SensActMapperData mapperData){
    super(systemModel);
    this.mapperData = mapperData;
    this.mapperData.setMapper(this);
    this.recreateRndGens();
  }

  /**
   * Maps the application on the platform contained in systemModel.
   * <p>
   * Application already has to be in systemModel.
   * @param app application to map (has to be inserted into systemModel first).
   * @throws NullPointerException if either application or system model which is provided to the 
   * constructor is <tt>null</tt>
   */
  public void map(ApplicationModel app) {
    // parameter checks
    if(app == null) throw new NullPointerException("app is null pointer");
    if(systemModel == null) throw new NullPointerException("system model is null pointer. call setSystemModel before calling map");
    
    List<Task> startTasks = new LinkedList<Task>(); // list of all tasks with in-degree=0
    List<Task> endTasks = new LinkedList<Task>(); // list of all tasks with out-degree=0
    List<Task> unmappedTasks = new LinkedList<Task>(app.getTaskList().values());
    List<Task> mappedTasks = new LinkedList<Task>();
    
    // get start and end tasks
    for(Task task : app.getTaskList().values()){
      if(task.getTrgLinkList().size()==0){
        startTasks.add(task);
      }
      if(task.getSrcLinkList().size()==0){
        endTasks.add(task);
      }
    }
    
    // map first start task arbitrarily
    Task task;
    Task firstTask;
    firstTask = startTasks.get(0);
    List<Resource> resources = new LinkedList<Resource>(systemModel.getResourceTable().values());
    Resource firstResource = resources.get(rnd.nextInt(resources.size()));
    app.mapTask(firstTask, firstResource);
    startTasks.remove(firstTask);
    for(TaskLinkIdentifier taskLinkId : firstTask.getSrcLinkList().values()){
      TaskLink taskLink = app.getTaskLink(taskLinkId);
      startTasks.add(app.getTask(taskLink.getTrgTaskId()));
    }
    endTasks.remove(firstTask);
    unmappedTasks.remove(firstTask);
    mappedTasks.add(firstTask);
    if(endTasks.size()>0){
      // map first end task most likely furthest away
      task = endTasks.get(0);    
      // construct the set of all possible resources this task may be mapped on
      {
        LinkedList<Resource> possibleResources = new LinkedList<Resource>(resources);
        int taskDist = app.getDist(task, firstTask);
        Resource mappedRes = (Resource) firstTask.getMappedTo();
        ListIterator<Resource> listIt = possibleResources.listIterator();
        while(listIt.hasNext()){
          Resource possibleResource = listIt.next();
          int resDist = systemModel.getResModel().getDistance(mappedRes, possibleResource);
          if(taskDist < resDist){
            listIt.remove();
          }
        }

        int index;
        if(possibleResources.size()==1){
          index = 0;
        } else{
          // construct probability distribution for controlling picking of mappedTo resource
          int[] mass = new int[possibleResources.size()];
          for(int i=0; i< mass.length; i++){
            mass[i]=10000;
          }

          for(Resource res : possibleResources){
            int dist = systemModel.getResModel().getDistance(firstResource, res);
            for(int j=1; j<=dist; j++)
              mass[possibleResources.indexOf(res)] = Math.max(1, (int)(mass[j]*mapperData.getKResDist()));
          }
          RandomSelection randSelector = new RandomSelection(mass, rnd.nextLong());
          index = randSelector.nextIndex();
        }
        app.mapTask(task, possibleResources.get(index));
        startTasks.remove(task);
        endTasks.remove(task);
        for(TaskLinkIdentifier taskLinkId : task.getTrgLinkList().values()){
          TaskLink taskLink = app.getTaskLink(taskLinkId);
          endTasks.add(app.getTask(taskLink.getSrcTaskId()));
        }
        unmappedTasks.remove(task);
        mappedTasks.add(task);      
      }
    }
    

    boolean nextStartTask = true;
    // map all tasks
    while(unmappedTasks.size()>0){

      if(nextStartTask && startTasks.size()>0){
        task = startTasks.get(0);
      }
      else if(endTasks.size()>0){
        task = endTasks.get(0);
      }
      else{
        throw new IllegalStateException("unmappedTasks > 0 but startTasks and endTasks are empty");
      }
      
      // construct the set of all possible resources this task may be mapped on
      LinkedList<Resource> possibleResources = new LinkedList<Resource>(resources);
      for(Task mappedTask : mappedTasks){
        int taskDist = app.getDist(task, mappedTask);
        Resource mappedRes = (Resource) mappedTask.getMappedTo();
        ListIterator<Resource> listIt = possibleResources.listIterator();
        while(listIt.hasNext()){
          Resource possibleResource = listIt.next();
          int resDist = systemModel.getResModel().getDistance(mappedRes, possibleResource);
          if(taskDist < resDist){
            listIt.remove();
          }
        }
      }

      int index;
      if(possibleResources.size()==1){
        index = 0;
      } else{
        // construct probability distribution for controlling picking of mappedTo resource
        int[] mass = new int[possibleResources.size()];
        for(int i=0; i< mass.length; i++){
          mass[i]=10000;
        }
        
        int i=0;
        for(Resource res : possibleResources){
          List<Task> connectedTasks = new LinkedList<Task>();
          // add mass multiplier for predecessors
          for(TaskLinkIdentifier taskLinkId : task.getTrgLinkList().values()){
            TaskLink taskLink = app.getTaskLink(taskLinkId);
            Task predecessor = app.getTask(taskLink.getSrcTaskId());
            connectedTasks.add(predecessor);
            if(res == predecessor.getMappedTo()){
              mass[i] = Math.max(1, (int)(mass[i]*mapperData.getKPredecessor()));
            }
          }
          // add mass multiplier for successors
          for(TaskLinkIdentifier taskLinkId : task.getSrcLinkList().values()){
            TaskLink taskLink = app.getTaskLink(taskLinkId);
            Task successor = app.getTask(taskLink.getTrgTaskId());
            connectedTasks.add(successor);
            if(res == successor.getMappedTo()){
              mass[i] = Math.max(1, (int)(mass[i]*mapperData.getKSuccessor()));
            }
          }
          // multiply masses for same application (except predecessor or successor)
          boolean connectedPresent = false;
          for(Task connectedTask : connectedTasks){
            if(connectedTask.getMappedTo() == res){
              connectedPresent = true;
            }
          }
          if(!connectedPresent){
            for(Task checkTask : app.getTaskList().values()){
              if(!connectedTasks.contains(checkTask) && 
                  checkTask.getMappedTo() == res){
                mass[i] = Math.max(1, (int)(mass[i]*mapperData.getKSameApp()));
              }
            }
          }
          i++;
        }
        RandomSelection randSelector = new RandomSelection(mass, rnd.nextLong());
        index = randSelector.nextIndex();
      }

      // now map the task on any of the possible resources
      app.mapTask(task, possibleResources.get(index));
      mappedTasks.add(task);
      unmappedTasks.remove(task);
      // and add its predecessors/successors to the next lists (and remove itself from them
      if(nextStartTask){
        for(TaskLinkIdentifier taskLinkId : task.getSrcLinkList().values()){
          TaskLink taskLink = app.getTaskLink(taskLinkId);
          startTasks.add(app.getTask(taskLink.getTrgTaskId()));
        }
      } else {
        for(TaskLinkIdentifier taskLinkId : task.getTrgLinkList().values()){
          TaskLink taskLink = app.getTaskLink(taskLinkId);
          endTasks.add(app.getTask(taskLink.getSrcTaskId()));
        }
      }
      startTasks.remove(task);
      endTasks.remove(task);
           
      // next time take a task from the other end
      nextStartTask = !nextStartTask;
    }

    //map all task links
    for(TaskLink taskLink : app.getTaskLinkList().values()){
      Task srcTask = app.getTask(taskLink.getSrcTaskId());
      Task trgTask = app.getTask(taskLink.getTrgTaskId());
      Resource srcRes = (Resource) srcTask.getMappedTo();
      Resource trgRes = (Resource) trgTask.getMappedTo();

      // if source and target are on the same resource both task links have to be on the same resource
      if(srcRes == trgRes){
        app.mapTaskLink(taskLink, srcRes);
      } else{
        CommResource mappedTo = systemModel.getResModel().getCommResBetween2Resources(srcRes, trgRes, (int) mapperData.getSeed());
        app.mapTaskLink(taskLink, mappedTo);
      }
    }
  }

  /**
   * Returns the mapper parameters.
   * @return mapper data
   */
  public SensActMapperData getMapperData() {
    return mapperData;
  }

  /**
   * Seeds the random number generator.
   */
  public void recreateRndGens() {
    rnd = new Random(mapperData.getSeed());
  }
}
