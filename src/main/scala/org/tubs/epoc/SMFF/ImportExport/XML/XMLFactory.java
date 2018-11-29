package org.tubs.epoc.SMFF.ImportExport.XML;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.jdom2.Element;

/**
 * This class provides a factory to create data extension elements from an XML description. The xml elements need to
 * have an attribute "classname" specifiying the qualified name of the class of the object to be created. Furthermore
 * that class needs to implement XMLSaveable and requires a constructor accepting a jdom Element as single parameter. If
 * any of these prerequisites are not met an exception is thrown.
 * 
 * @author moritzn
 * @see XMLSaveable
 * 
 */
public class XMLFactory {

	/**
	 * Instantiates an implementation of {@link XMLSaveable XMLSaveable} and returns it.
	 * <p>
	 * The xml element description to be passed to this parameter will contain an attribute called "classname", value of
	 * which must be an instance of interface {@link XMLSaveable XMLSaveable}. This method will instantiate an
	 * implementation of {@link XMLSaveable XMLSaveable} interface and return it. The implementation must include a
	 * constructor which has a single parameter of type {@link org.jdom2.Element org.jdom2.Element}.
	 * 
	 * @param element
	 *          jdom element from which the class information to be instantiated will be extracted.
	 * @return an instance of class {@link XMLSaveable XMLSaveable}.
	 * @throws Exception
	 *           if the xml <tt>element</tt> doesn't have an attribute called "classname" or the value of this attribute
	 *           is not an instance of {@link XMLSaveable XMLSaveable}.
	 * @see XMLSaveable
	 */
	@SuppressWarnings("unchecked")
	public static XMLSaveable fromXML(Element element, HashMap<String, HashMap<String, Class<?>>> defaultClassMap) throws Exception {
	  Class<?> clazz;
		// get the class name
		String classname = element.getAttributeValue("classname");
		if(classname == null && defaultClassMap == null){
      throw new Exception("No attribute \"classname\" found.");
		}
		if (classname == null) {
		  // if no classname is explicitly given check the default class map for a mapping of the name field
		  String elementGroupName = element.getName();
		  String name = element.getAttributeValue("name");
		  HashMap<String, Class<?>> classGroupDefaultMap = defaultClassMap.get(elementGroupName);
      if(classGroupDefaultMap == null || classGroupDefaultMap.get(name)== null){
		    throw new Exception("Failed to resolve \"classname\" from defaultClassMap and \"classname\" not explicitly specified.");
		  }
      clazz = classGroupDefaultMap.get(name);
      
		}
		else{
		  // get the class
		  clazz = Class.forName(classname);
		}
		if (!(XMLSaveable.class.isAssignableFrom(clazz))) {
			throw new Exception("Specified classname is no instance of \"XMLSaveable\".");
		}

		// get the construcotr for the found class
		Constructor<? extends XMLSaveable> con;
		con = ((Class<? extends XMLSaveable>) clazz).getConstructor(new Class[] { Element.class });

		// create the new XML savable from the found constructor
		return (XMLSaveable) con.newInstance(element);
	}
	
	public static XMLSaveable fromXML(Element element) throws Exception {
	  return fromXML(element, null);
	}
}
