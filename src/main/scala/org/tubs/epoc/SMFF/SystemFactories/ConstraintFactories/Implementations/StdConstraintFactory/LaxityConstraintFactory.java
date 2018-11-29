package org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.StdConstraintFactory;

import java.util.LinkedList;
import java.util.Random;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLinkIdentifier;
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.AbstractConstraintFactory;

/**
 * This constraint factory generates end-to-end path latency constraints. Paths are created from
 * every task that has no incoming task link to every reachable task with no outgoing task link.
 * The constraint is set as multiple (paramer laxity) of the sum of WCETs along the path.
 * @author moritzn
 *
 */
public class LaxityConstraintFactory extends AbstractConstraintFactory{
  public static final String IDENTIFIER = "SystemData LaxityConstraintFactory";
  
  private LaxityConstraintFactoryData constraintFactoryData;
  private Random rnd;

  /**
   * Constructs a new instance of laxity constraint factory.
   * @param systemModel system model
   * @param constraintFactoryData constraint factory data to be attached to this constraint factory
   */
  public LaxityConstraintFactory(SystemModel systemModel, LaxityConstraintFactoryData constraintFactoryData){
    super(systemModel);
    this.constraintFactoryData = constraintFactoryData;
    this.recreateRndGens();
  }

	/**
	 * Automatically creates path constraints that can be met through optimization.
	 */
	public  void generateConstraints(){
	  for(ApplicationModel app : systemModel.getApplications()){
	    this.generateConstraints(app);
	  }
  }

	/**
	 * Automatically creates constraints for the specified application.
	 * @param app application for which the constraints will be created
	 */
	public void generateConstraints(ApplicationModel app){
    this.createPaths(app, Integer.MAX_VALUE);
	}

  /**
   * Creates random paths in this application that lead from an arbitrary node
   * with <tt>indegree=0</tt> to an arbitrary node with <tt>outdegree=0</tt> and sets <tt>Integer.MAX_VALUE</tt>
   * as constraint. At most <tt>numPaths</tt> are created.
   * @param app application for which the constraints will be created
   * @param numPaths the maximum number of paths to be creates
   */
  private void createPaths(ApplicationModel app, int numPaths){
    LinkedList<Task> zeroInDegreeList = new LinkedList<Task>();

    // find all tasks with out-degree = 0
    for(Task currentTask : app.getTaskList().values()){
      if(currentTask.getTrgLinkList().size()==0){
        zeroInDegreeList.add(currentTask);
      }
    }

    // select arbitrary node from zeroOutDegreeList and find a connected
    // task with out-degree = 0
    int accWcet = 0; // accumulated wcet along the path
    
    while(zeroInDegreeList.size() > 0 &&
        numPaths > 0){
      Task startTask = zeroInDegreeList.get(rnd.nextInt(zeroInDegreeList.size()));
      Task endTask = startTask;  
      accWcet += endTask.getActiveProfile().getWCET();
      while(endTask.getSrcLinkList().size()!=0){
        LinkedList<TaskLinkIdentifier> srcTaskLinkCollect = new LinkedList<TaskLinkIdentifier>();
        TaskLinkIdentifier targetId;
        srcTaskLinkCollect.addAll(endTask.getSrcLinkList().values());
        targetId = srcTaskLinkCollect.get(rnd.nextInt(srcTaskLinkCollect.size()));
        endTask = systemModel.getTask(systemModel.getTaskLink(targetId).getTrgTask());
        accWcet += endTask.getActiveProfile().getWCET();
      }
      
      // set constraint according to constraint on constraint laxity
      double constrFactorWindow = constraintFactoryData.maxConstrFactor-constraintFactoryData.minConstrFactor;
      double targetConstrFactor = constraintFactoryData.minConstrFactor+rnd.nextDouble()*constrFactorWindow;
      
      app.addSysLatConstr(startTask, endTask, accWcet*targetConstrFactor);
      zeroInDegreeList.remove(startTask);
      numPaths--;
    }
  }

  /**
   * Getter method for the constraint factory data.
   * @return the constraint factory data attached to this constraint factory
   */
  public LaxityConstraintFactoryData getConstraintFactoryData() {
    return constraintFactoryData;
  }

  /**
   * Seeds the random number generator with the seed of constraint factory data.
   */
  public void recreateRndGens() {
    rnd = new Random(constraintFactoryData.getSeed());    
  }
}
