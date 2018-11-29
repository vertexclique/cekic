/**
 * An implementation of an application factory. 
 * <p>
 * This is the standard tgff-based application factory that was developed in chrisl's bachelor thesis
 * (the code is rather un-optimized und should be stripped of unnecessary classes:
 *  do we really need? -ApplicationGraph
 *                     -ApplicationGraphEditor
 *                     -DirectedEdge
 *                     -UndirectedEdge
 *                     -Vertex
 * They all seem to be something like a copy of the actual model in model elements)
 * @author moritzn
 * @see AbstractApplicationFactory  
 */

package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.TaskChainApplicationFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.AbstractApplicationFactory;

/**
 * This is a very simple and deterministic application factory. It merely generates a chain of tasks with
 * a specified number of tasks.
 * 
 * @author moritzn
 *
 */
public class TaskChainApplicationFactory extends AbstractApplicationFactory {
	private static Log logger = LogFactory.getLog(TaskChainApplicationFactory.class); 
  public static final String IDENTIFIER = "SystemData TaskChainApplicationFactory";

	private TaskChainApplicationFactoryData applicationFactoryData;

	private ApplicationModel app;

	/**
	 * Constructs an instance of an application factory.
	 * @param systemModel system model
	 * @param applicationFactoryData application data factory
	 */
	public TaskChainApplicationFactory(SystemModel systemModel, TaskChainApplicationFactoryData applicationFactoryData) {
		super(systemModel);
		this.applicationFactoryData = applicationFactoryData;
		this.applicationFactoryData.setApplicationFactory(this);
		this.recreateRndGens();
	}

	/**
	 * Getter method for the global identifier of this factory.
	 * @return the global identifier of this factory
	 */
	public String getIdentifier() {
		return IDENTIFIER;
	}

	/**
	 * Generates an application and returns it.
	 * @return the constructed application
	 */
	public ApplicationModel generateApplication() {
		// Application ID
		int appId = 1;

		// get a free application id
		for (appId = 1; appId < Integer.MAX_VALUE; appId++) {
			if (systemModel.getApplication(appId) == null) {
				break;
			}
		}

		// --- Creating Application from Graph ---
		// empty global application
		ApplicationModel app = new ApplicationModel(systemModel, appId, 0);

		// --- Add Application to System Model ---
		// insert application model into system
		systemModel.addApplication(app);

		// create Tasks and TaskLinks
		createTasksAndTaskLinks(app);

		return app;
	}

	/**
	 * create the Tasks and TaskLinks
	 */
	private void createTasksAndTaskLinks(ApplicationModel app) {
		logger.info("model(V,ID): " + app.getAppV() + " " + app.getAppId());

		// Tasks
		for (int i=0; i<applicationFactoryData.getNumTasks(); i++) {
			Task task = new Task(
			    "A"+app.getAppId()+"T"+i,  // name
			    app, // application
			    i); // task id
			app.addTask(task);
			logger.info(task.getUniqueName());
		}
		
		// TaskLinks
		for (int i=0; i<applicationFactoryData.getNumTasks()-1; i++) {
			logger.info("model(V,ID): " + app.getAppV() + " " + app.getAppId());

			TaskLink link = new TaskLink(
			    "A"+app.getAppId()+"TL"+i+"-"+(i+1), // name
			    app, // application
			    i, // link id
			    i, // source task id
			    i+1); // target task id
			logger.info(link.getUniqueName() + "name:" + link.getShortName());
			app.addTaskLink(link);
			// add TaskLink to Tasks
			systemModel.getApplication(app.getAppId()).getTask(i).addTaskLink(link);
			systemModel.getApplication(app.getAppId()).getTask(i+1).addTaskLink(link);
		}
	}

	/**
	 * Getter method for the application factory data
	 * @return the application factory data of this application factory
	 */
	public TaskChainApplicationFactoryData getApplicationFactoryData() {
		return applicationFactoryData;
	}

	/**
	 * Seed the random number generator via the seed of the application factory data.
	 */
	public void recreateRndGens() {
	}
}
