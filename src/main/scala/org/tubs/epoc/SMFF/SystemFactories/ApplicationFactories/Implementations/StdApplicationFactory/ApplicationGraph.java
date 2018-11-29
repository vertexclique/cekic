package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory;

import java.util.LinkedList;

import org.tubs.epoc.SMFF.ModelElements.Application.AbstractAppData;

/**
 * Class definition for the application graph. A model object which stores the relevant information related with
 * graph, i.e vertices and edges.
 *
 */
public class ApplicationGraph extends AbstractAppData{	
	private LinkedList<Vertex> vertices;
	private LinkedList<UndirectedEdge> undirectedEdges;
	private LinkedList<DirectedEdge> directedEdges;
	// this lists are used by ApplicationGraphEditor class
	private LinkedList<Vertex> usedNodes;
	private LinkedList<LinkedList<Vertex>> cycles;
	
	/**
	 * Default constructor.
	 */
	public ApplicationGraph(){
		vertices = new LinkedList<Vertex>();
		undirectedEdges = new LinkedList<UndirectedEdge>();
		directedEdges = new LinkedList<DirectedEdge>();
		usedNodes = new LinkedList<Vertex>();
		cycles = new LinkedList<LinkedList<Vertex>>();
	}
	
	
	// generic methods
	
	/**
	 * Getter method for the number of vertices.
	 * @return number of vertices.
	 */
	public int numVertices(){
		return vertices.size();
	}
	/**
	 * Getter method for the number of undirected edges.
	 * @return number of undirected edges
	 */
	public int numEdges(){
		return undirectedEdges.size();
	}
	
	/**
	 * Getter method for all the vertices of this graph.
	 * @return the vertices of this graph as a list.
	 */
	public LinkedList<Vertex> getVertices(){
		return vertices;
	}
	
	/**
	 * Getter method for the undirected edges of this graph.
	 * @return the undirected edges of this graph as a list.
	 */
	public LinkedList<UndirectedEdge> getUndirectedEdges(){
		return undirectedEdges;
	}
	
	/**
	 * Getter method for the directed edges of this graph.
	 * @return the directed edges of this graph as a list.
	 */
	public LinkedList<DirectedEdge> getDirectedEdges(){
		return directedEdges;
	}
	
	/**
	 * Getter method for the used nodes of this graph.
	 * <p>
	 * Used nodes are vertices too.
	 * @return the used nodes of this graph as a list.
	 */
	public LinkedList<Vertex> getUsedNodes() {
		return usedNodes;
	}

	/**
	 * Getter method for the cycles in this graph.
	 * @return the list of cyclic vertices in this graph. 
	 */
	public LinkedList<LinkedList<Vertex>> getCycles() {
		return cycles;
	}

	//update methods
	/**
	 * Inserts a vertex to this graph.
	 * @param v vertex to be added to this graph
	 */
	public void insertVertex(Vertex v){
		vertices.add(v);
	}
	
	/**
	 * Inserts an undirected edge to this graph.
	 * @param e undirected edge to be added to this graph
	 */
	public void insertEdge(UndirectedEdge e){
		undirectedEdges.add(e);
	}
	
	/**
	 * Inserts an directed edge to this graph.
	 * @param e directed edge to be added to this graph
	 */
	public void insertDirectedEdge(DirectedEdge e){
		directedEdges.add(e);
	}
	
	// accessor methods	- to test, they are public, later they will be private
	/**
	 * Query method for the degree of vertex passed as parameter to this method.
	 * @return degree of vertex <tt>v</tt>
	 */
	public int degree(Vertex v){
		return v.getAdj().size();
		
	}
	
	/**
	 * Checks whether two vertices that are passed as parameter to this method are neighbors.
	 * @param v first vertex
	 * @param w second vertex
	 * @return <tt>true</tt> if <tt>v</tt> and <tt>w</tt> are adjacent, <tt>false</tt> otherwise 
	 */
	public boolean areAdjacent(Vertex v, Vertex w){
		return v.getAdj().contains(w);
	}
	
	/**
	 * Query method for the neighbor vertices of passed vertex.
	 * @param v vertex, adjacent vertices of which will be returned
	 * @return adjacent vertices of vertex <tt>v</tt>
	 */
	public LinkedList<Vertex> adjacentVerices(Vertex v){
		return v.getAdj();
	}
	
	/**
	 * Query method for finding the vertices which is connected via the edge passed as parameter.
	 * <p>
	 * The result will be a list which includes both vertices connected via edge <tt>e</tt>.
	 * @param e edge
	 * @return returns both vertices which are connected by edge <tt>e</tt>
	 */
	public LinkedList<Vertex> endVertices(UndirectedEdge e){
		return e.getEndVertices();
	}
	/**
	 * Query method for finding the edge between two vertices which are passed as parameters to this method.
	 * @param v first vertex
	 * @param w second vertex
	 * @return the edge which connects vertices <tt>v</tt> and <tt>w</tt>
	 */
	public UndirectedEdge getUndirectedEdge(Vertex v, Vertex w){
		
		for(int i = 0; i < undirectedEdges.size(); i++){
			if(undirectedEdges.get(i).getEndVertices().contains(v) && undirectedEdges.get(i).getEndVertices().contains(w)){
				return undirectedEdges.get(i);
			}
		}
		// there is a bug...
		return null;
	}
	
	/**
	 * Query method for finding the second vertex which resides on the other side of the edge passed as parameter.
	 * <p>
	 * Edge <tt>e</tt> is the edge which binds the vertices <tt>v</tt> and the opposite vertex to be returned together.
	 * The first parameter to this method (<tt>v</tt> must either be the first or the last vertex of the edge <tt>e</tt>.
	 * @param v vertex which resides on one side of the edge <tt>e</tt>
	 * @param e edge from parameter <tt>v</tt> to the opposite vertex
	 * @return the opposite vertex which is connected to vertex <tt>v</tt> through the edge <tt>e</tt> 
	 */
	//TODO[mervan]: commented part seems to be true.
	public Vertex opposite(Vertex v, UndirectedEdge e){
	//	if(!e.getEndVertices().contains(v)){
	//		return null;
	//	}
			
		if(v == e.getEndVertices().getFirst()){
			return e.getEndVertices().getLast();
		}
		else
			return e.getEndVertices().getFirst();
	}
	
	/**
	 * Query method for the undirected edges of the vertex passed as parameter.
	 * @param v vertex
	 * @return incident edges of parameter <tt>v</tt>
	 */
	public LinkedList<UndirectedEdge> getIncidentEdges(Vertex v){
		return v.getIncidentEdges();
	}
	
	/**
	 * Assigns a label to a vertex.
	 * @param v vertex to be assigned a label
	 * @param s label for the vertex
	 */
	public void setLabel(Vertex v, String s){
		for(int i = 0; i < vertices.size(); i++){
			if(vertices.get(i).equals(v)){
				vertices.get(i).setLabel(s);
			}
		}
	}
	
	/**
	 * Assign a label to an edge.
	 * @param e edge to be assigned a label
	 * @param s label for the edge
	 */
	public void setLabel(UndirectedEdge e, String s){
		for(int i = 0; i < undirectedEdges.size(); i++){
			if(undirectedEdges.get(i).equals(e)){
				undirectedEdges.get(i).setLabel(s);
			}
		}
	}
}