package org.tubs.epoc.SMFF.ImportExport.Pdf.Graph;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.tubs.epoc.SMFF.ModelElements.Application.AbstractSchedElemData;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;

public class JGraphTask extends AbstractSchedElemData{
	DefaultGraphCell cell;
	
	public JGraphTask(Task task){
		cell = new DefaultGraphCell(task.getUniqueName());
		
		cell.add(new DefaultPort());
	    // TODO: set all kinds of graphing options here
	    GraphConstants.setAutoSize(cell.getAttributes(), true);
      GraphConstants.setGradientColor(cell.getAttributes(), GraphFormating.CreateGraphCellColor(task.getAppId()));
//	    GraphConstants.setGradientColor(cell.getAttributes(), GraphFormating.CreateGraphCellColor(SystemModel.getApplication(applicationID).getAppId()));
	    GraphConstants.setOpaque(cell.getAttributes(), true);
	    GraphConstants.setBorderColor(cell.getAttributes(), Color.BLACK);
	    GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(0,0,0,0));
	    GraphConstants.setInset(cell.getAttributes(), 5);
	    GraphConstants.setEditable(cell.getAttributes(), false);
	}
	

	public DefaultPort getPort(){
		return (DefaultPort) cell.getChildAt(0);
	}

	public DefaultGraphCell getCell() {
		return cell;
	}
}
