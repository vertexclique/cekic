package org.tubs.epoc.SMFF.ModelElements;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * An abstract class for data extensions to model elements.
 * 
 * @author steffens
 * @see ExtendibleModelElement
 * 
 */
public abstract class AbstractDataExtension<ParentClass extends ExtendibleModelElement> implements Cloneable {
	private static Log logger = LogFactory.getLog(AbstractDataExtension.class);
	// the resource this data belongs to
	protected ParentClass parent;

	/**
	 * Sets the parent this data object belongs to
	 * 
	 * @param parent
	 *          the model element to associate
	 */
	public void setParent(ParentClass parent) {
		this.parent = parent;
	}

	/**
	 * returns the parent of this data extension element
	 * 
	 * @return parent element
	 */
	public ParentClass getParent() {
		return this.parent;
	}

	/**
	 * returns a clone of this object
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("Error during cloning the object.", e);
		}
		return null;
	}
}
