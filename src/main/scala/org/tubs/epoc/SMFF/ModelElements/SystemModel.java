package org.tubs.epoc.SMFF.ModelElements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLinkIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Platform.ResourceGraph;

/**
 * This class is an extended model which stores related information regarding to a system.
 * <p>
 * A system is identified by its resource graph, resources and applications. Furthermore there are two types of
 * resources. The first type is the resource graph itself, whereas the second is the list of communication resources.
 * Both of them are identified by a unique name. Applications, on the other hand, are
 * {@link org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel ApplicationModel} model which have an id. Time
 * base for the analysis of a system model is in milliseconds.
 * 
 * This class includes both search methods between two resources, as well as necessary getter and setter methods for the
 * resource graph, resources and applications of a system model.
 * 
 * @see ResourceGraph
 * @see Resource
 * @see CommResource
 * @see ApplicationModel
 * @see ExtendibleModelElement
 * @see AbstractSystemData
 * 
 */
public class SystemModel extends ExtendibleModelElement<AbstractSystemData> {
	// generic constants
	public static final String TIME_BASE = "ms"; // time base for all analysis

	// structures to store annotations
	private Hashtable<String, Resource> resourceList = new Hashtable<String, Resource>();
	private Hashtable<String, CommResource> commResourceList = new Hashtable<String, CommResource>();
	private Hashtable<Integer, ApplicationModel> applicationList = new Hashtable<Integer, ApplicationModel>();

	// adjacency matrix of the platform
	private ResourceGraph resModel;

	public SystemModel() {
	}

	// -----------MODEL MODIFICATION----------
	/**
	 * Adds a resource to this system model.
	 * 
	 * Only resources which are an instance of {@link org.tubs.epoc.SMFF.ModelElements.Platform.Resource Resource} or
	 * {@link org.tubs.epoc.SMFF.ModelElements.Platform.CommResource CommResource} will be added to the system model. Any
	 * other resource type passed as a parameter to this method will not taken into account.
	 * 
	 * @param resource
	 *          Resource to be added to this system model.
	 * @see AbstractResource
	 * @see CommResource
	 * @see Resource
	 */
	public void addResource(AbstractResource resource) {
		if (resource instanceof Resource) {
			resourceList.put(resource.getUniqueName(), (Resource) resource);
		} else if (resource instanceof CommResource) {
			commResourceList.put(resource.getUniqueName(), (CommResource) resource);
		}
	}

	/**
	 * Removes the resource which is passed as parameter to this method from the system model.
	 * 
	 * If there isn't such a resource, this method has no effect.
	 * 
	 * @param resource
	 *          to be deleted from the system model.
	 * @see AbstractResource
	 * @see CommResource
	 * @see Resource
	 */
	public void removeResource(AbstractResource resource) {
		if (resource instanceof Resource) {
			resourceList.remove(resource.getUniqueName());
		} else if (resource instanceof CommResource) {
			commResourceList.remove(resource.getUniqueName());
		}
	}

	/**
	 * Removes all resources that this system model has.
	 */
	public void removeAllResources() {
		resourceList.clear();
		commResourceList.clear();
	}

	/**
	 * Adds an application model to this system model.
	 * 
	 * @param app
	 *          application model to be added to this system model.
	 * @throws NullPointerException
	 *           if the application model is null.
	 * 
	 * @see ApplicationModel
	 */
	public void addApplication(ApplicationModel app) {
		applicationList.put(app.getAppId(), app);
	}

	/**
	 * Recreates the resource graph based on the current platform.
	 */
	public void recreateResGraph() {
		resModel = new ResourceGraph(this);
	}

	// /**
	// * recreates the Local Views from Global View
	// */
	// public void recreateLocalViews(){
	// Collection<Resource> resCollect = ResourceList.values();
	// Collection<CommResource> cResCollect = CommResourceList.values();
	// for(Resource Resource : resCollect){
	// Resource.recreateLocalView();
	// }
	// for(CommResource cResource : cResCollect){
	// cResource.recreateLocalView();
	// }
	// }
	//
	// /**
	// * refreshes the Global View from Local Views
	// * updates:
	// * - current output timing behavior
	// * - current response time
	// * - neighbor overhead;
	// * - current inputOverestimation
	// */
	// public void refreshGlobalView(){
	// //go through all resources to copy tasks to global view
	// for(Resource resource : ResourceList.values()){
	// for(LocalApplicationModel currentApp : resource.getLocalApplicationList()){
	// for(LocalTask currentLocalTask : currentApp.getTaskList().values()){
	// // synchronize timing data and prio
	// currentLocalTask.synchronizeGlobalTask();
	// }
	// }
	// }
	// //go through all communication resources to copy tasklinks to global view
	// for(CommResource cResource : CommResourceList.values()){
	// for(LocalApplicationModel currentApp : cResource.getLocalApplicationList()){
	// for(LocalTaskLink currentLocalTaskLink : currentApp.getTaskLinkList().values()){
	// // synchronize timing data and prio
	// currentLocalTaskLink.synchronizeGlobalTaskLink();
	// }
	// }
	// }
	// }

	// -----------MODEL QUERY----------
	/**
	 * Getter method for all the resources attached to this system model.
	 * 
	 * @return all the resources that this system model has.
	 */
	public Collection<AbstractResource> getAllRes() {
		LinkedList<AbstractResource> allRes = new LinkedList<AbstractResource>();
		allRes.addAll(resourceList.values());
		allRes.addAll(commResourceList.values());
		return allRes;
	}

	/**
	 * Getter method for the resource table of this system model.
	 * 
	 * @return the resource table of this system model.
	 */
	public Hashtable<String, Resource> getResourceTable() {
		return resourceList;
	}

	/**
	 * Getter method for communication resources attached to this system model.
	 * 
	 * @return the communication resources of this system model.
	 */
	public Hashtable<String, CommResource> getCommResourceTable() {
		return commResourceList;
	}

	/**
	 * Getter method for the resource graph of this system model.
	 * 
	 * @return the resource graph attached to this system model.
	 */
	public ResourceGraph getResModel() {
		return resModel;
	}

	/**
	 * Setter method to set the resource graph of this system model.
	 * 
	 * @param resModel
	 *          resource model to be assigned to this system model.
	 */
	public void setResModel(ResourceGraph resModel) {
		this.resModel = resModel;
	}

	/**
	 * Returns the Resource with the given ID using linear search.
	 * 
	 * Used for fixed time lookup.
	 * 
	 * @see SystemModel#getAbstractResource(String)
	 * 
	 * @param ResourceId
	 *          resource id to be searched.
	 * @return the first resource in the list with the given ID or null if it does not exist
	 */
	public Resource getResource(int ResourceId) {
		Collection<Resource> c = resourceList.values();
		for (Resource Resource : c) {
			if (Resource.getResId() == ResourceId)
				return Resource;
		}
		return null;
	}

	/**
	 * Getter method for resources of this system model with this system model.
	 * 
	 * Resource graph has priority over communication resources. That is if there are both resource graph and
	 * communication resource graph with this name, the resource graph will be returned. If there are neither resource
	 * graphs nor communication resource graphs with this name, method will return null.
	 * 
	 * @param uniqueName
	 *          which identifies the resource to be searched.
	 * @return the resource with this name, null if there is no resource with this name.
	 */
	public AbstractResource getAbstractResource(String uniqueName) {
		if (resourceList.get(uniqueName) != null) {
			return resourceList.get(uniqueName);
		} else if (commResourceList.get(uniqueName) != null) {
			return commResourceList.get(uniqueName);
		} else {
			return null;
		}
	}

	/**
	 * Getter method for the communication resource graph of this system model with the id which is passed as a parameter
	 * to the method.
	 * 
	 * If there are more than one communication resources with this id, then the first one in the list will be returned.
	 * If there isn't any communication resources with this id, method will return null.
	 * 
	 * @param CommId
	 *          which identifies the communication resource to be searched.
	 * @return the communication resource with this id, null if there is no communication resources with this id.
	 */
	public CommResource getCommResource(int CommId) {
		Collection<CommResource> c = commResourceList.values();
		for (CommResource CommResource : c) {
			if (CommResource.getResId() == CommId)
				return CommResource;
		}
		return null;
	}

	/**
	 * Uses depth-first search to find the shortest path from res1 to res2.
	 * 
	 * @param res1
	 *          - first resource
	 * @param res2
	 *          - second resource
	 * @return the shortest distance from <tt>res1</tt> to <tt>res2</tt>.
	 * @throws IllegalArgumentException
	 *           if any of the resources visited is not of type {@link org.tubs.epoc.SMFF.ModelElements.Platform.Resource
	 *           Resource}.
	 * @throws NullPointerException
	 *           if the parameter <tt>visitedNodes</tt> is null.
	 */
	public int getDist(Resource res1, Resource res2) {
		return getDist(res1, res2, new HashSet<AbstractResource>());
	}

	/**
	 * Uses depth-first search to find the shortest path from res1 to res2.
	 * 
	 * All the visited nodes will be added to the parameter <tt>visitedNodes</tt>. This parameter must not be null.
	 * Otherwise it will cause a NullPointerException.
	 * 
	 * @param res1
	 *          - first resource
	 * @param res2
	 *          - second resource
	 * @param visitedNodes
	 *          - HashSet to log visited nodes (pass new HashSet<Resource>())
	 * @return the shortest distance from <tt>res1</tt> to <tt>res2</tt>.
	 * @throws IllegalArgumentException
	 *           if any of the resources visited is not of type {@link org.tubs.epoc.SMFF.ModelElements.Platform.Resource
	 *           Resource}.
	 * @throws NullPointerException
	 *           if the parameter <tt>visitedNodes</tt> is null.
	 */
	public int getDist(Resource res1, Resource res2, HashSet<AbstractResource> visitedNodes) {
		int minDist = Integer.MAX_VALUE;

		if (res1 == res2) {
			return 0;
		}

		visitedNodes.add(res1);
		// always do two hops -- this way we jump over communication resources
		for (AbstractResource cRes : res1.getNeighbors()) {
			// second hop :)
			if (!visitedNodes.contains(cRes)) {
				visitedNodes.add(cRes);
				for (AbstractResource res : cRes.getNeighbors()) {
					if (!visitedNodes.contains(res)) {
						// sanity check if the two-hopped experience worked
						// we should now have to Resources again and skipped the ComResource in between
						if (!((res instanceof Resource) && (res2 instanceof Resource))) {
							throw new IllegalStateException(
							    "depth first search in Resource graph encountered a comm-resource where a normal resource was expected");
						}
						// the cast is now OK
						int currentDist = getDist((Resource) res, (Resource) res2, visitedNodes);
						minDist = Math.min(minDist, currentDist);
					}
				}
			}
			visitedNodes.remove(cRes);
		}

		visitedNodes.remove(res1);
		if (minDist < Integer.MAX_VALUE) {
			minDist++;
		}
		return minDist;
	}

	/**
	 * Getter method for the application models attached to this system model.
	 * 
	 * @return the list of application models for this system model.
	 */
	public Collection<ApplicationModel> getApplications() {
		return applicationList.values();
	}

	/**
	 * Getter method for application model of this system model with this application id.
	 * 
	 * @param AppId
	 *          unique identifier of the application model of this system model.
	 * @return the application model of this system model with this applicaion id.
	 */
	public ApplicationModel getApplication(int AppId) {
		return applicationList.get(AppId);
	}

	/**
	 * Getter method for the task with the passed identifier which is associated to this system model.
	 * 
	 * Only the application id and the task id of the identifier <tt>taskId</tt> is used on returning the queried task.
	 * There must exist an application model with the application id of the identifier <tt>taskId</tt> as well as a task
	 * with the element id of <tt>taskId</tt> associated to the application model for a non-null result.
	 * 
	 * @param taskId
	 *          identifier to be used to search the task of the system model.
	 * @return the task with the given element id in the application model attached to this system model, null if either
	 *         an application model with the application id or a task with an element id of the task identifier passed to
	 *         the method exists.
	 * @throws NullPointerException
	 *           if the <tt>taskId</tt> is null.
	 */
	public Task getTask(TaskIdentifier taskId) {
		ApplicationModel app = this.getApplication(taskId.getAppId());
		return app.getTask(taskId.getElemId());
	}

	/**
	 * Getter method for a task link from a source to a target both of which are included as information in the parameter
	 * <tt>taskLinkId</tt> passed to the method.
	 * 
	 * For a valid query the parameter <tt>taskLinkId</tt> must include the necessary information which are the
	 * application id, source task id and target task id. If the system model doesn't haven an application with the passed
	 * application id or there is no link from source to target, method will return null.
	 * 
	 * @param taskLinkId
	 *          identifier to be used for querying.
	 * @return the tasklink from a certain source to a target in an application attached to this system model, null if
	 *         there is no valid link or any applicaion.
	 * @throws NullPointerException
	 *           if the <tt>taskLinkId</tt> is null.
	 */
	public TaskLink getTaskLink(TaskLinkIdentifier taskLinkId) {
		return this.getApplication(taskLinkId.getAppId()).getTaskLink(taskLinkId.getSrcTaskId(), taskLinkId.getTrgTaskId());
	}

	/**
	 * Clears the resource graph of this system model.
	 */
	public void clearResources() {
		resourceList = null;
		resourceList = new Hashtable<String, Resource>();
	}

	/**
	 * Clears the communication resource graph of this system model.
	 */
	public void clearCommResources() {
		commResourceList = null;
		commResourceList = new Hashtable<String, CommResource>();
	}

	/**
	 * Clears the applications of this system model.
	 */
	public void clearApplications() {
		applicationList = null;
		applicationList = new Hashtable<Integer, ApplicationModel>();
	}
}
