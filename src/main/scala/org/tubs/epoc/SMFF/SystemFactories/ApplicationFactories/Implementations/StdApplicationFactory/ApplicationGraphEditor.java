package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Edits a given graph so that no redundant cycles will occur in it.
 *
 */
public class ApplicationGraphEditor {
	
	private ApplicationGraph graph;
	private boolean cyclic;
	private long seed;
	
	/**
	 * Constructs an instance of a graph editor.
	 * <p>
	 * As a result it will manipulate the graph so that no cycles will occur within the graph.
	 * @param graph the application graph
	 * @param c boolean indicator for cyclic graphs, <tt>true</tt> if graph is cyclic
	 * @param seed for random number generators
	 */
	public ApplicationGraphEditor(ApplicationGraph graph, boolean c, long seed){
		this.graph = graph;
		this.cyclic = c;
		this.seed = seed;
		editTaskGraph();
	}
	
	private void editTaskGraph() {
		findCycles(graph);
		deleteDoubleCycles();
		directingEdges();
	}



	private void findCycles(ApplicationGraph g) {
		// first node is start
		Vertex start = g.getVertices().getFirst();
		exploreGraph(g, start);
	}
	
	private void exploreGraph(ApplicationGraph g, Vertex v) {
		if(graph.getUsedNodes().contains(v)){
			LinkedList<Vertex> t = new LinkedList<Vertex>();
			// copy all nodes from index v in list t
			int cycleStartIndex = graph.getUsedNodes().indexOf(v);
			for(int i = cycleStartIndex; i < graph.getUsedNodes().size(); i++){
				t.add(graph.getUsedNodes().get(i));
			}
			// add start node to list
			t.add(t.getFirst());
			if(t.size() > 3){
				graph.getCycles().add(t);
			}
			return;
		}
		if((g.adjacentVerices(v).size()==1) && (v != g.getVertices().getFirst())){
			return;
		}
		
		graph.getUsedNodes().add(v);
		
		Vertex w = null;
		
		for(int i = 0; i < g.getIncidentEdges(v).size(); i++){
			UndirectedEdge e = (UndirectedEdge) g.getIncidentEdges(v).get(i);
			
			if(e.getLabel() == "UNEXPLORED"){
				w = g.opposite(v, e);
				w.setExploredFrom(e);
				exploreGraph(g, w);
			}
				
		}
		if(w != null){
			Iterator<UndirectedEdge> it = w.getIncidentEdges().iterator();
			while(it.hasNext()){
				UndirectedEdge e = it.next();
				e.setLabel("UNEXPLORED");
			}
		}
		graph.getUsedNodes().removeLast();
	}
	
	
	
	/**
	 * Deletes redundant cycles in results.
	 */
	private void deleteDoubleCycles() {
		// take every cycle and delete those who have the same vertices
		for(int i = 0; i < graph.getCycles().size()-1; i++){
			LinkedList<Vertex> l1 = graph.getCycles().get(i);
			for(int j = i+1; j < graph.getCycles().size(); j++){
				LinkedList<Vertex> l2 = graph.getCycles().get(j);
				boolean areTheSame = true;
				// only if the lists have the same length, they could be the same cycle
				if(l1.size() == l2.size()){
					for(int k = 0; k < l1.size(); k++){
						if(!l1.contains(l2.get(k))){
							areTheSame = false;
						}
					}
				}
				if(areTheSame && (l1.size() == l2.size())){
					graph.getCycles().remove(j);
				}
			}
		}
		
		// delete all cycle edges from appGraph.edges list
		for(int i = 0; i < graph.getCycles().size(); i++){
			LinkedList<Vertex> l1 = graph.getCycles().get(i);
			for(int j = 0; j < l1.size()-1; j++){
				
				UndirectedEdge ue = graph.getUndirectedEdge(l1.get(j), l1.get(j+1));
				graph.getDirectedEdges().remove(ue);
			}
		}
	}
	

	/**
	 * directed the edges by using the cycles
	 */
	private void directingEdges(){
		// at first, direct all cycle edges
		// if cyclic = 1 -> directing the cycles
		// else, split the cycle, direct some edges in one
		// and the rest in other direction
		Integer k = 0;
		if(cyclic){
			for(int i = 0; i < graph.getCycles().size(); i++){
				for(int j = 0; j < graph.getCycles().get(i).size()-1; j++){
					Vertex source = graph.getCycles().get(i).get(j);
					Vertex destination = graph.getCycles().get(i).get(j+1);
					DirectedEdge de = new DirectedEdge(k,"e"+k.toString(),source,destination);
					if(!graph.getDirectedEdges().contains(de)){
						graph.getDirectedEdges().add(de);
						k++;
					}
					
				}
			}
		}
		else{
			Random rnd = new Random(seed);
			for(int i = 0; i < graph.getCycles().size(); i++){
				
				int split = 0;
				while(split == 0 || split ==graph.getCycles().get(i).size()){
					split = rnd.nextInt(graph.getCycles().get(i).size());
				}
				
				for(int j = 0; j < graph.getCycles().get(i).size()-1; j++){
					if(j < split){
						Vertex source = graph.getCycles().get(i).get(j);
						Vertex destination = graph.getCycles().get(i).get(j+1);
						DirectedEdge de = new DirectedEdge(k,"e"+k.toString(),source,destination);
						if(!graph.getDirectedEdges().contains(de)){
							graph.getDirectedEdges().add(de);
							k++;
						}
					}
					else{
						Vertex destination = graph.getCycles().get(i).get(j);
						Vertex source = graph.getCycles().get(i).get(j+1);
						DirectedEdge de = new DirectedEdge(k,"e"+k.toString(),source,destination);
						if(!graph.getDirectedEdges().contains(de)){
							graph.getDirectedEdges().add(de);
							k++;
						}
					}
				}
			}
		}
		
		// direct the rest of edges
		for(int i = 0; i < graph.getUndirectedEdges().size(); i++){
			UndirectedEdge ue = graph.getUndirectedEdges().get(i);
			DirectedEdge de = new DirectedEdge(k, "e"+k.toString(),ue.getEndVertices().getFirst(),
					ue.getEndVertices().getLast());
			graph.getDirectedEdges().add(de);
			k++;
		}
		// delete diametrical edges
		for(int i = 0; i < graph.getDirectedEdges().size()-1; i++){
			DirectedEdge e1 = graph.getDirectedEdges().get(i);
			for(int j = i+1; j < graph.getDirectedEdges().size(); j++){
				DirectedEdge e2 = graph.getDirectedEdges().get(j);
	
				if((e1.getSource().getName() == e2.getDestination().getName())&&
						e1.getDestination().getName() == e2.getSource().getName()){
					graph.getDirectedEdges().remove(e2);
				}
			}
		}
		// delete double edges
		for(int i = 0; i < graph.getDirectedEdges().size()-1; i++){
			DirectedEdge e1 = graph.getDirectedEdges().get(i);
			for(int j = i+1; j < graph.getDirectedEdges().size(); j++){
				DirectedEdge e2 = graph.getDirectedEdges().get(j);
				if((e1.getSource().getName() == e2.getSource().getName())&&
						e1.getDestination().getName() == e2.getDestination().getName()){
					graph.getDirectedEdges().remove(e2);
				}
			}
		}
	}
}
