package org.tubs.epoc.SMFF.ImportExport.Pdf.Graph;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.util.ParallelEdgeRouter;
import org.tubs.epoc.SMFF.ModelElements.Application.AbstractSchedElemData;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;

public class JGraphTaskLink extends AbstractSchedElemData{

  private DefaultEdge src;
  private DefaultEdge trg;
  private DefaultGraphCell cell;
  private TaskLink taskLink;

  public JGraphTaskLink(TaskLink taskLink, JGraphTask srcJGraphTask, JGraphTask trgJGraphTask){
    this.taskLink = taskLink;
    
    DefaultGraphCell cell = new DefaultGraphCell();
    this.setCell(cell);
    DefaultEdge srcEdge = new DefaultEdge();
    this.setSrc(srcEdge);
    DefaultEdge trgEdge = new DefaultEdge();
    this.setTrg(trgEdge);
    this.setSource(srcJGraphTask.getCell());
    this.setTarget(trgJGraphTask.getCell());
  }

  private void setSource(DefaultGraphCell s){
    src.setSource(s);
  }

  public DefaultEdge getSourceEdge(){
    return this.src;
  }

  private void setTarget(DefaultGraphCell t){
    trg.setTarget(t);
  }

  public DefaultEdge getTargetEdge(){
    return this.trg;
  }

  public DefaultGraphCell getCell(){
    return cell;
  }


  private void setCell(DefaultGraphCell cell) {
    this.cell = cell;
    // setting up layout of cell
    GraphConstants.setAutoSize(cell.getAttributes(), true);
    GraphConstants.setBackground(cell.getAttributes(), GraphFormating.CreateGraphCellColor(taskLink.getAppId()));
    GraphConstants.setOpaque(cell.getAttributes(), true);
    GraphConstants.setBorderColor(cell.getAttributes(), Color.BLACK);
    GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(0,0,25,25));
    GraphConstants.setInset(cell.getAttributes(), 5);
    GraphConstants.setEditable(cell.getAttributes(), false);
  }

  public void setSrc(DefaultEdge src) {
    this.src = src;
    GraphConstants.setLineColor(src.getAttributes(), GraphFormating.CreateGraphCellColor(taskLink.getAppId()));
    GraphConstants.setLineBegin(src.getAttributes(), GraphConstants.ARROW_NONE);
    GraphConstants.setLineEnd(src.getAttributes(), GraphConstants.ARROW_NONE);
    GraphConstants.setDisconnectable(src.getAttributes(), false);
    GraphConstants.setEditable(src.getAttributes(), false);
    GraphConstants.setLineStyle(src.getAttributes(), GraphConstants.STYLE_ORTHOGONAL);
    GraphConstants.setRouting(src.getAttributes(), ParallelEdgeRouter.getSharedInstance());
    GraphConstants.setLineWidth(src.getAttributes(), (float) 2.0);
    src.setTarget(cell);
  }

  public void setTrg(DefaultEdge trg) {
    this.trg = trg;
    GraphConstants.setLineColor(trg.getAttributes(),GraphFormating.CreateGraphCellColor(taskLink.getAppId()));
    GraphConstants.setLineBegin(trg.getAttributes(), GraphConstants.ARROW_NONE);
    GraphConstants.setLineEnd(trg.getAttributes(), GraphConstants.ARROW_TECHNICAL);
    GraphConstants.setEndFill(trg.getAttributes(), true);
    GraphConstants.setEditable(trg.getAttributes(), false);
    GraphConstants.setLineStyle(trg.getAttributes(), GraphConstants.STYLE_ORTHOGONAL);
    GraphConstants.setRouting(trg.getAttributes(), ParallelEdgeRouter.getSharedInstance());
    GraphConstants.setDisconnectable(trg.getAttributes(), false);
    GraphConstants.setLineWidth(trg.getAttributes(), (float) 2.0);
    trg.setSource(cell);
  }

}
