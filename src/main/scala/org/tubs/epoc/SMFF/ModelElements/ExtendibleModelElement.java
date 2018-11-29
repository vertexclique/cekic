package org.tubs.epoc.SMFF.ModelElements;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class to be extended by the model elements which want to add additional data to its data objects.
 * 
 * This class also includes utility methods to extract type related information of the class.
 *
 */
public abstract class ExtendibleModelElement<DataExtensionType extends AbstractDataExtension> implements Cloneable {
	private static Log logger = LogFactory.getLog(ExtendibleModelElement.class);

	// additional data to be attached by other classes using this data structure
	protected HashMap<Class<? extends DataExtensionType>, LinkedList<DataExtensionType>> dataObjects = new HashMap<Class<? extends DataExtensionType>, LinkedList<DataExtensionType>>();
	protected Set<DataExtensionType> cloneDataObjects = new HashSet<DataExtensionType>();

	/**
	 * @return the type parameter of this extendible model element
	 */
	public Type getDataExtensionType() {
		return getTypeArguments(ExtendibleModelElement.class, getClass()).get(0);
	}

	/**
	 * This is a utility method for getDataExtensionType Get the underlying class for a type, or null if the type is a
	 * variable type.
	 * 
	 * @param type
	 *          the type
	 * @return the underlying class
	 */
	private static Class<?> getClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * This is a utility method for getDataExtensionType Get the actual type arguments a child class has used to extend a
	 * generic base class.
	 * 
	 * @param baseClass
	 *          the base class
	 * @param childClass
	 *          the child class
	 * @return a list of the raw classes for the actual type arguments.
	 */
	private static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
		Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just keep going.
				type = ((Class) type).getGenericSuperclass();
			} else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class) parameterizedType.getRawType();

				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}

		// finally, for each actual type argument provided to baseClass, determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}

	/**
	 * Clones an instance of the object and returns it.
	 * 
	 * @return the object that is cloned.
	 */
	@Override
	public Object clone() {
		ExtendibleModelElement<DataExtensionType> clone = null;
		try {
			// get new instance of the calling class
			clone = (ExtendibleModelElement<DataExtensionType>) super.clone();
			// create new Hashmap for data extensions
			clone.dataObjects = new HashMap<Class<? extends DataExtensionType>, LinkedList<DataExtensionType>>();

			// copy all data extension to be cloned
			for (DataExtensionType cloneDataExt : cloneDataObjects) {
				clone.addExtData((DataExtensionType) cloneDataExt.clone(), true);
			}
		} catch (CloneNotSupportedException e) {
			logger.error("Error during cloning the object.", e);
		}
		return clone;
	}

	/**
	 * Copies all cloneable data extensions to the specified element.
	 * 
	 * @param modelElem
	 *          object which will store the copied data extensions.
	 */
	@SuppressWarnings("unchecked")
	public void copyDataExtTo(ExtendibleModelElement<DataExtensionType> modelElem) {
		// copy all data extension to be cloned
		for (DataExtensionType cloneDataExt : this.cloneDataObjects) {
			modelElem.addExtData((DataExtensionType) cloneDataExt.clone(), true);
		}
	}

	/**
	 * Adds a data extension object to this task.
	 * 
	 * @param data
	 *          the object to add
	 * @param clone
	 *          clone this object to LocalApplicationModel if true
	 * 
	 * @throws IllegalArgumentException
	 *           if an object with the same identifier is already attached to this task
	 */
	public void addExtData(DataExtensionType data, boolean clone) throws IllegalArgumentException {
		this.addExtData(data, clone, false, false);
	}

	/**
	 * Adds a data object to this task.
	 * 
	 * @param data
	 *          the object to add
	 * @param clone
	 *          clone this object to LocalApplicationModel if true
	 * @param overwrite
	 *          overwrite any existing data objects of this type if any exist
	 * @param ignoreExisting
	 *          ignore this call if an object of this type already exists
	 * 
	 * @throws IllegalArgumentException
	 *           if an object with the same identifier is already attached to this task
	 */
	public void addExtData(DataExtensionType data, boolean clone, boolean overwrite, boolean ignoreExisting)
	    throws IllegalArgumentException {
		// if no list for this type exists, create it and get the list
		if (!dataObjects.containsKey(data.getClass())) {
			dataObjects.put((Class<? extends DataExtensionType>) data.getClass(), new LinkedList<DataExtensionType>());
		}
		LinkedList<DataExtensionType> list = dataObjects.get(data.getClass());

		// if call is ignored if an object of this type exists, return here
		if (ignoreExisting && list.size() > 0) {
			return;
		}
		// if existing object of this types should be overwritten, clear the list
		if (overwrite) {
			list.clear();
		}

		// associate with the data object
		data.setParent(this);
		// add to internal storage
		list.add(data);

		// if this element should be cloned
		if (clone) {
			cloneDataObjects.add(data);
		}
		return;
	}

	/**
	 * Removes all data objects of the exact class clazz from this task.
	 * 
	 * @param clazz
	 *          class of objects to remove
	 * @return list of removed objects, null if key was not found
	 */
	public LinkedList<DataExtensionType> clearExtDataByClass(Class<? extends DataExtensionType> clazz) {
		// remove all elements of the class clazz from the cloneDataObjects list
		for (DataExtensionType cloneable : cloneDataObjects) {
			if (cloneable.getClass() == clazz) {
				cloneDataObjects.remove(cloneable);
			}
		}

		// remove the collection of this type from the data objects list
		return dataObjects.remove(clazz);
	}

	/**
	 * Removes all data objects of the classes that inherit from clazz from this task.
	 * 
	 * @param clazz
	 *          class of objects to remove
	 * @return list of removed objects, null if key was not found
	 */
	public LinkedList<DataExtensionType> clearExtDataByParentClass(Class<? extends DataExtensionType> clazz) {
		// remove all elements of the class clazz from the cloneDataObjects list
		for (DataExtensionType cloneable : cloneDataObjects) {
			if (clazz.isAssignableFrom(cloneable.getClass())) {
				cloneDataObjects.remove(cloneable);
			}
		}

		// create new list for the return value
		LinkedList<DataExtensionType> returnList = new LinkedList<DataExtensionType>();

		// go through all keys
		for (Class<? extends AbstractDataExtension> dataClass : dataObjects.keySet()) {
			// if the key is inheriting from clazz remove it from the data objects
			if (clazz.isAssignableFrom(dataClass)) {
				// remove the collection and add it to the return list
				returnList.addAll(dataObjects.remove(dataClass));
			}
		}

		// return the list of removed objects
		return returnList;
	}

	/**
	 * Returns the data object with the given class if associated with this task. If there isn't any associated data
	 * objects with this clazz, it will return null.
	 * 
	 * @param clazz
	 *          class of data extension elements
	 * @return list of data objects of the specified class clazz, null if there is no data objects associated with clazz.
	 */
	public LinkedList<DataExtensionType> getExtDataByClass(Class<? extends DataExtensionType> clazz) {
		// simply retrieve from dataObjects
		return dataObjects.get(clazz);
	}

	/**
	 * Returns a single data object with the given class if associated with this task.
	 * 
	 * It will return null, if there isn't any data objects associated with this class.
	 * 
	 * @param clazz
	 *          class of data extension elements
	 * @return data object of the specified class clazz (first one if multiple exist, null if there isn't any)
	 */
	public DataExtensionType getSingleExtDataByClass(Class<? extends DataExtensionType> clazz) {
		if (dataObjects.get(clazz) != null && !dataObjects.get(clazz).isEmpty()) {
			return dataObjects.get(clazz).getFirst();
		} else {
			return null;
		}
	}

	/**
	 * Returns a single data object that extends the given class if associated with this task.
	 * 
	 * It will return null, if there isn't any.
	 * 
	 * @param clazz
	 *          class of data extension elements
	 * @return data object of the specified class clazz (first one if multiple exist, null otherwise)
	 */
	public DataExtensionType getSingleExtDataByParentClass(Class<? extends DataExtensionType> clazz) {
		for (Class<? extends AbstractDataExtension> dataClass : dataObjects.keySet()) {
			if (clazz.isAssignableFrom(dataClass)) {
				return dataObjects.get(dataClass).getFirst();
			}
		}
		return null;
	}

	/**
	 * Returns the data object with the given class if associated with this task.
	 * 
	 * @param clazz
	 *          class of data extension elements
	 * @return list of data objects of the specified class clazz
	 */
	public LinkedList<DataExtensionType> getExtDataByParentClass(Class<? extends DataExtensionType> clazz) {
		// return list
		LinkedList<DataExtensionType> returnList = new LinkedList<DataExtensionType>();

		// go through all extensions and check whether they inherit from clazz
		for (Class<? extends AbstractDataExtension> dataClass : dataObjects.keySet()) {
			if (clazz.isAssignableFrom(dataClass)) {
				// get the collection and add it to the return list
				returnList.addAll(dataObjects.get(dataClass));
			}
		}
		return returnList;
	}
}
