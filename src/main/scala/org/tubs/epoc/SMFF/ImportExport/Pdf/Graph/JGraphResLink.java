package org.tubs.epoc.SMFF.ImportExport.Pdf.Graph;

import java.awt.Color;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.util.ParallelEdgeRouter;

public class JGraphResLink extends DefaultEdge {
	
	public JGraphResLink(DefaultPort src, DefaultPort trg){
		this.setSource(src);
		this.setTarget(trg);
		// setting up layout of edge
	    GraphConstants.setLineColor(this.getAttributes(), Color.BLACK);
	    GraphConstants.setLineBegin(this.getAttributes(), GraphConstants.ARROW_NONE);
	    GraphConstants.setLineEnd(this.getAttributes(), GraphConstants.ARROW_NONE);
	    GraphConstants.setRouting(this.getAttributes(), ParallelEdgeRouter.getSharedInstance());
	}
}
