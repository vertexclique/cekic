package org.tubs.epoc.SMFF.Comparators;

import java.util.Comparator;

import org.tubs.epoc.SMFF.ModelElements.Application.Task;

/**
 * A comparator implementation which uses the class {@link org.tubs.epoc.SMFF.ModelElements.Application.Task Task} as a
 * type parameter to the {@link java.util.Comparator Comparator} interface.
 * 
 * The class {@link org.tubs.epoc.SMFF.ModelElements.Application.Task Task} contains task related information such as
 * the application id and task id of the application. {@link #compare(Task, Task) compare()} method will favor the
 * parameter with greater application id. If both of the tasks have the same application id, they will be compared with
 * their task ids. Two tasks which have the same application ids as well as the task ids are considered to be equal.
 * {@link #compare(Task, Task) compare()} will return 0 in this case. These are the possible outputs for
 * {@link #compare(Task, Task) compare()} method.
 * <ul>
 * <li>if <tt>task1.applicationid &gt task2.applicationid</tt> return 1.
 * <li>if <tt>task1.applicationid &lt task2.applicationid</tt> return -1.
 * <li>if <tt>task1.applicationid = task2.applicationid and task1.taskid &gt task2.taskid</tt> return 1.
 * <li>if <tt>task1.applicationid = task2.applicationid and task1.taskid &lt task2.taskid</tt> return -1.
 * <li>if <tt>task1.applicationid = task2.applicationid and task1.taskid = task2.taskid</tt> return 0.
 * </ul>
 * 
 * @see org.tubs.epoc.SMFF.ModelElements.Application.Task
 * 
 */
public class TaskIdComparator implements Comparator<Task> {

	/**
	 * Creates new comparator for Task IDs ascending order of 1. AppId, 2. TaskId
	 */
	public TaskIdComparator() {
	}

	/**
	 * Compares two tasks based on their AppId and TaskId
	 * 
	 * @param task1
	 *          First Element to compare
	 * @param task2
	 *          Second Element to compare
	 * @return 1 if <tt>task1</tt> has a greater application id, -1 if <tt>task2</tt> has a greater application id. If
	 *         both tasks have the same application id, they will be compared based upon their task-ids. If <tt>task1</tt>
	 *         has the same application id with <tt>task2</tt> but a greater task-id, return 1. If
	 *         <tt>task2.taskid &gt task1.taskid</tt> return -1. Return 0, if both tasks have the same application id and
	 *         task id.
	 * @throws java.lang.NullPointerException
	 *           if one of the parameters is null.
	 * @see org.tubs.epoc.SMFF.ModelElements.Application.Task
	 */
	public int compare(Task task1, Task task2) {
		// return appropriate value
		// first check whether only one is violated
		if (task1.getAppId() > task2.getAppId()) {
			return 1;
		} else if (task1.getAppId() < task2.getAppId()) {
			return -1;
		} else {
			if (task1.getElemId() > task2.getElemId()) {
				return 1;
			} else if (task1.getElemId() < task2.getElemId()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
