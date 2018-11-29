package org.tubs.epoc.SMFF.ModelElements.Platform;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.tubs.epoc.SMFF.ModelElements.ExtendibleModelElement;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.SchedElemIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Application.SchedulableElement;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractScheduler;

/**
 * Intended to be extended by the resources. A resource is an extendible model element which belongs to a system model. 
 * It has an id and type and it belongs to a resource group. 
 * Moreover applications and scheduled elements can be mapped to a resource. 
 * 
 * Once extended, this class can be used to extend and manipulate the resources. It also provides certain helper functions
 * such as {@link #getLoad() getLoad()}, which returns the load of an application.
 * 
 * @see ExtendibleModelElement
 *
 */
public abstract class AbstractResource extends ExtendibleModelElement<AbstractResourceData>{
	/**
	 * System model which this resource is part of
	 */
  protected SystemModel systemModel;
  
  /**
   * Short name of this resource
   */
  protected String shortName;
  
  /**
   * Id of this resource
   */
  protected int resID;
  
  /**
   * Type of this resource
   */
  protected AbstractResourceType resType;

  /**
   * Group of this resource
   */
  protected AbstractResourceGroup resGroup;
  
  /**
   * Hashmap of neighbors
   */
  private HashMap<Integer,AbstractResource> neighbors;
  
  /**
   * associated scheduler
   */
  private AbstractScheduler scheduler;
  
  /**
   * Lists of applications mapped to this resource
   */
  protected HashMap<Integer, ApplicationModel> localApps = new HashMap<Integer, ApplicationModel>();
  /**
   * Lists of local scheduled elements mapped to this resource
   */
  protected HashMap<SchedElemIdentifier, SchedulableElement> localSchedElems = new HashMap<SchedElemIdentifier, SchedulableElement>();
  /**
   * Lists of local application scheduled elements mapped to this resource
   */
  protected HashMap<ApplicationModel, LinkedList<SchedulableElement>> localAppSchedElems = new HashMap<ApplicationModel, LinkedList<SchedulableElement>>();

  /**
   * Additional data to be attached by other classes using this data structure
   */
  protected HashMap<Class<? extends AbstractResourceData>, LinkedList<AbstractResourceData>> dataObjects = new HashMap<Class<? extends AbstractResourceData>, LinkedList<AbstractResourceData>>();

  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName short name for this resource
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   * @param scheduler associated scheduler
   */
  public AbstractResource(SystemModel m, String shortName, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup, AbstractScheduler scheduler) {
    this.shortName = shortName;
    this.resID = resId;
    this.resType = resType;
    this.resGroup = resGroup;
    this.scheduler = scheduler;
    
    if (m==null) {
      RuntimeException e = new RuntimeException("SystemModel must not be NULL when instantiating Resource");
      e.printStackTrace();
      throw e;
    }

    this.systemModel = m;
    neighbors = new HashMap<Integer,AbstractResource>();
  }
  
  /**
   * Getter method for the short name.
   * @return a short name of the resource user-supplied
   */
  public String getShortName() {
    return (shortName == null)? "": shortName;
  }
  
  /**
   * Getter method for unique name.
   * Unique name is created from ID. Therefore it may be the same for resources of same type with same id.
   * @return the unique name
   */
  public abstract String getUniqueName();
  
  /**
   * Returns the short name, if no short name assigned returns unique name.
   * @return the description
   */
  @Override
  public String toString() {
    return (shortName == null)? getUniqueName() : shortName;
  }

  /**
   * Getter method for the ID of this resource.
   * @return the resource id
   */
  public int getResId() {
    return resID;
  }

  /**
   * Getter method for resource type.
   * @return the type of this resource
   */
  public AbstractResourceType getResType() {
    return resType;
  }

  /**
   * Getter method for resource group.
   * @return the group of this resource
   */
  public AbstractResourceGroup getResGroup() {
    return resGroup;
  }
  
  /**
   * Maps the specified schedulable element to this resource.
   * Note: this function should only be called from the application model, otherwise it will cause the data to be inconsistent.
   * @param schedElem schedulable element to map to this resource
   */
  public void mapSchedElem(SchedulableElement schedElem){
    ApplicationModel app = schedElem.getApplication();
    // add entry, that this application exists on this resource
    localApps.put(schedElem.getAppId(), app);
    // add entry for the schedulable element
    localSchedElems.put(schedElem.getIdent(), schedElem);
    // add the schedulable element to the list of schedulable elements of the corresponding local application
    if(!localAppSchedElems.containsKey(app)){
      localAppSchedElems.put(app, new LinkedList<SchedulableElement>());
    }
    localAppSchedElems.get(app).add(schedElem);
  }

  /**
   * Unmaps the specified schedulable element from this resource.
   * Note: this function should only be called from the application model, otherwise data will be inconsistent
   * @param schedElem schedulable element to remove from this resource
   */
  public void unmapSchedElem(SchedulableElement schedElem){
    ApplicationModel app = schedElem.getApplication();
    // remove the task from the list of tasks
    localSchedElems.put(schedElem.getIdent(), schedElem);
    // remove the schedulable element from the list of schedulable elements of the corresponding local application
    localAppSchedElems.get(app).remove(schedElem);
    // if no more schedulable elements exist on this resource for this application remove the application from the list
    if(localAppSchedElems.get(app).size()==0){
      localApps.remove(schedElem.getAppId());
      localAppSchedElems.remove(app);
    }
  }

  /**
   * Returns all applications that have at least one task (or tasklink) mapped to this Resource.
   * 
   * @return a collection of ApplicationModels
   */
  public Collection<ApplicationModel> getMappedApps() {
    return localApps.values();
  }

  /**
   * Returns all schedulable element of a given application that has at least one task (or tasklink) mapped to this Resource.
   * 
   * @param app application model to retrieve schedulable elems for
   * @return a collection of schedulable elements
   */
  public Collection<SchedulableElement> getMappedAppSchedElems(ApplicationModel app) {
    return localAppSchedElems.get(app);
  }

	/**
	 * Returns all tasks (or tasklinks) mapped to this Resource.
	 * 
	 * @return a collection of either Tasks or Task Links
	 */
	public Collection<SchedulableElement> getMappedSchedElems() {
	  return localSchedElems.values();
	}
	
	/**
	 * Returns the schedulable element with the provided identifier, if it exists on this resource.
	 * @param identifier id of the schedulable element
	 * @return schedulable element if mapped to this resource else null
	 */
	public SchedulableElement getMappedSchedElem(SchedElemIdentifier identifier){
	  return localSchedElems.get(identifier);
	}
	
	/**
	 * Calculates the load on this resource. 
	 * @return the load on this resource
	 */
  public double getLoad(){
    double load = 0.0;
    int actPeriod = Integer.MAX_VALUE;
    
    for(ApplicationModel app : systemModel.getApplications()){
      // get activation period for this application (assuming AND activation and equal periods)
      for(Task task : app.getTaskList().values()){
        if(task.getTrgLinkList().size()==0){
          actPeriod = task.getActiveProfile().getActivationPeriod();
          break;
        }
      }
      
      // add load of this application to total load
      if(this instanceof Resource){
        for(Task task : app.getTaskList().values()){
          if(task.getMappedTo()==this){
            load += (double)task.getActiveProfile().getWCET()/(double)actPeriod;
          }
        }
      } else if(this instanceof CommResource){
        for(TaskLink taskLink : app.getTaskLinkList().values()){
          if(taskLink.getMappedTo()==this){
            load += (double)taskLink.getWCET()/(double)actPeriod;
          }
        }
      }
    }
    
    return load;
  }
  
  public final AbstractScheduler getScheduler() {
    return scheduler;
  }

  // TODO: there should be some sanity checks included that check whether sched elems have the proper scheduling parameters attached
  public final void setScheduler(AbstractScheduler scheduler) {
    this.scheduler = scheduler;
  }

  //---------------utility functions---------------------	
  /**
   * Reorders every task randomly and assigns them priorities.
   * @param seed seed value to be used for generating random priorities.
   * @deprecated now various scheduler are supported. this should be moved somewhere else, possibly the scheduler class
   */
  public void rndPriorities(long seed){
    Random rndInt =  new Random(seed);
    LinkedList<SchedulableElement> schedElemPrioList = new LinkedList<SchedulableElement>();

    // put every task at random position in the list
    for(SchedulableElement schedElem : localSchedElems.values()){
      if((schedElem instanceof Task && this instanceof Resource) ||
         (schedElem instanceof TaskLink && this instanceof CommResource)){
        int index = 0;
        if(schedElemPrioList.size()>0){
          index = rndInt.nextInt(schedElemPrioList.size());
        }
        schedElemPrioList.add(index, schedElem);
      }
    }

    // assign priorities according to the list
    int i = 1;
    for(SchedulableElement schedElem : schedElemPrioList){
      schedElem.setPrio(i);
      i++;
    }
  }

	/**
	 * Getter method for the system model of this resource. 
	 * @return the system model belonging to this resource
	 */
	public SystemModel getSystemModel() {
		return systemModel;
	}

  /**
   * Sets the system model belonging to this resource.
   * @param systemModel system model
   */
  protected void setSystemModel(SystemModel systemModel) {
    this.systemModel = systemModel ;
  }

	/**
	 * TODO: DOCUMENT ME!
	 */
//	public abstract void recreateLocalView();

	/**
	 *  Inserts a link between this and the given resource
	 *  
	 *  TODO: public API call should be moved into system Model
	 *  
	 *   @param resource resource to add link to
	 *   @throws IllegalArgumentException if resource == null
	 */
	public void addLink(AbstractResource resource) throws IllegalArgumentException {
		// argument check
		if(resource == null) {
			throw new IllegalArgumentException("resource to add link to must not be NULL!");
		}

		// insert relation if not already known
		if (this.neighbors.get(resource.getResId()) == null) {
			this.neighbors.put(resource.getResId(),resource);
		}

		if (resource.neighbors.get(this.getResId()) == null) {
			resource.neighbors.put(this.getResId(),this);			
		}
	}
	
	/**
	 * Resets all entries for neighbors.
	 */
	public void resetConnections(){
	  neighbors.clear();
	}
	
	/**
	 * Resets all entries for applications and schedulable elements.
	 */
	public void resetApplications(){
	  localApps.clear();
	  localAppSchedElems.clear();
	  localSchedElems.clear();
	}

  /**
   *  Removes a link between this and the given resource
   *  
   *  TODO: public API call should be moved into system Model
   *  
   *   @param resource resource to remove link to
   *   @throws IllegalArgumentException if resource == null
   */
	public void removeLink(AbstractResource resource) throws IllegalArgumentException{
    // argument check
    if(resource == null) {
      throw new IllegalArgumentException("resource to remove link from must not be NULL!");
    }

    // insert relation if not already known
    this.neighbors.remove(resource.getResId());
    resource.neighbors.remove(this.getResId());
	}

	/**
	 * Returns a collection of all neighbors of this resource
	 * 
	 * @return all neighbors
	 */
	public Collection<AbstractResource> getNeighbors() {
		return neighbors.values();
	}

	/**
	 * Returns the neighbor with the given ID or null if it does
	 * not exist
	 * 
	 * @param id identification of the neighbor
	 * @return neighbor with given ID
	 */
	public AbstractResource getNeighbor(int id) {
		return neighbors.get(id);
	}
}
