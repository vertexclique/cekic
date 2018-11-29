package org.tubs.epoc.SMFF.ModelElements.Application;

import java.util.ArrayList;

import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class maps tasks that are defined as schedulable elements to resources (key=task, value=resource).
 */
public class ResourceMap implements Cloneable {
	private static Log logger = LogFactory.getLog(ResourceMap.class);

	private ArrayList<SchedElemIdentifier> keys;
	private ArrayList<AbstractResource> values;

	/**
	 * Default constructor for necessary initializations.
	 */
	public ResourceMap() {
		keys = new ArrayList<SchedElemIdentifier>();
		values = new ArrayList<AbstractResource>();
	}

	/**
	 * Returns clone of this Map.
	 * 
	 * @return the cloned instance.
	 * 
	 * @see Object#clone()
	 * */
	@Override
	public ResourceMap clone() {
		ResourceMap clone = new ResourceMap();
		this.mergeTo(clone);
		return clone;
	}

	/**
	 * Returns the number of mappings in this resource map.
	 * 
	 * @return the number of mappings in this resource map
	 */
	public int size() {
		return keys.size();
	}

	/**
	 * Checks whether the map is empty.
	 * 
	 * @return <tt>true</tt> if this map is empty, <tt>false</tt> otherwise.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Checks whether the passed parameter is in this resource map as a key.
	 * 
	 * @param k
	 *          scheduled element identifier to be whether it is included in this map.
	 * @return <tt>true</tt> if parameter <tt>k</tt> is contained as a key in this map, <tt>false</tt> otherwise.
	 */
	public boolean containsKey(SchedElemIdentifier k) {
		return keys.contains(k);
	}

	/**
	 * Checks whether the passed parameter is in this resource map as a value.
	 * 
	 * @param v
	 *          resource to be checked whether it is contained in this map as a value.
	 * @return <tt>true</tt> if resource <tt>v</tt> is contained as a value in this map, <tt>false</tt> otherwise.
	 */
	public boolean containsValue(AbstractResource v) {
		return values.contains(v);
	}

	/**
	 * Gets the object value corresponding to the key passed as parameter. If the task is not mapped this method will
	 * return null.
	 * 
	 * @param k
	 *          identifier for the scheduled element (task) to be searched for a corresponding resource value
	 * @return the object value corresponding to the key <tt>k</tt>, null if the key is not mapped.
	 */
	public AbstractResource get(SchedElemIdentifier k) {
		int i = keys.indexOf(k);
		if (i == -1)
			return null;
		return values.get(i);
	}

	/**
	 * Get the object key at position i
	 * 
	 * @param i
	 *          index of key to get
	 * @return the key at index i
	 */
	private SchedElemIdentifier getKey(int i) {
		if (i < 0 || i >= keys.size())
			return null;
		else {
			return keys.get(i);
		}
	}

	/**
	 * Get the object value at position i
	 * 
	 * @param i
	 *          index of value to get
	 * @return the value at index i
	 */
	private AbstractResource getValue(int i) {
		if (i < 0 || i >= values.size())
			return null;
		else {
			return values.get(i);
		}
	}

	/**
	 * Put the given pair (k, v) into this map, by maintaining "keys" in sorted order. Returns old mapped value of the key
	 * <tt>k</tt>, null if id doesn't not exist before.
	 * 
	 * @param k
	 *          key (task) for the task, resource pair
	 * @param v
	 *          value (resource) for the task, resource pair
	 * @return the old mapping of the task <tt>k</tt> if there is one, <tt>null</tt> otherwise.
	 */
	public AbstractResource put(SchedElemIdentifier k, AbstractResource v) {
		for (int i = 0; i < keys.size(); i++) {
			AbstractResource old = values.get(i);

			/* Does the key already exist? */
			// overwrite key (just in case anything changed for the task)
			if (keys.get(i).getElemId() == k.getElemId()) {
				keys.set(i, k);
				values.set(i, v);
				return old;
			}

			/*
			 * Did we just go past where to put it? i.e., keep keys in sorted order.
			 */
			if (keys.get(i).getElemId() >= k.getElemId()) {
				keys.add(i, k);
				values.add(i, v);
				return null;
			}
		}

		// Else it goes at the end.
		keys.add(k);
		values.add(v);
		return null;
	}

	/**
	 * Removes the given task and its resource value from this mapping.
	 * 
	 * @param k
	 *          identifier for the key to be removed.
	 * @return the value of the deleted task, resource pair.
	 */
	public AbstractResource remove(SchedElemIdentifier k) {
		int i = keys.indexOf(k);
		if (i == -1)
			return null;
		AbstractResource old = values.get(i);
		keys.remove(i);
		values.remove(i);
		return old;
	}

	/**
	 * Copy all entries of this ResourceMap to the parameter <tt>mergeToMap</tt>.
	 * <p>
	 * This function overwrites duplicate entries in <tt>mergeToMap</tt>.
	 * 
	 * @param mergeToMap
	 *          AbstractResourceMap to copy entries to
	 * @throws NullPointerException
	 *           if passed parameter <tt>mergeToMap</tt> is null.
	 */
	public void mergeTo(ResourceMap mergeToMap) {
		if (mergeToMap == null) {
			logger.error("Error during merging the map. MergeToMap not found.");
			throw new NullPointerException("MergeToMap not found.\n");
		}

		SchedElemIdentifier Task;
		AbstractResource abstractResource;
		for (int i = 0; i < this.size(); i++) {
			Task = this.getKey(i);
			abstractResource = this.getValue(i);
			mergeToMap.put(Task, abstractResource);
		}
	}

	/**
	 * Clears all the keys and values of this resource map.
	 */
	public void clear() {
		keys.clear();
		values.clear();
	}
}
