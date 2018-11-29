package org.tubs.epoc.SMFF.Utility;

import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Application.SysLatencyConstraint;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLinkIdentifier;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;

/**
 * Hashing related utility class. Through this class hashing identifiers for an application, task, task link, resource,
 * communication resource and constraint can be created.
 * 
 */
public class Hashing {
	private static final String LAT = "Lat:";
	private static final String COMM_ID = "CommId:";
	private static final String RES_ID = "ResId:";
	private static final String TRG_TASK_ID = "-TrgTaskId:";
	private static final String SRC_TASK_ID = "-SrcTaskId:";
	private static final String TASK_ID = "-TaskId:";
	private static final String APP_ID = "AppId:";

	private Hashing() { // Prevent the class to be initialized. Not necessary since it is a utility class.
	}

	/**
	 * Constructs a hash id from the given application id.
	 * 
	 * @param appId
	 *          to be used to create a hash.
	 * @return the hash id created from the passed application id.
	 */
	public static String getAppHash(int appId) {
		return APP_ID + appId;
	}

	/**
	 * Constructs a hash id from the given application model.
	 * 
	 * Only the application id of the model passed will be used to create the hash.
	 * 
	 * @param app
	 *          application model to be used to create the hash id.
	 * @return the hash id created from the given model.
	 * @throws NullPointerException
	 *           if the parameter <tt>app</tt> is null.
	 */
	public static String getHash(ApplicationModel app) {
		return APP_ID + app.getAppId();
	}

	/**
	 * Constructs a task hash id from the given application id and task id.
	 * 
	 * @param appId
	 *          application id to be used for creating the task hash.
	 * @param taskId
	 *          task id to be used for creating the task hash.
	 * @return the task hash constructed from the application id and the task id.
	 */
	public static String getTaskHash(int appId, int taskId) {
		return APP_ID + appId + TASK_ID + taskId;
	}

	/**
	 * Constructs a task hash id from the passed parameter <tt>task</tt>.
	 * 
	 * Only the application id and the element id of the task are used to create the task hash id.
	 * 
	 * @param task
	 *          to be used to construct the task hash id.
	 * @return the task hash id created from the parameter <tt>task</tt>.
	 * 
	 * @throws NullPointerException
	 *           if the parameter <tt>task</tt> is null.
	 */
	public static String getHash(Task task) {
		return APP_ID + task.getAppId() + TASK_ID + task.getElemId();
	}

	/**
	 * Constructs a task hash id from the passed parameter <tt>taskId</tt>.
	 * 
	 * Only the application id and the element id of the task identifier are used to create the task hash id.
	 * 
	 * @param taskId
	 *          task identifier to be used to construct the task hash id.
	 * @return the task hash id created from the parameter <tt>taskId</tt>.
	 * 
	 * @throws NullPointerException
	 *           if the parameter <tt>taskId</tt> is null.
	 */
	public static String getHash(TaskIdentifier taskId) {
		return APP_ID + taskId.getAppId() + TASK_ID + taskId.getElemId();
	}

	/**
	 * Constructs a task link hash id from the passed parameters application id, source task id and target task id.
	 * 
	 * @param appId
	 *          application id to be used to create a task link hash id.
	 * @param srcTaskId
	 *          source task id to be used to create a task link hash id.
	 * @param trgTaskId
	 *          target task id to be used to create a task link hash id.
	 * @return the task link hash id created from the application id, source task id and target task id.
	 */
	public static String getTaskLinkHash(int appId, int srcTaskId, int trgTaskId) {
		return APP_ID + appId + SRC_TASK_ID + srcTaskId + TRG_TASK_ID + trgTaskId;
	}

	/**
	 * Constructs a task link hash id from the passed parameter <tt>taskLink</tt>.
	 * 
	 * Necessary information in order to create a task link hash is application id, source task id and target task id
	 * which are to be stored in the parameter <tt>taskLink</tt>.
	 * 
	 * @param taskLink
	 *          task link from which a task link hash id will be created.
	 * @return the task link hash id created from the parameter <tt>taskLink</tt>.
	 * 
	 * @throws NullPointerException
	 *           if the parameter <tt>taskLink</tt> is null.
	 */
	public static String getHash(TaskLink taskLink) {
		return APP_ID + taskLink.getAppId() + SRC_TASK_ID + taskLink.getSrcTaskId() + TRG_TASK_ID + taskLink.getTrgTaskId();
	}

	/**
	 * Constructs a task link hash id from the passed parameter <tt>taskLinkId</tt>, which is an identifier for the task
	 * link.
	 * 
	 * Necessary information in order to create a task link hash is application id, source task id and target task id
	 * which are to be stored in the parameter <tt>taskLinkId</tt>.
	 * 
	 * @param taskLinkId
	 *          task link identifier from which a task link hash id will be created.
	 * @return the task link hash id created from the parameter <tt>taskLinkId</tt>.
	 * 
	 * @throws NullPointerException
	 *           if the parameter <tt>taskLinkId</tt> is null.
	 */
	public static String getHash(TaskLinkIdentifier taskLinkId) {
		return APP_ID + taskLinkId.getAppId() + SRC_TASK_ID + taskLinkId.getSrcTaskId() + TRG_TASK_ID
		    + taskLinkId.getTrgTaskId();
	}

	/**
	 * Constructs a resource hash id from the given resource id.
	 * 
	 * @param resId
	 *          resource id to be used to create a resource hash id.
	 * @return the resource hash id created using the the given <tt>resId</tt>.
	 */
	public static String getResHash(int resId) {
		return RES_ID + resId;
	}

	/**
	 * Constructs a resource hash id from the passed parameter <tt>resource</tt>.
	 * 
	 * Necessary information to create a resource hash id is the resource id and this must be available in the passed
	 * parameter <tt>resource</tt>.
	 * 
	 * @param resource
	 *          resource, id of which will be used to create a resource hash id.
	 * @return the resource hash id created using the the given <tt>resource</tt>.
	 * 
	 * @throws NullPointerException
	 *           if the parameter <tt>resource</tt> is null.
	 */
	public static String getHash(Resource resource) {
		return RES_ID + resource.getResId();
	}

	/**
	 * Constructs a communication resource hash id from the passed parameter <tt>commId</tt>.
	 * 
	 * @param commId
	 *          communication resource id to be used to create a communication resource hash id.
	 * @return the communication resource hash id created using the the given <tt>commId</tt>.
	 * 
	 */
	public static String getCResHash(int commId) {
		return COMM_ID + commId;
	}

	/**
	 * Constructs a communication resource hash id from the passed parameter <tt>commResource</tt>, which defines the
	 * communication resource.
	 * 
	 * Necessary information to create a communication resource hash id is the communication resource id and this must be
	 * available in the passed parameter <tt>commResource</tt>.
	 * 
	 * @param commResource
	 *          communication resource, id of which will be used to create a resource hash id.
	 * @return the communication resource hash id created using the the given <tt>resource</tt>.
	 * 
	 * @throws NullPointerException
	 *           if the parameter <tt>commResource</tt> is null.
	 */
	public static String getHash(CommResource commResource) {
		return COMM_ID + commResource.getResId();
	}

	/**
	 * Constructs a constraint hash id from the passed parameters application id, start task id and end task id.
	 * 
	 * @param appId
	 *          application id to be used to create the constraint hash id.
	 * @param startTaskId
	 *          start task id to be used to create the constraint hash id.
	 * @param endTaskId
	 *          end task id to be used to create the constraint hash id.
	 * @return the constraint hash id constructed from the parameters application id, start task id and end task id.
	 */
	public static String getLatConstrHash(int appId, int startTaskId, int endTaskId) {
		return LAT + appId + startTaskId + endTaskId;
	}

	/**
	 * Getter method to get the hash id of the given constraint.
	 * 
	 * @param constraint
	 *          hash of which will be returned to the client.
	 * @return the hash of the constraint.
	 */
	public static String getHash(SysLatencyConstraint constraint) {
		return constraint.getHash();
	}
}
