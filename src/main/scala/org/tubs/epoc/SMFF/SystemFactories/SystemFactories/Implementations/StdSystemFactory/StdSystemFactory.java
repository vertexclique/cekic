package org.tubs.epoc.SMFF.SystemFactories.SystemFactories.Implementations.StdSystemFactory;

import java.util.Random;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory.StdTgffApplicationFactory;
import org.tubs.epoc.SMFF.SystemFactories.Mappers.Implementations.StdMapper.SensActMapper;
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory.StdPlatformFactory;
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.StdPriorityAssigner.StdPriorityAssigner;
import org.tubs.epoc.SMFF.SystemFactories.SystemFactories.AbstractSystemFactory;
import org.tubs.epoc.SMFF.SystemFactories.SystemFactories.AbstractSystemFactoryData;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.StdTimingFactory.TaskLoadTimingFactory;

/**
 * This system Generator returns a system based on the algorithms from chrisl's bachelor thesis.
 * The model does not include either constraints nor proper timing information.
 * @author moritzn
 *
 */
public class StdSystemFactory extends AbstractSystemFactory{
	private StdSystemFactoryData systemFactoryData;
	private Random rnd;

  /**
   * Creates a new instance of this class.
   * @param systemFactoryData system factory data to be attached to this system factory
   */
  public StdSystemFactory(StdSystemFactoryData systemFactoryData){
    this.systemFactoryData = systemFactoryData;
    this.recreateRndGens();
  }
  
  /**
   * Generates the system model and returns  it.
   * 
   * @return the system model.
   */
  public SystemModel generateSystem() {
    new StdPlatformFactory(systemModel, systemFactoryData.getPlatformFactoryData());
    new StdTgffApplicationFactory(systemModel, systemFactoryData.getAppFactoryData());
    new SensActMapper(systemModel, systemFactoryData.getMapperData());
    new StdPriorityAssigner(systemModel, systemFactoryData.getPriorityAssignerData());
    new TaskLoadTimingFactory(systemModel, systemFactoryData.getTimingFactoryData());
    
    ((StdPlatformFactory)systemModel.getSingleExtDataByClass(StdPlatformFactory.class)).generatePlatform();
    for(int i=0; i<systemFactoryData.getNumApps(); i++){
      ApplicationModel app = ((StdTgffApplicationFactory)systemModel.getSingleExtDataByClass(StdTgffApplicationFactory.class)).generateApplication();
      ((SensActMapper)systemModel.getSingleExtDataByClass(SensActMapper.class)).map(app);
    }
    ((TaskLoadTimingFactory)systemModel.getSingleExtDataByClass(TaskLoadTimingFactory.class)).generateTimings();
    ((StdPriorityAssigner)systemModel.getSingleExtDataByClass(StdPriorityAssigner.class)).assignPriorities();
    
    return systemModel;
  }

  /**
   * Getter method to fetch the system factory data attached to this system factory.
   */
  public AbstractSystemFactoryData getSystemFactoryData() {
    return systemFactoryData;
  }

  /**
   * Feeds the seed for random number generation.
   */
  public void recreateRndGens() {
    rnd = new Random(systemFactoryData.getSeed());
  }

}
