package org.tubs.epoc.SMFF.ModelElements.Platform;

import org.tubs.epoc.SMFF.ModelElements.AbstractDataExtension;

/**
 * A container for data that can be attached to a resource.
 * Classes, that need to store additional data with a resource
 * object, can inherit from this class and add the object to the resource.
 * 
 * Lookup will be done by the identifier. This must be unique.
 * 
 * @author steffens
 *
 */
public abstract class AbstractResourceData extends AbstractDataExtension<AbstractResource>{
}
