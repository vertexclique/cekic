package org.tubs.epoc.SMFF.ImportExport.Pdf.Graph;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.BorderFactory;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceData;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;

public class JGraphCRes extends AbstractResourceData{
	
	DefaultGraphCell cell;
	
	DefaultPort taskLinkPort;
	
	Random rnd = new Random();
	
	JGraphCRes(CommResource cRes){		
		cell = new DefaultGraphCell();
		cell.add(new DefaultPort());

		GraphConstants.setAutoSize(cell.getAttributes(), false);
	    GraphConstants.setBackground(cell.getAttributes(), Color.GRAY);
	    GraphConstants.setBorderColor(cell.getAttributes(), Color.BLACK);
	    GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(0,0,150,50));
	    GraphConstants.setOpaque(cell.getAttributes(), true);
	    GraphConstants.setGroupOpaque(cell.getAttributes(), true);
	    GraphConstants.setInset(cell.getAttributes(), 20);
	    GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createTitledBorder(cRes.getUniqueName()));
	    GraphConstants.setEditable(cell.getAttributes(), false);
	}

	/**
	 * adds a new port for a TaskLink
	 * @param port
	 */
	public void addTaskLinkPort(DefaultPort port){
		DefaultPort taskLinkPort = port;		
		cell.add(taskLinkPort);
	}
	
	
	public DefaultPort getPort() {
		return (DefaultPort) cell.getChildAt(0);
	}

	public DefaultGraphCell getCell() {
		return cell;
	}
	
	
	
	
}
