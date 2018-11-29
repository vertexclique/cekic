package org.tubs.epoc.SMFF.ModelElements.Platform;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractScheduler;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.SPPScheduler;

/**
 * A resource implementation which extends {@link AbstractResource AbstractResource}.
 * 
 * @see AbstractResource
 *
 */
public class Resource extends AbstractResource{
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m,String shortName, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup, AbstractScheduler scheduler) {
    super(m, shortName, resId, resType, resGroup, scheduler);
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m,String shortName, int resId, AbstractScheduler scheduler) {
    this(m, shortName, resId, new GenericResourceType(), new GenericResourceGroup(), scheduler);
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m,String shortName, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup) {
    this(m, shortName, resId, resType, resGroup, new SPPScheduler());
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m,String shortName, int resId) {
    this(m, shortName, resId, new GenericResourceType(), new GenericResourceGroup(), new SPPScheduler());
  }
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup, AbstractScheduler scheduler) {
    this(m, "ResId:"+resId, resId, resType, resGroup, scheduler);
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m, int resId, AbstractScheduler scheduler) {
    this(m, "ResId:"+resId, resId, new GenericResourceType(), new GenericResourceGroup(), scheduler);
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup) {
    this(m, "ResId:"+resId, resId, resType, resGroup, new SPPScheduler());
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public Resource(SystemModel m,int resId) {
    this(m, "ResId:"+resId, resId, new GenericResourceType(), new GenericResourceGroup(), new SPPScheduler());
  }
	
	//file handling for csv statistics output
	private FileOutputStream statFile;
	private PrintStream statStream;


	//---------------Model maintenance---------------------  

//	/**
//	 * Refreshes the local view of the system by parsing all applications of the system
//	 *
//	 * @return void
//	 */
//	@Override
//	public void recreateLocalView(){
//		LocalApplicationModel localApplication;
//		Collection<ApplicationModel> appCollect;
//		Collection<Task> taskCollect;
//		Collection<TaskLinkIdentifier> taskLinkCollect;
//		
//		// remove references to the local schedulable elements (so they can be deleted by GC)
//		for(LocalApplicationModel localApp : LocalApplicationList.values()){
//		  for(LocalTask localTask : localApp.getTaskList().values()){
//		    localTask.getGlobalSchedElem().getLocalVersions().remove(localTask);
//		  }
//		  for(LocalTaskLink localTaskLink : localApp.getTaskLinkList().values()){
//		    localTaskLink.getGlobalSchedElem().getLocalVersions().remove(localTaskLink);
//		  }
//		}
//
//		// clear local view of applications
//		LocalApplicationList.clear();
//
//		// copy annotations of all applications that are at least partially on this resource
//		appCollect = this.systemModel.getApplications();
//		for(ApplicationModel globalApplication : appCollect){
//			taskCollect = globalApplication.getTaskList().values();
//			for(Task globalTask : taskCollect){
//				if(globalTask.getMappedTo()==this){
//					globalTask.initTaskResourceMap();
//					localApplication = LocalApplicationList.get(globalApplication.getHash());
//					//if this application view does not exist yet in local view add it
//					if(localApplication==null){
//						localApplication = new LocalApplicationModel(globalApplication);
//						LocalApplicationList.put(localApplication.getHash(), localApplication);
//					}
//					// from this point only tasks and task links are cloned to local versions. everything below in the hierarchy is simply linked
//					// clone all tasks (and link all profiles etc.) into local view
//					globalTask.createLocalTask(localApplication);
//					// clone all task links
//					taskLinkCollect = globalTask.getSrcLinkList().values();
//					for(TaskLinkIdentifier TaskLink : taskLinkCollect){
//						this.systemModel.getTaskLink(TaskLink).createLocalTaskLink(localApplication);
//					}
//					taskLinkCollect = globalTask.getTrgLinkList().values();
//					for(TaskLinkIdentifier TaskLink : taskLinkCollect){
//						this.systemModel.getTaskLink(TaskLink).createLocalTaskLink(localApplication);
//					}
//				}
//			}
//		}
//	}

	//---------------utility functions---------------------
	/**
   * Returns the short name.
   * @return the description
   */
	@Override
	public String toString(){
		return shortName /*+" ,ResID: "+ ResId + "\n" + "ResType: "+ ResType +" ResGroup: "+ ResGroup*/;
	}

	/**
   * Getter method for unique name.
   * Unique name is created from ID. Therefore it may be the same for resources of same type with same id.
   * @return the unique name
   */
	@Override
	public String getUniqueName() {
		return "ResId-"+this.resID;
	}
}
