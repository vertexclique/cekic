package org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.UUniFast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Profile;
import org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Timing.EventActivation;
import org.tubs.epoc.SMFF.ModelElements.Timing.PJActivation;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.AbstractTimingFactory;

/**
 * This algorithm is based on the UUniFast algorithm presented in
 * Bini, E. & Buttazzo, G. Measuring the Performance of Schedulability Tests Real-Time Syst., 2005, 30, 129-154.
 * <p>
 * Periods are set in a specified window. Execution times are determined from a load requirement. This algorithm
 * guarantees that the load on the resource is as specified in the parameters and that the distribution of task
 * utilizations is uniformely distributed. Activation jitters are set to "1" for compatibility reasons with an external tool.
 * @author moritzn
 *
 */
public class UUniFastTimingFactory extends AbstractTimingFactory{  
  UUniFastTimingFactoryData timingFactoryData;
  private Random rnd;

  // map for storing periods of all applications
  private HashMap<ApplicationModel, Integer> appPeriods = new HashMap<ApplicationModel, Integer>();

  /**
   * Creates an instance of this class.
   * @param systemModel system model
   * @param timingFactoryData timing factory data
   */
  public UUniFastTimingFactory(SystemModel systemModel, UUniFastTimingFactoryData timingFactoryData){
    super(systemModel);
    this.timingFactoryData = timingFactoryData;
    this.recreateRndGens();
  }

  /**
   * Creates new timings for all applications in the system.
   */
  public void generateTimings() {    
    // generate activation patterns for all applications
    for(ApplicationModel app : systemModel.getApplications()){
      this.generateActivationPattern(app);
    }

    // generate task utilizations
    for(AbstractResource res : systemModel.getAllRes()){
      HashMap<SchedulableElement,Double> taskUs = generateTaskUs(res);
      generateTaskWCETs(taskUs);
    }
  }

  /**
   * Generates activation period and jitter for all applications.
   * @param application model
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
   * Generates task utilizations for a given resource
   * this is essentially the UUniFast algorithm by Bini.
   * @param res abstract resource
   * @return the utilization values of schedulable elements as a hash map
   */
  private HashMap<SchedulableElement,Double> generateTaskUs(AbstractResource res){
    List<SchedulableElement> schedElemList = new ArrayList<SchedulableElement>();
    HashMap<SchedulableElement,Double> utilizations = new HashMap<SchedulableElement,Double>();
    Double overallUtil = timingFactoryData.minResU + rnd.nextDouble()*(timingFactoryData.maxResU-timingFactoryData.minResU);

    // assemble list of all schedulable elements (iterating because of local task links)
    for(SchedulableElement schedElem : res.getMappedSchedElems()){
      if((schedElem instanceof Task     && res instanceof Resource) ||
          (schedElem instanceof TaskLink && res instanceof CommResource)){
        schedElemList.add(schedElem);
      } else{
        // if this is a task mapped to a resource add a pseudo-profile
        schedElem.addProfile(new Profile(0, 0, new EventActivation()));
      }
    }

    int n = schedElemList.size();
    if(n>0){
      // algorithm by Bini
      Double sumU = overallUtil;
      for(int i=1; i<=n-1; i++){
        Double nextSumU = sumU*Math.pow(rnd.nextDouble(),(1.0/(double)(n-i)));
        utilizations.put(schedElemList.get(i-1), sumU-nextSumU);
        sumU = nextSumU;
      }
      utilizations.put(schedElemList.get(n-1), sumU);
    }

    return utilizations;
  }

  /**
   * Generates worst case execution times based on periods and utilization vector.
   * @param utilizations hashmap of utilization values of schedulable elements (mapped by schedulable elements as key)
   */
  private void generateTaskWCETs(HashMap<SchedulableElement,Double> utilizations){
    for(SchedulableElement schedElem : utilizations.keySet()){
      // ensure that each task/tasklink has a profile
      if(schedElem.getProfileList().isEmpty()){
        Profile emptyProfile = new Profile(0, Integer.MAX_VALUE, null);
        schedElem.addProfile(emptyProfile);
        schedElem.setActiveProfile(emptyProfile);
        emptyProfile.setActivationPattern(new EventActivation());
      }
      
      ApplicationModel app = schedElem.getApplication();
      Integer period = appPeriods.get(app);
      Integer wcet = (int) Math.round((double) period*utilizations.get(schedElem));
      wcet = Math.max(wcet, 1);
      Integer bcet = (int) ((double) wcet*timingFactoryData.bcetPercentage);
      bcet = Math.max(bcet, 1);
      schedElem.getActiveProfile().setWCET(wcet);
      schedElem.getActiveProfile().setBCET(bcet);
    }
  }

  /**
   * Getter method to fetch the timing factory data attached to this object.
   * 
   * @return the timing factory data
   */
  public UUniFastTimingFactoryData getTimingFactoryData() {
    return timingFactoryData;
  }

  /**
   * Recreate random number generators with a specific value set to the timing factory data. 
   */
  public void recreateRndGens() {
    rnd = new Random(timingFactoryData.getSeed());
  }
}
