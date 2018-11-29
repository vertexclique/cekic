package org.tubs.epoc.SMFF.ImportExport.Pdf.Graph;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.ModelElements.Platform.AbstractResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.CommResource;
import org.tubs.epoc.SMFF.ModelElements.Platform.Resource;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;

/**
 * 
 * @author chrisl
 *
 */
public class SystemJGraph {
  SystemModel systemModel;

  // construct the graph to hold model and view
  private JGraph graph;
  private DefaultGraphModel model = new DefaultGraphModel();
  private GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory(), true);

  private JGraphOrganicLayout organicLayout;
  private JGraphFastOrganicLayout fastOrganicLayout;
  private JGraphFacade facade;

  private HashMap<JGraphRes,Point> resourcePositions;
  private HashMap<JGraphCRes,Point> commResourcePositions;

  public SystemJGraph(SystemModel systemModel){
    this.systemModel = systemModel;

    this.view.setFactory(new DefaultCellViewFactory());
    this.graph = new JGraph(model, view);

    generateResourceJGraph();
    generateAppJGraph();

    //runLayout();

    getResourcePositions();
    getCommResourcePositions();

    setTaskPositions();

    resizeResources();
    setupLayouts();
    runLayout();

    Rectangle2D cellBounds = facade.getCellBounds();
    graph.setSize((int)(cellBounds.getWidth()*1.3), (int)(cellBounds.getHeight()*1.3));
  }


  /**
   * help points to recalculate the task positions
   */
  private void getResourcePositions() {
    resourcePositions = new HashMap<JGraphRes,Point>();
    for(Resource res : systemModel.getResourceTable().values()){
      JGraphRes r = (JGraphRes)res.getSingleExtDataByClass(JGraphRes.class);
      Point p = new Point();
      p.setLocation(0,0);
      resourcePositions.put(r, p);
    }
  }

  /**
   * help points to recalculate the taskLink positions
   */
  private void getCommResourcePositions() {
    commResourcePositions = new HashMap<JGraphCRes,Point>();
    for(CommResource cRes : systemModel.getCommResourceTable().values()){
      JGraphCRes r = (JGraphCRes) cRes.getSingleExtDataByClass(JGraphCRes.class);
      Point p = new Point();
      p.setLocation(0, 0);
      commResourcePositions.put(r,p);
    }
  }

  /**
   * creates and run layout on the graph
   */
  private void setupLayouts() {
    setCommResBounds();

    fastOrganicLayout = new JGraphFastOrganicLayout();
    fastOrganicLayout.setForceConstant(100.0);
    fastOrganicLayout.setInitialTemp(1000.0);
    fastOrganicLayout.setMaxIterations(1000);

    // organic layout
    organicLayout = new JGraphOrganicLayout();
    organicLayout.setOptimizeBorderLine(false);
    organicLayout.setDeterministic(true);
    organicLayout.setAverageNodeArea(10000);
    organicLayout.setOptimizeEdgeDistance(true);
    organicLayout.setEdgeDistanceCostFactor(5000);
    organicLayout.setOptimizeEdgeLength(true);
    organicLayout.setEdgeLengthCostFactor(0.1);
    organicLayout.setOptimizeNodeDistribution(true);
    organicLayout.setNodeDistributionCostFactor(10000);
    organicLayout.setOptimizeEdgeCrossing(true);
    organicLayout.setEdgeCrossingCostFactor(500000);
  }

  public void runLayout() {
    Map<?, ?> nested;
    
    // create automatic graph layout (layout pro Functionality)
    facade = new JGraphFacade(graph);
    facade.setDirected(true);
    facade.setIgnoresCellsInGroups(false);

    facade.run(organicLayout, true);
    facade.createNestedMap(true,new Point2D.Double(100,100));
    nested = facade.createNestedMap(true,new Point2D.Double(100,100));  
    graph.getGraphLayoutCache().edit(nested);

    getResourcePositions();
    getCommResourcePositions();
    setTaskPositions();
    facade.run(organicLayout, true);
    nested = facade.createNestedMap(true,new Point2D.Double(100,100));   
    graph.getGraphLayoutCache().edit(nested);

    facade.run(fastOrganicLayout, true);
    nested = facade.createNestedMap(true,new Point2D.Double(100,100));   
    graph.getGraphLayoutCache().edit(nested);
  }

  /**
   * calculates better positions for Tasks and TaskLinks
   */
  private void setTaskPositions() {
    // put the Tasks in the graph
    setCommResBounds();

    for(ApplicationModel app : systemModel.getApplications()){
      for(Task task : app.getTaskList().values()){
        JGraphTask jGraphTask = (JGraphTask) task.getSingleExtDataByClass(JGraphTask.class);
        JGraphRes jGraphRes = (JGraphRes) task.getMappedTo().getSingleExtDataByClass(JGraphRes.class);

        DefaultGraphCell resCell = jGraphRes.getCell();

        Rectangle2D rect = (Rectangle2D) resCell.getAttributes().get(GraphConstants.BOUNDS);

        Map nested = new Hashtable();
        Map attrMap = new Hashtable();

        Point p = resourcePositions.get(jGraphRes);

        GraphConstants.setBounds(attrMap, 
            new Rectangle2D.Double(rect.getX() + p.getX(), rect.getY() + p.getY(),0,0));

        p.setLocation(p.getX()+140, p.getY());
        if(p.getX()>200){
          p.setLocation(0, p.getY()+70);
        }

        nested.put(jGraphTask.getCell(),attrMap);
        graph.getGraphLayoutCache().edit(nested, null, null, null);


      }
    }

    // put the TaskLinks in the graph
    for(ApplicationModel app : systemModel.getApplications()){
      for(TaskLink tasklink : app.getTaskLinkList().values()){
        JGraphTaskLink jGraphTasklink = (JGraphTaskLink) tasklink.getSingleExtDataByClass(JGraphTaskLink.class);
        Task srcTask = systemModel.getTask(tasklink.getSrcTask());
        Task trgTask = systemModel.getTask(tasklink.getTrgTask());
        JGraphTask jGraphSrcTask = (JGraphTask) srcTask.getSingleExtDataByClass(JGraphTask.class);
        JGraphTask jGraphTrgTask = (JGraphTask) trgTask.getSingleExtDataByClass(JGraphTask.class);

        Map nested = new Hashtable();
        Map attrMap = new Hashtable();

        Rectangle2D rectSrc = (Rectangle2D) jGraphSrcTask.getCell().getAttributes().get(GraphConstants.BOUNDS);
        Rectangle2D rectTrg = (Rectangle2D) jGraphTrgTask.getCell().getAttributes().get(GraphConstants.BOUNDS);

        // if TaskLink is mapped On a CommResource, draw it on it
        if(tasklink.getMappedTo() instanceof CommResource){
          JGraphCRes c = (JGraphCRes) ((CommResource)tasklink.getMappedTo()).getSingleExtDataByClass(JGraphCRes.class);

          Random rnd = new Random();

          Rectangle2D  cRect =  (Rectangle2D) c.getCell().getAttributes().get(GraphConstants.BOUNDS);

          double x = (rectSrc.getX()+rectTrg.getX())/2 +rnd.nextInt(50);
          double y =  (rectSrc.getY()+rectTrg.getY())/2 +rnd.nextInt(50);

          if(x-cRect.getX() > 250){
            x = cRect.getX()+cRect.getWidth()/2;//x - (x-cRect.getX() - 300);									
          }
          if(x-cRect.getX() < 0){
            x = cRect.getX()+rnd.nextInt((int) cRect.getWidth()/2);//x - (x-cRect.getX() - 300);									
          }
          if(y-cRect.getY() > 150){
            y = cRect.getY()+cRect.getHeight()/2;//y - (y-cRect.getY() - 150);
          }
          if(y-cRect.getY() < 0){
            y = cRect.getY()+rnd.nextInt((int) cRect.getHeight()/2);//x - (x-cRect.getX() - 300);									
          }
          GraphConstants.setBounds(attrMap, 
              new Rectangle2D.Double(x , y  ,15,15));

        }
        // both tasks mapped on the same resource
        else{
          if(rectSrc.getX() == rectTrg.getX()){
            if(rectSrc.getY() > rectTrg.getY()){
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectSrc.getX()-20, rectTrg.getY()+1.5*rectTrg.getHeight(),15,15));
            }
            else{
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectSrc.getX()-20, rectSrc.getY()+1.5*rectSrc.getHeight(),15,15));
            }
          }
          else if(rectSrc.getY() == rectTrg.getY()){
            if(rectSrc.getX() > rectTrg.getX()){
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectTrg.getX()+rectTrg.getWidth(),rectSrc.getY()-20, 15,15));
            }
            else{
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectSrc.getX() + rectSrc.getWidth(),rectSrc.getY()-20, 15,15));
            }
          }
          else{
            if(rectSrc.getX() > rectTrg.getX() && rectSrc.getY() > rectTrg.getY()){
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectTrg.getX()+(rectSrc.getX()-rectTrg.getX())/2, rectTrg.getY()+(rectSrc.getY()-rectTrg.getY())/2,15,15));
            }
            else if(rectSrc.getX() < rectTrg.getX() && rectSrc.getY() > rectTrg.getY()){
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectSrc.getX()+(rectTrg.getX()-rectSrc.getX())/2, rectTrg.getY()+(rectSrc.getY()-rectTrg.getY())/2,15,15));
            }
            else if(rectSrc.getX() > rectTrg.getX() && rectSrc.getY() < rectTrg.getY()){
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectTrg.getX()+(rectSrc.getX()-rectTrg.getX())/2, rectSrc.getY()+(rectTrg.getY()-rectSrc.getY())/2,15,15));

            }
            else{
              GraphConstants.setBounds(attrMap, 
                  new Rectangle2D.Double(rectSrc.getX()+(rectTrg.getX()-rectSrc.getX())/2, rectSrc.getY()+(rectTrg.getY()-rectSrc.getY())/2,15,15));
            }
          }

        }

        nested.put(jGraphTasklink.getCell(),attrMap);
        graph.getGraphLayoutCache().edit(nested, null, null, null);

      }
    }
  }

  private void generateAppJGraph() {
    // put the Tasks in the graph
    // for all applications of this app
    for(ApplicationModel app : systemModel.getApplications()){
      // insert all tasks
      for(Task task: app.getTaskList().values()){
        // create JTask for the Task we are currently looking at
        JGraphTask jGraphTask = new JGraphTask(task);
        task.addExtData(jGraphTask, false, true, false);

        // reflect mapping
        if (task.getMappedTo() != null) {
          Resource res = (Resource) task.getMappedTo();
          JGraphRes jRes = (JGraphRes)res.getSingleExtDataByClass(JGraphRes.class);
          DefaultGraphCell resCell = jRes.getCell();
          resCell.insert(jGraphTask.getCell(), resCell.getChildCount());
        }

        // insert the task into the jgraph
        graph.getGraphLayoutCache().insert(jGraphTask.getCell());
      }

      // insert all task links of this app
      for (TaskLink tasklink : app.getTaskLinkList().values()) {
        Task srcTask = systemModel.getTask(tasklink.getSrcTask());
        Task trgTask = systemModel.getTask(tasklink.getTrgTask());
        JGraphTask src = (JGraphTask) srcTask.getSingleExtDataByClass(JGraphTask.class);
        JGraphTask trg = (JGraphTask) trgTask.getSingleExtDataByClass(JGraphTask.class);

        // attach data extension
        JGraphTaskLink link = new JGraphTaskLink(tasklink, src, trg);
        tasklink.addExtData(link, false, true, false);

        // if TaskLink is mapped On a CommResource, draw it on it
        if(tasklink.getMappedTo() instanceof CommResource){
          CommResource cRes = systemModel.getCommResource(tasklink.getMappedTo().getResId());
          JGraphCRes jGraphCRes = (JGraphCRes) cRes.getSingleExtDataByClass(JGraphCRes.class);

          jGraphCRes.getCell().insert(link.getCell(), jGraphCRes.getCell().getChildCount());
          graph.getGraphLayoutCache().insert(link.getCell());
          graph.getGraphLayoutCache().insert(link.getSourceEdge());
          graph.getGraphLayoutCache().insert(link.getTargetEdge());


        } else {
          // get the hash from mapped Resource
          Resource res = systemModel.getResource(tasklink.getMappedTo().getResId());
          JGraphRes jGraphRes = (JGraphRes) res.getSingleExtDataByClass(JGraphRes.class);
          //DefaultPort port = new DefaultPort();
          //link.setJointPort(port);
          jGraphRes.getCell().insert(link.getCell(), jGraphRes.getCell().getChildCount());
          graph.getGraphLayoutCache().insert(link.getCell());
          jGraphRes.getCell().insert(link.getSourceEdge(), jGraphRes.getCell().getChildCount());
          graph.getGraphLayoutCache().insert(link.getSourceEdge());
          jGraphRes.getCell().insert(link.getTargetEdge(), jGraphRes.getCell().getChildCount());
          graph.getGraphLayoutCache().insert(link.getTargetEdge());

        }
      }
    }
  }

  /**
   * creates graphical Presentation of Resources and put them to the graph
   */
  private void generateResourceJGraph() {

    // go through all resources and attach the jgraph modelling extension
    for(Resource res : systemModel.getResourceTable().values()){
      // create modelling extension
      JGraphRes jRes = new JGraphRes(res);
      res.addExtData(jRes, false, true, false);
      // add cell of modelling extension to jgraph model
      graph.getGraphLayoutCache().insert(jRes.getCell());
    }

    // go through all comm. resources and attach the jgraph modelling extension
    for (CommResource cRes : systemModel.getCommResourceTable().values()){
      JGraphCRes jCommRes = new JGraphCRes(cRes);
      // create modelling extension
      cRes.addExtData(jCommRes, false, true, false);
      // add cell of modelling extension to jgraph model
      graph.getGraphLayoutCache().insert(jCommRes.getCell());
    }

    // connect them		
    for (Resource res: systemModel.getResourceTable().values()){
      for(AbstractResource neighbor : res.getNeighbors()){
        CommResource cRes = (CommResource) neighbor;

        // establish connection in jgraph model
        JGraphRes jRes = (JGraphRes)res.getSingleExtDataByClass(JGraphRes.class);
        JGraphCRes jCRes = (JGraphCRes)cRes.getSingleExtDataByClass(JGraphCRes.class);
        DefaultPort src = jRes.getPort();
        DefaultPort trg = jCRes.getPort();
        JGraphResLink link = new JGraphResLink(src, trg);
        graph.getGraphLayoutCache().insert(link);
      }
    }
  }

  /**
   * resize resources to have a better layout
   */
  private void resizeResources() {
    // resize resources
    for(Resource res : systemModel.getResourceTable().values()){
      JGraphRes jGraphRes = (JGraphRes) res.getSingleExtDataByClass(JGraphRes.class);

      Rectangle2D rect = (Rectangle2D) jGraphRes.getCell().getAttributes().get(GraphConstants.BOUNDS);

      rect.setRect(rect.getX(), rect.getY(), /*resourcePositions.get(i).getX()+100*/200,
          100/*resourcePositions.get(i).getY()+100*/);

      Map nested = new Hashtable();
      Map attrMap = new Hashtable();

      GraphConstants.setBounds(attrMap, rect);

      nested.put(jGraphRes.getCell(),attrMap);
      graph.getGraphLayoutCache().edit(nested, null, null, null);
    }
    //resize comm resources
    for(CommResource cRes : systemModel.getCommResourceTable().values()){
      JGraphCRes jGraphCRes = (JGraphCRes) cRes.getSingleExtDataByClass(JGraphCRes.class);

      Rectangle2D rect = (Rectangle2D) jGraphCRes.getCell().getAttributes().get(GraphConstants.BOUNDS);

      rect.setRect(rect.getX(), rect.getY(), /*resourcePositions.get(i).getX()+100*/100,
          50/*resourcePositions.get(i).getY()+100*/);

      Map nested = new Hashtable();
      Map attrMap = new Hashtable();

      GraphConstants.setBounds(attrMap, rect);

      nested.put(jGraphCRes.getCell(),attrMap);
      graph.getGraphLayoutCache().edit(nested, null, null, null);
    }
    setupLayouts();
  }

  public JGraph getGraph() {
    return graph;
  }

  private void setCommResBounds(){
    for(CommResource cRes : systemModel.getCommResourceTable().values()){
      JGraphCRes jGraphCRes = (JGraphCRes) cRes.getSingleExtDataByClass(JGraphCRes.class);
      
      Map nested = new Hashtable();
      Map attrMap = new Hashtable();

      Rectangle2D rect = (Rectangle2D) jGraphCRes.getCell().getAttributes().get(GraphConstants.BOUNDS);
      rect = new Rectangle2D.Double(rect.getX(),rect.getY(),300,150);

      GraphConstants.setBounds(attrMap, rect);

      nested.put(jGraphCRes.getCell().getAttributes(),attrMap);
      graph.getGraphLayoutCache().edit(nested, null, null, null);
    }
  }
}
