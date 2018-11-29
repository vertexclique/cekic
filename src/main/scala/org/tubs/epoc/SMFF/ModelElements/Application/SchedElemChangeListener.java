package org.tubs.epoc.SMFF.ModelElements.Application;

/**
 * Interface description for a task change listener. 
 *
 */
public interface SchedElemChangeListener {
	/**
	 * Register the task event that will be executed in case of task changes. 
	 * @param e the event that will be executed in case of task changes
	 */
	public void taskDescriptionChanged(SchedElemChangeEvent e);
}
