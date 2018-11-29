package org.tubs.epoc.SMFF.ImportExport.XML;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.tubs.epoc.SMFF.ModelElements.AbstractDataExtension;
import org.tubs.epoc.SMFF.ModelElements.ExtendibleModelElement;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Profile;
import org.tubs.epoc.SMFF.ModelElements.Application.SysLatencyConstraint;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceGroup;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceType;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;
import org.tubs.epoc.SMFF.ModelElements.Scheduling.AbstractSchedulingParameter;

/**
 * Generates an XML file from the given system model description.
 * <p>
 * Model description will be saved to the file which is passed to the constructor of this class. If
 * {@link #saveModel(SystemModel) saveModel()} called more than once with the same instance of this ModelSaver the
 * latest call will overwrite previous one.
 * 
 * @see SystemModel
 * 
 */
public class ModelSaver {
	private static Log logger = LogFactory.getLog(ModelSaver.class);

	private File file;

	public ModelSaver(String filename) {
		this.file = new File(filename);
	}
	
  public ModelSaver(File file) {
    this.file = file;
  }

	/**
	 * Saves the passed system model description to the file through which an instance of this ModelSaver is created.
	 * 
	 * @param systemModel model to be saved.
	 * @throws IOException if an output stream cannot be created to the file.
	 */
	public void saveModel(SystemModel systemModel) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(file);

		Element root = new Element("SystemModel");
		Element configurationSection = new Element("Configuration");
		// the following 'data extensions' are obligatory and therefore translated in the configuration section
		// to allow easier writing of system files by hand
		configurationSection.addContent(new Element("ResourceType"));
		configurationSection.addContent(new Element("ResourceGroup"));
		configurationSection.addContent(new Element("Scheduler"));
		configurationSection.addContent(new Element("SchedulingParameter"));
		configurationSection.addContent(new Element("ActivationPattern"));
		root.addContent(configurationSection);

		insertExtensions(root, systemModel);

		// insert platform
		insertPlatform(root, configurationSection, systemModel);
		// insert applications
		insertApplications(root, configurationSection, systemModel);

		Document doc = new Document(root);
		// serialize it onto System.out
		try {
			XMLOutputter serializer = new XMLOutputter();
			serializer.output(doc, fileOut);
		} catch (IOException e) {
			logger.error("Error during serializing the xml output", e);
		} finally{
		  fileOut.close();
		}

	}

	private void insertConstraints(Element appE, ApplicationModel app) throws NumberFormatException,
	IllegalStateException {
		// create constraints element
		Element constrElements = new Element("Constraints");

		// create constraint elements
		for (SysLatencyConstraint sysLatConstr : app.getConstraints().values()) {
			Element sysLatConstrElem = new Element("Constraint");
			sysLatConstrElem.setAttribute("StartTaskId", String.valueOf(sysLatConstr.getStartTask().getElemId()));
			sysLatConstrElem.setAttribute("EndTaskId", String.valueOf(sysLatConstr.getEndTask().getElemId()));
			sysLatConstrElem.setAttribute("Constraint", String.valueOf(sysLatConstr.getLatencyConstr()));

			// add constraint element to constraints
			constrElements.addContent(sysLatConstrElem);
		}

		// add constraints to application
		appE.addContent(constrElements);
	}

	/**
	 * inserts the XML description of all applications at root
	 * 
	 * @param root
	 *          root element (system model)
	 * @param configurationSection 
	 * @param systemModel
	 *          system model to extract application models from
	 */
	private void insertApplications(Element root, Element configurationSection, SystemModel systemModel) {
		Element apps = new Element("Applications");

		root.addContent(apps);

		for (ApplicationModel app : systemModel.getApplications()) {
			Element appElem = new Element("Application");
			appElem.setAttribute("appID", String.valueOf(app.getAppId()));
			appElem.setAttribute("appV", String.valueOf(app.getAppV()));

			// insert extensions of this app
			insertExtensions(appElem, app);

			apps.addContent(appElem);

			insertTasks(appElem, configurationSection, app);
			insertTaskLinks(appElem, configurationSection, app);
			insertMapping(appElem, app);
			insertConstraints(appElem, app);
		}
	}

	private void insertMapping(Element appE, ApplicationModel app) throws NumberFormatException, IllegalStateException {

		// create mapping element
		Element mappingElem = new Element("Mapping");

		// create mappings for all tasks
		for (Task task : app.getTaskList().values()) {
			Element mapElem = new Element("mapTask");
			mapElem.setAttribute("tid", String.valueOf(task.getElemId()));
			mapElem.setAttribute("rid", String.valueOf(task.getMappedTo().getResId()));
			mappingElem.addContent(mapElem);
		}

		// create mapping for all task links
		for (TaskLink taskLink : app.getTaskLinkList().values()) {
			Element mapElem = new Element("mapLink");
			mapElem.setAttribute("lid", String.valueOf(taskLink.getElemId()));
			if (taskLink.getMappedTo() instanceof Resource) {
				mapElem.setAttribute("rid", String.valueOf(taskLink.getMappedTo().getResId()));
			} else if (taskLink.getMappedTo() instanceof CommResource) {
				mapElem.setAttribute("crid", String.valueOf(taskLink.getMappedTo().getResId()));
			} else {
				throw new IllegalStateException("Task link mapped to neither Resource nor CommResource");
			}
			mappingElem.addContent(mapElem);
		}

		// add mapping to application
		appE.addContent(mappingElem);
	}

	/**
	 * @param appE
	 * @param app
	 * @throws NumberFormatException
	 * @throws IllegalStateException
	 */
	private void insertTaskLinks(Element appE, Element configurationSection, ApplicationModel app) throws NumberFormatException, IllegalStateException {

		// insert all task links of this application
		for (TaskLink taskLink : app.getTaskLinkList().values()) {
      // get relevant sections of configuration section
      Element schedulingParamConfiguration = configurationSection.getChild("SchedulingParameter");
      Element resTypeParamConfiguration = configurationSection.getChild("ResourceType");
      Element resGroupParamConfiguration = configurationSection.getChild("ResourceGroup");
      Element activationConfiguration = configurationSection.getChild("ActivationPattern");
			// add task and all its parameters
			Element taskLinkElem = new Element("TaskLink");
			taskLinkElem.setAttribute("ID", String.valueOf(taskLink.getElemId()));
			taskLinkElem.setAttribute("src", String.valueOf(taskLink.getSrcTaskId()));
			taskLinkElem.setAttribute("trgt", String.valueOf(taskLink.getTrgTaskId()));


      // add all profiles for this possible resource
      for (Profile profile : taskLink.getProfileList()) {
        Element profileElem = new Element("Profile");
        profileElem.setAttribute("bcet", String.valueOf(profile.getBCET()));
        profileElem.setAttribute("wcet", String.valueOf(profile.getWCET()));
        Element activationXML = profile.getActivationPattern().toXML();
        // take out the classname description and put it into the defaultClassMap instead
        updateConfiguration(activationConfiguration, activationXML);
        if(taskLink.getActiveProfile()==profile){
          profileElem.setAttribute("active", String.valueOf(true));
        } else{
          profileElem.setAttribute("inactive", String.valueOf(false));
        }
        profileElem.addContent(activationXML);
        AbstractResourceGroup resGroup = profile.getResGroup();
        Element resGroupXML = resGroup.toXML();
        AbstractResourceType resType = profile.getResType();
        Element resTypeXML = resType.toXML();
        // update resGroup and resType from configuration section
        updateConfiguration(resGroupParamConfiguration, resGroupXML);
        updateConfiguration(resTypeParamConfiguration, resTypeXML);

        profileElem.addContent(new Element("ResourceType").addContent(resTypeXML));
        profileElem.addContent(new Element("ResourceGroup").addContent(resGroupXML));
        taskLinkElem.addContent(profileElem);
      }

      AbstractSchedulingParameter schedParam = taskLink.getSchedulingParameter();
      if(schedParam != null){
        Element schedulingParamXML = schedParam.toXML();
			// take out the classname description and put it into the defaultClassMap instead
			updateConfiguration(schedulingParamConfiguration, schedulingParamXML);
			taskLinkElem.addContent(schedulingParamXML);
      }

			if (taskLink.getShortName() != "") {
				taskLinkElem.setAttribute("shortName", taskLink.getShortName());
			}

			// insert extensions of this app
			insertExtensions(taskLinkElem, taskLink);

			// add task link to application
			appE.addContent(taskLinkElem);
		}
	}

	private void insertTasks(Element appE, Element configurationSection, ApplicationModel app) {
		// insert all tasks of this application
		for (Task task : app.getTaskList().values()) {
			// add task and all its parameters
			Element taskElem = new Element("Task");
			taskElem.setAttribute("ID", String.valueOf(task.getElemId()));
			// take out the classname description and put it into the defaultClassMap instead
			Element schedulingParamConfiguration = configurationSection.getChild("SchedulingParameter");
      Element resTypeParamConfiguration = configurationSection.getChild("ResourceType");
      Element resGroupParamConfiguration = configurationSection.getChild("ResourceGroup");
      Element activationConfiguration = configurationSection.getChild("ActivationPattern");

      // write scheduling parameter
      AbstractSchedulingParameter schedulingParameter = task.getSchedulingParameter();
      if(schedulingParameter!=null){
        Element schedulingParamXML = schedulingParameter.toXML();
			updateConfiguration(schedulingParamConfiguration, schedulingParamXML);
			taskElem.addContent(schedulingParamXML);
      }

			if (task.getShortName() != "") {
				taskElem.setAttribute("shortName", task.getShortName());
			}

				// add all profiles for this possible resource
      for (Profile profile : task.getProfileList()) {
					Element profileElem = new Element("Profile");
					profileElem.setAttribute("bcet", String.valueOf(profile.getBCET()));
					profileElem.setAttribute("wcet", String.valueOf(profile.getWCET()));
					Element activationXML = profile.getActivationPattern().toXML();
					// take out the classname description and put it into the defaultClassMap instead
					updateConfiguration(activationConfiguration, activationXML);
					if(task.getActiveProfile()==profile){
					  profileElem.setAttribute("active", String.valueOf(true));
					} else{
          profileElem.setAttribute("inactive", String.valueOf(false));
					}
					profileElem.addContent(activationXML);
        AbstractResourceGroup resGroup = profile.getResGroup();
        Element resGroupXML = resGroup.toXML();
        AbstractResourceType resType = profile.getResType();
        Element resTypeXML = resType.toXML();
        // update resGroup and resType from configuration section
        updateConfiguration(resGroupParamConfiguration, resGroupXML);
        updateConfiguration(resTypeParamConfiguration, resTypeXML);

        profileElem.addContent(new Element("ResourceType").addContent(resTypeXML));
        profileElem.addContent(new Element("ResourceGroup").addContent(resGroupXML));
        taskElem.addContent(profileElem);
			}

			// insert extensions of this app
			insertExtensions(taskElem, task);

			appE.addContent(taskElem);
		}
	}

	private void insertPlatform(Element root, Element configurationSection, SystemModel systemModel) {
		Element platformElement = new Element("Platform");

		// add all resources to platform
		for (AbstractResource resource : systemModel.getAllRes()) {
			Element resourceElem;

			if (resource instanceof Resource) {
				resourceElem = new Element("Resource");

				// for resources get all neighbors (comm resources) and create attachedTo elements)
				for (AbstractResource attachedTo : resource.getNeighbors()) {
					Element attachedToElem = new Element("attachedTo");
					attachedToElem.setAttribute("ID", String.valueOf(attachedTo.getResId()));

					// add the attachedTo element to the resource
					resourceElem.addContent(attachedToElem);
				}
			} else if (resource instanceof CommResource) {
				resourceElem = new Element("CommResource");
			} else {
				throw new IllegalStateException();
			}
			resourceElem.setAttribute("resID", String.valueOf(resource.getResId()));
			Element resTypeXML = resource.getResType().toXML();
			Element resGroupXML = resource.getResGroup().toXML();
			Element schedulerXML = resource.getScheduler().toXML();

			// take out the classname description and put it into the defaultClassMap instead
			Element resTypeConfiguration = configurationSection.getChild("ResourceType");
			updateConfiguration(resTypeConfiguration, resTypeXML);
			Element resGroupConfiguration = configurationSection.getChild("ResourceGroup");
			updateConfiguration(resGroupConfiguration, resGroupXML);
			Element schedulerConfiguration = configurationSection.getChild("Scheduler");
			updateConfiguration(schedulerConfiguration, schedulerXML);

			resourceElem.addContent(resTypeXML);
			resourceElem.addContent(resGroupXML);
			resourceElem.addContent(schedulerXML);

			if (resource.getShortName() != "") {
				resourceElem.setAttribute("shortName", resource.getShortName());
			}

			// add all extensions for this resourceElem
			insertExtensions(resourceElem, resource);

			// add resource to platform
			platformElement.addContent(resourceElem);
		}

		// add platform to root
		root.addContent(platformElement);
	}

	/**
	 * checks whether the classname of the xml element is already configured in the configuraiton section
	 * Three cases that are handled
	 * 1. classname not configured yet -> add classname to configuration and remove from xmlElement
	 * 2. classname configured and identical to the one of the xmlElement -> remove from xmlElement
	 * 3. classname configured but different -> dont touch the xmlElement
	 * @param configurationSection
	 * @param xmlElement
	 */
	private void updateConfiguration(Element configurationSection, Element xmlElement) {
		String xmlElementName = xmlElement.getAttributeValue("name");
		Element configuredName = configurationSection.getChild(xmlElementName);
		// if no configuration for this name exists yet, add it and remove the attribute
		if(configuredName == null){
			Element newConfiguration = new Element(xmlElementName).setAttribute("classname", xmlElement.getAttributeValue("classname"));
			configurationSection.addContent(newConfiguration);
			xmlElement.removeAttribute("classname");
		}
		// if the name already exists, check whether the mapping is identical. If so remove the attribute
		else if(configuredName.getAttributeValue("classname").equals(xmlElement.getAttributeValue("classname"))){
			xmlElement.removeAttribute("classname");
		}
	}

	/**
	 * adds all data extensions that implement XMLSaveable to the specified rootXML
	 * 
	 * @param rootXML
	 * @param rootModel
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void insertExtensions(Element rootXML, ExtendibleModelElement rootModel) {
		// add an extension element to the rootXML
		Element dataExtensionsXML = new Element("DataExtensions");

		// get a collection of all data extensions of this model element
		Collection<AbstractDataExtension> dataExtensions = rootModel.getExtDataByParentClass(AbstractDataExtension.class);
		// iterate over the list
		for (AbstractDataExtension dataExt : dataExtensions) {
			// check whether this extensions is XMLSaveable
			if (XMLSaveable.class.isInstance(dataExt)) {
				// if so add it to the rootXML data extension section
				Element xmlDataExt = ((XMLSaveable) dataExt).toXML();
				xmlDataExt.setAttribute("isCloneable", String.valueOf(((XMLSaveable) dataExt).isCloneable()));
				xmlDataExt.setAttribute("isIgnoreExisting", String.valueOf(((XMLSaveable) dataExt).isIgnoreExisiting()));
				xmlDataExt.setAttribute("isOverwrite", String.valueOf(((XMLSaveable) dataExt).isOverwrite()));
				dataExtensionsXML.addContent(xmlDataExt);
			}
		}

		// if this element has any savable extensions, save the extension list to the rootXML
		if (dataExtensionsXML.getChildren().size() > 0) {
			rootXML.addContent(dataExtensionsXML);
		}
	}
}
