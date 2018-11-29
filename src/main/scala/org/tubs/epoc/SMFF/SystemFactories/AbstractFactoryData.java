package org.tubs.epoc.SMFF.SystemFactories;

import org.jdom.Element;

/**
 * Abstract class to manipulate the factory.
 *
 */
public abstract class AbstractFactoryData {
  
  /**
   * Abstract method to write the factory data to XML.
   * @return the data as XML element.
   */
  public abstract Element toXML();
}
