package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory;

import java.util.LinkedList;

/**
 * Model object for the vertex of a graph.
 * <p>
 * Vertex represents a graph node.
 * Possible labels for a vertex are UNVISITED and VISITED.
 * 
 * @author Christoph Lipka
 *
 */
public class Vertex {
	
	private String name;
	private String label;
	private int id;
	private int mappedResource;
	private LinkedList<Vertex> vertexAdj; 
	private LinkedList<Vertex> directVertexAdj;
	private LinkedList<UndirectedEdge> incidentEdges;
	private UndirectedEdge exploredFrom;
	private LinkedList<DirectedEdge> incidentDirectedEdges;
	
	/**
	 * Constructs an instance of a vertex.
	 * @param name name of the vertex
	 * @param id id of the vertex
	 */
	public Vertex(String name, int id){
		this.id = id;
		this.name = name;
		mappedResource = -1;
		vertexAdj = new LinkedList<Vertex>();
		incidentEdges = new LinkedList<UndirectedEdge>();
		incidentDirectedEdges = new LinkedList<DirectedEdge>();
		directVertexAdj = new LinkedList<Vertex>();
		label = "UNVISITED";
		exploredFrom = null;
	}
	
	/**
	 * Getter method for the label of the vertex.
	 * <p>
	 * Possible labels for a vertex are UNVISITED and VISITED.
	 * @return <tt>VISITED</tt> for visited vertices, <tt>UNVISITED</tt> otherwise.
	 */
	//TODO[mervan]: ENUM for LABELS?
	public String getLabel(){
		return label;
	}
	
	/**
	 * Setter method for the label of the vertex.
	 * <p>
	 * Possible labels for a vertex are UNVISITED and VISITED.
	 * @param lab <tt>VISITED</tt> for visited vertices, <tt>UNVISITED</tt> otherwise.
	 */
	public void setLabel(String lab){
		label = lab;
	}
	
	/**
	 * Getter method for the name of the vertex.
	 * @return the name of the vertex
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Getter method for the id of this vertex. 
	 * @return the id of this vertex
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Queries all the adjacent vertices of this vertex and returns it as a list.
	 * @return the adjacent vertices of this vertex as a list.
	 */
	public LinkedList<Vertex> getAdj() {
		return vertexAdj;
	}
	
	/**
	 * Queries all the incident undirected edges of this vertex and returns it as a list.
	 * <p>
	 * Incident edges are the edges arising from this vertex
	 * @return the incident undirected edges of this vertex as a list.
	 */
	public LinkedList<UndirectedEdge> getIncidentEdges(){
		return incidentEdges;
	}
	
	/**
	 * Queries all the incident directed edges of this vertex and returns it as a list.
	 * @return the incident directed edges of this vertex as a list.
	 */
	public LinkedList<DirectedEdge> getIncidentDirectedEdges(){
		return incidentDirectedEdges;
	}
	
	/**
	 * Queries the direct neighbors of this vertex and returns it as a list.
	 * @return the direct neighbors of this vertex as a list
	 */
	public LinkedList<Vertex> getDirectAdj(){
		return directVertexAdj;
	}
	
	/**
	 * If this vertex is explored, this method is to as for the edge that has explored this edge.
	 * @return the undirected edge which has explored this vertex, <tt>null</tt> if this vertex is not explored
	 */
	public UndirectedEdge getExploredFrom() {
		return exploredFrom;
	}

	/**
	 * Sets the explorer edge of this vertex.
	 * @param exploredFrom edge which has explored this vertex
	 */
	public void setExploredFrom(UndirectedEdge exploredFrom) {
		this.exploredFrom = exploredFrom;
	}

	/**
	 * Description of the vertex.
	 * <p>
	 * A vertex is described via its name.
	 * @return the description of the vertex.
	 */
	public String toString(){
		return "<" + name + ">";
	}

	public int getMappedResource() {
		return mappedResource;
	}

	public void setMappedResource(int mappedResource) {
		this.mappedResource = mappedResource;
	}
	
	
}
