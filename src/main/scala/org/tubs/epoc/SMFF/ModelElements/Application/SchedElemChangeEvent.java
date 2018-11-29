package org.tubs.epoc.SMFF.ModelElements.Application;

/**
 * Class description for a task event change. A change event is triggered by a changed object on a task. Possible event 
 * types are output behavior and priority.
 *
 */
public class SchedElemChangeEvent {
	
	/**
	 * Enumerator for possible event types that can be changed. An event can be either a timing behavior description 
	 * or a priority description.
	 *
	 */
	public static enum EventType {
		/**
		 * object is type TimingBehavior
		 */
		OUTPUT_BEHAVIOR, 	
		/**
		 * object is type Integer
		 */
		PRIORITY		
	}
	
	// the object that caused the event to occur
	private Object changedObject;
	// the type of the event (see above)
	private EventType type;
	// the task on which the event occured
	private SchedulableElement schedElem;
	
	/**
	 * Constructor.
	 * 
	 * Only to be called from within this package, when producing 
	 * task changed events - thus protected visibility
	 * 
	 * @param schedElem the task the event originates from
	 * @param type type of the event
	 * @param o the data object that actually changed
	 */
	protected SchedElemChangeEvent(SchedulableElement schedElem, EventType type, Object o) {
		this.changedObject = o;
		this.type = type;
		this.schedElem = schedElem;
	}
	
	/**
	 * The object that caused the change event to occur.
	 * 
	 * @return changed object 
	 */
	public Object getChangedObject() {
		return changedObject;
	}
	
	/**
	 * Type indicating what changed.
	 * Determines type of changed Object.
	 * 
	 * @return the type of the event that changed
	 */
	public EventType getType() {
		return type;
	}
	
	/**
	 * The task that changed.
	 * 
	 * @return reference to the task that changed
	 */
	public SchedulableElement getAffectedSchedElem() {
		return schedElem;
	}
}
