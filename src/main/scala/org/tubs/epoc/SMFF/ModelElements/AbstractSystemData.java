package org.tubs.epoc.SMFF.ModelElements;

/**
 * 
 * A container for data that can be attached to system model classes that need to store additional data with a system
 * model object which can be inherited from this class and add the object to the system model.
 * 
 * Lookup will be done by the identifier. This must be unique.
 * 
 * @author steffens
 * @see SystemModel
 * 
 */
public abstract class AbstractSystemData extends AbstractDataExtension<SystemModel> {
}
