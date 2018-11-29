package org.tubs.epoc.SMFF.ImportExport.XML;

import org.jdom2.Element;

/**
 * Interface to support saving to XML and loading from XML.
 * <p>
 * Besides the declared method, classes implementing this interface must - declare a constructor accepting a jdom
 * Element as single parameter - attach an attribute to the root xml element containing the qualified name of the class
 * and has the attribute name "classname".
 * 
 * @author moritzn
 * 
 */
public interface XMLSaveable {
	/**
	 * Creates a jdom element representation of the given XMLSaveable instance.
	 * 
	 * @return a jdom element representing this object
	 */
	public Element toXML();

	/**
	 * Decides whether the given instance is cloneable.
	 * 
	 * @return true, if this extension should be cloned to local versions
	 */
	public boolean isCloneable();

	/**
	 * Decides if the existing extensions of same type must be overwritten.
	 * 
	 * @return true, if existing extensions of same type should be overwritten
	 */
	public boolean isOverwrite();

	/**
	 * Decides if the instance should be added to data extensions in case the extensions already exist.
	 * 
	 * @return true, if it should not be added to data extensions if the extensions exists already
	 */
	public boolean isIgnoreExisiting();
}
