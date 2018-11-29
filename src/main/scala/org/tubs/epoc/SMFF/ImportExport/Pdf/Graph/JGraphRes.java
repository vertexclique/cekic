package org.tubs.epoc.SMFF.ImportExport.Pdf.Graph;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResourceData;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;

public class JGraphRes extends AbstractResourceData{
  DefaultGraphCell cell;

  public JGraphRes(Resource res){

    cell = new DefaultGraphCell();

    cell.add(new DefaultPort());
    GraphConstants.setAutoSize(cell.getAttributes(), false);
    GraphConstants.setBackground(cell.getAttributes(), Color.LIGHT_GRAY);
    Rectangle2D rect = new Rectangle2D.Double(0,0,200,100);
    GraphConstants.setBounds(cell.getAttributes(), rect);
    GraphConstants.setOpaque(cell.getAttributes(), true);
    //GraphConstants.setHorizontalTextPosition(cell.getAttributes(),JLabel.LEFT);
    //GraphConstants.setVerticalTextPosition(cell.getAttributes(), JLabel.TOP);
    GraphConstants.setInset(cell.getAttributes(), 20);
    GraphConstants.setGroupOpaque(cell.getAttributes(), true); 
    GraphConstants.setBorderColor(cell.getAttributes(), Color.BLACK);
    GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createTitledBorder(res.getUniqueName()));
    GraphConstants.setEditable(cell.getAttributes(), false);
  }

  public DefaultPort getPort(){
    return (DefaultPort) cell.getChildAt(0);
  }

  public DefaultGraphCell getCell() {
    return cell;
  }


}
