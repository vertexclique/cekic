package org.tubs.epoc.SMFF.SystemFactories.Example;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelLoader;
import org.tubs.epoc.SMFF.ImportExport.XML.ModelSaver;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.TaskChainApplicationFactory.TaskChainApplicationFactory;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.TaskChainApplicationFactory.TaskChainApplicationFactoryData;
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.StdConstraintFactory.LaxityConstraintFactory;
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.StdConstraintFactory.LaxityConstraintFactoryData;
import org.tubs.epoc.SMFF.SystemFactories.Mappers.Implementations.StdMapper.SensActMapper;
import org.tubs.epoc.SMFF.SystemFactories.Mappers.Implementations.StdMapper.SensActMapperData;
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory.StdPlatformFactory;
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory.StdPlatformFactoryData;
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.TaskChainPriorityAssigner.TaskChainPriorityAssigner;
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.TaskChainPriorityAssigner.TaskChainPriorityAssignerData;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.UUniFast.UUniFastTimingFactory;
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.UUniFast.UUniFastTimingFactoryData;

/***
 * This Example demonstrates how to create pseudorandom testcases with SMFF
 * In the first section of the code the parameter sets for the single generation steps are defined. This defines
 * the general structure and properties of the testcases that are being generated.
 * With these parameters the actual factories are being generated. The actual testcase generation follows.
 * In the last code section the testcase is written to an XML file.
 * 
 * @author moritzn
 *
 */
public class Example {
  public static Logger  logger = Logger.getLogger("org.tubs.epoc.SMFF");

  public static void main(String[] args) {
    logger.setLevel(Level.WARN);
    BasicConfigurator.configure();

    // create a new and empty system model
    SystemModel systemModel = new SystemModel();

    // parameter sets for the different factories
    StdPlatformFactoryData          platformFactoryData;
    TaskChainApplicationFactoryData appFactoryData;
    SensActMapperData               mapperData;
    UUniFastTimingFactoryData       timingFactoryData;
    LaxityConstraintFactoryData     constrFactoryData;
    TaskChainPriorityAssignerData   prioAssignerData;

    // specify the path to which the testcases are to be stored
    //    String outputPath = "C://Testcases//";
    String outputPath = "/user/Mit/E_12/moritzn/Testcases/";
    new File(outputPath).mkdirs();

    //--------------------------------------
    // PARAMETRIZATION OF GENERATION PROCESS
    //--------------------------------------
    // PLATFORM FACTORY
    // this parameter set will create platforms consisting of 5 processors and 2 communication media
    platformFactoryData = new StdPlatformFactoryData(
        5, //numRes
        2, //numCRes
        0  //seed
    );

    // APPLICATION FACTORY
    // this parameter set will create chains of tasks consisting of 6 tasks
    appFactoryData = new TaskChainApplicationFactoryData(
        6, //taskCnt
        0  //seed
    );

    // MAPPER
    // this parameter set will map task chains such that only tasks that are adjacent in the task graph are mapped to the same processor
    // (uses standard parameters and works only for task chains) 
    mapperData = new SensActMapperData(
        0  //seed
    );

    // PRIORITY ASSIGNER
    // this parameter set assigns priorities randomly but ensures that tasks in the front of a task chain receives higher priorities
    // (works only for task chains)
    prioAssignerData = new TaskChainPriorityAssignerData(
        0  //seed
    );

    // TIMING FACTORY
    // this parameter assigns timing properties according to the UUniFast algorithm. Activation Periods are uniformly distributed between
    // minActPeriod and maxActPeriod. BCET is set as percentage of WCET
    timingFactoryData = new UUniFastTimingFactoryData(
        0.45,  //minResU
        0.55,  //maxResU
        300,   //minActPeriod
        1000,  //maxActPeriod
        1.0,   //bcetPercentage
        0      //seed
    );

    // CONSTRAINT FACTORY
    // creates path latency constraints such that the constraint is LaxityFactor*sum of WCETs along the path. LaxityFactor is randomly selected
    // from the interval minLaxityFactor,maxLaxityFactor
    constrFactoryData = new LaxityConstraintFactoryData(
        1.5,   //minLaxityFactor
        2.0,   //maxLaxityFactor
        0      //seed
    );
    //--------------------------------------
    // END OF PARAMETRIZATION
    //--------------------------------------

    // create the actual factories with the parameter sets and the system model
    StdPlatformFactory          platformFactory = new StdPlatformFactory(systemModel, platformFactoryData);
    TaskChainApplicationFactory appFactory      = new TaskChainApplicationFactory(systemModel, appFactoryData);
    SensActMapper               mapper          = new SensActMapper(systemModel, mapperData);
    TaskChainPriorityAssigner   prioAssigner    = new TaskChainPriorityAssigner(systemModel, prioAssignerData);
    UUniFastTimingFactory       timingFactory   = new UUniFastTimingFactory(systemModel, timingFactoryData);
    LaxityConstraintFactory     constrFactory   = new LaxityConstraintFactory(systemModel, constrFactoryData);

    //-----------------------------
    // START THE GENERATION PROCESS
    //-----------------------------
    // generate platform
    platformFactory.generatePlatform();

    // generate two applications and map them on the platform
    for(int k=0; k<2; k++){
      ApplicationModel app = appFactory.generateApplication();
      mapper.map(app);
    }

    // assign priorities
    prioAssigner.assignPriorities();

    // assign timing properties
    timingFactory.generateTimings();

    // generate constraints
    constrFactory.generateConstraints();
    //-----------------------------
    // END OF TESTCASE GENERATION
    //-----------------------------

    //-----------------------------
    // WRITE THE TESTCASE TO A FILE
    //-----------------------------
    // save the model to an XML file
    try {
      new ModelSaver(outputPath+"System.xml").saveModel(systemModel);
    } catch (IOException e) {
      e.printStackTrace();
    }

    systemModel = null;

    try {
      systemModel = new ModelLoader(outputPath+"System.xml").generateSystem();
      new ModelSaver(outputPath+"System2.xml").saveModel(systemModel);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // save a graph of the testcase as pdf for visualization purposes
    // (this requires an additional package which is not yet publicly available)
    //    PdfPrinter.convertToPdf(systemModel, outputPath+"sysGraph.pdf");
  }
}