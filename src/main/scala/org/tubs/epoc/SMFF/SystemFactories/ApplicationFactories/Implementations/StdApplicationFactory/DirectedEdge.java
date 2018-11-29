package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory;



/**
 * Model object for a directed edge. 
 * @author Christoph Lipka
 *
 */
public class DirectedEdge{
	
	private int id;
	private String name;
	private Vertex source;
	private Vertex destination;
	
	/**
	 * Getter method for the id of the directed edge.
	 * @return the id of the directed edge.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Getter method for the name of the directed edge. 
	 * @return the name of the directed edge
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Getter method for the source vertex of the directed edge.
	 * <p>
	 * Method will return the source vertex from which this edge arises-  
	 * @return the source vertex of this edge.
	 */
	public Vertex getSource() {
		return source;
	}
	
	/**
	 * Getter method for the destination vertex of this edge.
	 * <p>
	 * Method will return the vertex to which this edge points to.
	 * @return the destination vertex of this edge.
	 */
	public Vertex getDestination() {
		return destination;
	}

	/**
	 * Constructs an instance of a directed edge.
	 * <p>
	 * Class is mutual. Once the properties set they can only be queried and cannot be changed. 
	 * @param id identifier for this edge
	 * @param name name for this edge
	 * @param source vertex from which this edge arises
	 * @param destination vertex to which this edge points
	 */
	public DirectedEdge(int id, String name, Vertex source, Vertex destination){
		this.id = id;
		this.name = name;
		this.source = source;
		this.destination = destination;
		
		source.getAdj().add(destination);
		source.getIncidentDirectedEdges().add(this);
	}
	
	/**
	 * Gives a description for the directed edge.
	 * <p>
	 * A directed edge defined by its source and destination vertices as well as the id of this edge and a description is 
	 * constructed accordingly via the identifiers of the vertices and the id of this edge.
	 * @return description of the edge
	 */
	public String toString(){
		return  source + "-" + id + "->" + destination;
	}
	
}
