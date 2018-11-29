package org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.StdTimingFactory;

import java.util.HashMap;
import java.util.Random;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Profile;
import org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Timing.EventActivation;
import org.tubs.epoc.SMFF.ModelElements.Timing.PJActivation;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.AbstractTimingFactory;

/**
 * This timing factory scales all worst case execution times in the system so that the corresponding resource's load
 * is within a specified window. OutputJitters will be set to zero.
 * @author moritzn
 *
 */
public class TaskLoadTimingFactory extends AbstractTimingFactory{  
  private TaskLoadTimingFactoryData timingFactoryData;
  private Random rnd;

  // map for storing periods of all applications
  private HashMap<ApplicationModel, Integer> appPeriods = new HashMap<ApplicationModel, Integer>();

  public TaskLoadTimingFactory(SystemModel systemModel, TaskLoadTimingFactoryData timingFactoryData){
    super(systemModel);
    this.timingFactoryData = timingFactoryData;
    this.recreateRndGens();
  }

  /**
   * creates new timings for all applications in the system
   */
  public void generateTimings() {    
    // scale all tasks and task links of all applications
    for(ApplicationModel app : systemModel.getApplications()){
      this.generateActivationPattern(app);
      this.generateTimings(app);
    }
  }



  /**
   * Generates activation period and jitter for all applications.
   */
  private void generateActivationPattern(ApplicationModel app){
    // generate activation period for this application
    Integer activationPeriod = timingFactoryData.minActPeriod + rnd.nextInt(timingFactoryData.maxActPeriod - timingFactoryData.minActPeriod);
    appPeriods.put(app, activationPeriod);

    for(Task task : app.getTaskList().values()){
      // add a profile for every task
      if(task.getProfileList().isEmpty()){
        Profile emptyProfile = new Profile(0, Integer.MAX_VALUE, null);
        task.addProfile(emptyProfile);
        task.setActiveProfile(emptyProfile);
        // set activation period and activation jitter for all tasks that have no incoming task links
        if(task.getTrgLinkList().size()==0){
          emptyProfile.setActivationPattern(new PJActivation(activationPeriod, 1));
        } else{
          emptyProfile.setActivationPattern(new EventActivation());
        }
      }
    }
  }

  /**
   * Creates new timings for specified application.
   * @param app application model
   */
  public void generateTimings(ApplicationModel app){
    for(Task task : app.getTaskList().values()){   
      // get next load
      double loadWindow = timingFactoryData.maxTaskLoad-timingFactoryData.minTaskLoad;
      double targetLoad = timingFactoryData.minTaskLoad+rnd.nextDouble()*loadWindow;

      // assign wcet and bcet
      Profile profile = task.getActiveProfile();
      double wcet = Math.max(1.0,(double)profile.getWCET()*targetLoad/getLoad(task));
      double bcet = Math.max(1.0,wcet*timingFactoryData.bcetPercentage);
      profile.setWCET((int)wcet);
      profile.setBCET((int)bcet);
    }
    for(TaskLink taskLink : app.getTaskLinkList().values()){
      // get next load
      double loadWindow = timingFactoryData.maxTaskLoad-timingFactoryData.minTaskLoad;
      double targetLoad = timingFactoryData.minTaskLoad+rnd.nextDouble()*loadWindow;

      // ensure that each task link has a profile
      if(taskLink.getProfileList().isEmpty()){
        Profile emptyProfile = new Profile(0, Integer.MAX_VALUE, null);
        taskLink.addProfile(emptyProfile);
        taskLink.setActiveProfile(emptyProfile);
        emptyProfile.setActivationPattern(new EventActivation());
      }
      
      // assign wcet and bcet
      double wcet = Math.max(1.0,taskLink.getWCET()*targetLoad/getLoad(taskLink));
      double bcet = Math.max(1.0,wcet*timingFactoryData.bcetPercentage);
      taskLink.getActiveProfile().setWCET((int)wcet);
      taskLink.getActiveProfile().setBCET((int)bcet);
    }
  }

  /**
   * Queries the load which this schedulable element is causing.
   * <p>
   * Load is defined as <tt>wcet/period</tt>, where  wcet: worst case execution time,
   * and bcet: best case execution time.
   * @param schedElem schedulable element
   * @return load of this schedulable element
   */
  private double getLoad(SchedulableElement schedElem){
    int actPeriod = Integer.MAX_VALUE;

    ApplicationModel app = systemModel.getApplication(schedElem.getAppId());
    // get activation period for this application (assuming AND activation and equal periods)
    for(Task task : app.getTaskList().values()){
      if(task.getTrgLinkList().size()==0){
        actPeriod = task.getActiveProfile().getActivationPeriod();
        break;
      }
    }

    // get load of this task
    if(schedElem instanceof Task){
      Task task = (Task) schedElem;
      if(task.getActiveProfile().getWCET()==0){
        task.getActiveProfile().setWCET(Integer.MAX_VALUE);
      }      
      return (double)task.getActiveProfile().getWCET()/(double)actPeriod;
    } else if(schedElem instanceof TaskLink){
      TaskLink taskLink = (TaskLink) schedElem;
      if(taskLink.getWCET()==0){
        taskLink.setWCET(Integer.MAX_VALUE);
      }      
      return (double)taskLink.getWCET()/(double)actPeriod;
    } else{
      return Double.MAX_VALUE;
    }
  }

  /**
   * Getter method to fetch the timing factory data attached to this timing factory.
   * @return the timing factory data
   */
  public TaskLoadTimingFactoryData getTimingFactoryData() {
    return timingFactoryData;
  }

  /**
   * Recreate the random number generators associated with a specific seed.
   */
  public void recreateRndGens() {
    rnd = new Random(timingFactoryData.getSeed());
  }
}
