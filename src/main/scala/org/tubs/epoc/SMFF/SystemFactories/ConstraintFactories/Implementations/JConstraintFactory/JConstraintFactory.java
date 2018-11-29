package org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.JConstraintFactory;

import java.lang.reflect.Constructor;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.AbstractTimingBehavior;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.PJdTimingBehavior;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.DataExtensions.PJdConstraint;
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.AbstractConstraintFactory;
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.AbstractConstraintFactoryData;

/**
 * This jitter constraint factory attaches jitter constraint to all tasks that have no outgoing task links.
 * The provided system model has to include a specification of actual timing behavior (i.e. analyzed with Symta
 * before), as the constraints are set as percentage of actual output jitter. If you don't have the Symta Interface
 * module you cannot properly use this factory.
 *
 * @see AbstractConstraintFactory
 */
public class JConstraintFactory extends AbstractConstraintFactory {
	private static Log logger = LogFactory.getLog(JConstraintFactory.class);
  
	private JConstraintFactoryData constraintFactoryData;
	private Random rnd;
	private Class<? extends PJdConstraint> constraintClass;

  
  /**
   * @param systemModel system model
   * @param constraintFactoryData constraint data factory
   */
  public JConstraintFactory(SystemModel systemModel, JConstraintFactoryData constraintFactoryData) {
    this(systemModel, constraintFactoryData, PJdConstraint.class);
  }  
  /**
   * @param systemModel system model 
   * @param constraintFactoryData constraint data factory
   * @param constraintClass class of constraint to create (in case an extended class of PJdConstraint should be added)
   */
  public JConstraintFactory(SystemModel systemModel, JConstraintFactoryData constraintFactoryData, Class<? extends PJdConstraint> constraintClass) {
    super(systemModel);
    this.constraintFactoryData = constraintFactoryData;
    this.recreateRndGens();
    this.constraintClass = constraintClass;
  }

  /**
   * Assigns priorities according to constraint generator and its 
   * parameters.
   */
  @Override
  public void generateConstraints() {
    // go through all tasks and find those, that are end of path
    for(ApplicationModel app : systemModel.getApplications()){
      for(Task task : app.getTaskList().values()){
        if(task.getSrcLinkList().size()==0){
          // for end of path, get the (global) output behavior and set a constraint
          AbstractTimingBehavior globalTiming = task.getOutputBehavior();
          if(globalTiming instanceof PJdTimingBehavior){
            PJdTimingBehavior pjdTiming = (PJdTimingBehavior) globalTiming;
            int period = pjdTiming.getPeriod();
            int jitter = pjdTiming.getJitter();
            int dmin   = 0;
            
            // create random factor in the interval jitterMinLaxity-jitterMaxLaxity
            Double rndFactor =  constraintFactoryData.jitterMinLaxity + rnd.nextDouble()*(constraintFactoryData.jitterMaxLaxity-constraintFactoryData.jitterMinLaxity);
            // adapt allowed jitter to that range
            jitter = (int)((double) jitter * rndFactor);
            
            PJdTimingBehavior pjdConstraint = new PJdTimingBehavior(period, jitter, dmin);
            
            Constructor<? extends PJdConstraint> con;
            try {
              con = constraintClass.getConstructor(new Class[]{PJdTimingBehavior.class});
              PJdConstraint jConstraint = con.newInstance(pjdConstraint); 
              task.addExtData(jConstraint, true, false, false);
            } catch (Exception e) {
              logger.error("No appropriate constructor found for the specified jitter constraint class. Ignoring this constraint.");
            }
          }
        }
      }
    }
  }

  /**
   * Getter method for the factory data.
   * @return the factory data
   */
  @Override
  public AbstractConstraintFactoryData getConstraintFactoryData() {
    return constraintFactoryData;
  }

  /**
   * Recreates the random number generator from the provided seed.
   */
  @Override
  public void recreateRndGens() {
    rnd = new Random(constraintFactoryData.getSeed());
  }

}
