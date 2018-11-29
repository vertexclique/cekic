package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory;

import java.util.LinkedList;

/**
 * Model object for an edge.
 * <p>
 * Edges are the links between nodes
 * every edges has two end nodes
 * Possible labels for an edge are labels: UNEXPLORED, DISCOVERY, BACK
 * 
 * @author Christoph Lipka
 *
 */
//TODO[mervan]: ID has never been set!
public class UndirectedEdge{
	
	private int id;
	private String name;
	private String label;
	private LinkedList<Vertex> endVertices;
	
	
	/**
	 * Constructs an instance of an edge.
	 * @param name name of the edge
	 * @param l first Vertex
	 * @param r second Vertex
	 */
	public UndirectedEdge(String name, Vertex l, Vertex r){
		id = 0;
		this.name = name;
		label = "UNEXPLORED";
		endVertices = new LinkedList<Vertex>();
		
		l.getAdj().add(r);
		l.getIncidentEdges().add(this);
		r.getAdj().add(l);
		r.getIncidentEdges().add(this);
		endVertices.add(l);
		endVertices.add(r);
		
	}
	
	/**
	 * Getter method for the label of the edge.
	 * <p>
	 * Possible labels for an edge are: UNEXPLORED, DISCOVERY, BACK.
	 * @return the label of the edge
	 */
	//TODO[mervan]: ENUM for LABELS?
	public String getLabel() {
		return label;
	}

	/**
	 * Setter method for the label of the edge.
	 * <p>
	 * Possible labels for an edge are: UNEXPLORED, DISCOVERY, BACK.
	 * @param label the label of the edge (UNEXPLORED, DISCOVERY or BACK)
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Getter method for the end vertices of this edge.
	 * <p>
	 * An edge has two end vertices. These two vertices will be returned to the client as a list.
	 * @return the end vertices of this edge (a list of size two including both vertices).
	 */
	public LinkedList<Vertex> getEndVertices() {
		return endVertices;
	}
	
	/**
	 * Description of the edge.
	 * <p>
	 * An undirected edge is defined via the two vertices on each side of this edge as well as the id
	 * of this edge. 
	 * @return the description
	 */
	public String toString(){
		return  endVertices.get(0) + "-" + id + "-" + endVertices.get(1);
	}

}

