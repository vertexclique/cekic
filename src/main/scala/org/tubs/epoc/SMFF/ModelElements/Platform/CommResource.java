package org.tubs.epoc.SMFF.ModelElements.Platform;

import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractScheduler;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.SPPScheduler;

/**
 * Commresource defines the communication resource which is a special type of a resource. 
 *
 */
public class CommResource extends AbstractResource {
	/**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
	public CommResource(SystemModel m,String shortName, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup, AbstractScheduler scheduler) {
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
  public CommResource(SystemModel m,String shortName, int resId, AbstractScheduler scheduler) {
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
  public CommResource(SystemModel m,String shortName, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup) {
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
  public CommResource(SystemModel m,String shortName, int resId) {
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
  public CommResource(SystemModel m, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup, AbstractScheduler scheduler) {
    this(m, "CResId:"+resId, resId, resType, resGroup, scheduler);
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public CommResource(SystemModel m, int resId, AbstractScheduler scheduler) {
    this(m, "CResId:"+resId, resId, new GenericResourceType(), new GenericResourceGroup(), scheduler);
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public CommResource(SystemModel m, int resId, AbstractResourceType resType, AbstractResourceGroup resGroup) {
    this(m, "CResId:"+resId, resId, resType, resGroup, new SPPScheduler());
  }
  
  /**
   * Constructor
   * @param m         system model this resource is part of
   * @param shortName name
   * @param resId     resource id
   * @param resType   resource type
   * @param resGroup  resource group
   */
  public CommResource(SystemModel m,int resId) {
    this(m, "CResId:"+resId, resId, new GenericResourceType(), new GenericResourceGroup(), new SPPScheduler());
  }

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
//		Collection<TaskLink> taskLinkCollect;
//		
//    // remove references to the local schedulable elements (so they can be deleted by GC)
//    for(LocalApplicationModel localApp : LocalApplicationList.values()){
//      for(LocalTask localTask : localApp.getTaskList().values()){
//        localTask.getGlobalSchedElem().getLocalVersions().remove(localTask);
//      }
//      for(LocalTaskLink localTaskLink : localApp.getTaskLinkList().values()){
//        localTaskLink.getGlobalSchedElem().getLocalVersions().remove(localTaskLink);
//      }
//    }
//
//		// clear local view of applications
//		LocalApplicationList.clear();
//
//		// copy annotations of all applications that are at least partially on this resource
//		appCollect = this.systemModel.getApplications();
//		for(ApplicationModel globalApplication : appCollect){
//			taskLinkCollect = globalApplication.getTaskLinkList().values();
//			for(TaskLink globalTaskLink : taskLinkCollect){
//				if(globalTaskLink.getMappedTo()==this){
//					//          globalTaskLink.initTaskResourceMap(globalApplication);
//					localApplication = LocalApplicationList.get(globalApplication.getHash());
//					//if this application view does not exist yet in local view add it
//					if(localApplication==null){
//						localApplication = new LocalApplicationModel(globalApplication);
//						LocalApplicationList.put(localApplication.getHash(), localApplication);
//					}
//					// from this point only tasks and task links are cloned. everything below in the hierarchy is simply linked
//					// clone all task links into local view
//					globalTaskLink.createLocalTaskLink(localApplication);
//					// now copy all annotations of tasks (shallow - posRes and profiles are simply linked)
//					// this is not needed for implementation in C as timing models are propagated within AE.
//					// At this point however we need to store the connected tasks to hold the timing model at each resource locally
//					this.systemModel.getTask(globalTaskLink.getSrcTask()).createLocalTask(localApplication);
//					this.systemModel.getTask(globalTaskLink.getTrgTask()).createLocalTask(localApplication);
//				}
//			}
//		}
//	}

	//---------------utility functions---------------------

	 /**
   * Getter method for unique name.
   * @return the unique name
   */
	@Override
	public String getUniqueName() {
		return "CommId-"+getResId();
	}
}