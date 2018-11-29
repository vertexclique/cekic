package org.tubs.epoc.SMFF.ImportExport.Pdf.Graph;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;

public class Presentation {
	SystemModel systemModel;
  
	// graph to hold model and view
	JGraph graph;
	DefaultGraphModel graphModel;
	GraphLayoutCache view;
	
	// System graph
	SystemJGraph systemGraph;
	
	public Presentation(SystemModel systemModel){
	  // cache current system model
		this.systemModel = systemModel;
				
		// generate jGraph model
		this.systemGraph = new SystemJGraph(systemModel);
	}
	
	public SystemJGraph getSystemGraph(){
		return systemGraph;
	}
}
