package org.insight.centre.feds.pathmerger;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.insight.centre.feds2.feds2.Path;




public class BaselineAlgo{

	
	LinkedList<TNode> q;
	protected List<Path> solutions = new LinkedList<>();
	boolean reversed = false;
	protected Node startNode, targetNode;
	protected Node filterEdge;
	
	Model gIdx;

	public BaselineAlgo() {
		
	}

	public List<Path> run(String start, String end, Node filter,  int k, Model mdl) throws Exception {

		this.startNode=NodeFactory.createURI(start);
		this.targetNode= NodeFactory.createURI(end);
		this.gIdx=mdl;
	        
	      filterEdge =  filter==null? null : null;// gIdx.getProperty(filter.toString()).asNode();
	      
		TNode n = TNode.createNode(startNode, null, null);
		q = new LinkedList<>();
		q.add(n);

		

		int qsize = 0;
		while (!q.isEmpty()) {
			if (qsize < q.size()) {
				qsize = q.size();
			}
			TNode current = q.poll();
			visit(current);
		
			if (solutions.size() >= k) {
				System.out.println("Baseline-Algo Done!: " + qsize);
				break;
			}
		}
		System.out.println("Done!!");
			
		return solutions;


	}

	private void visit(TNode current) throws Exception {
		
		TNode next = null;
		
		for(StmtIterator stmtI= gIdx.listStatements(gIdx.getResource(current.vertex.toString()), null, (RDFNode)null);stmtI.hasNext(); ){//gIdx.getResource(vertex.toString()).listProperties();stmtI.hasNext();){
   		Triple t = stmtI.next().asTriple();
			if (t.getObject().isLiteral())
				continue;
			boolean doNotQueue = false;
			
			if (current.visitedEdge(t)) {
			
			
					continue;
			
				
			}
			
			
			
			if (current.visiteddVertex(t)) {
				
				if (t.getObject() != targetNode) {
					doNotQueue = false;
				}else{doNotQueue = true;}

			}

			next = TNode.createNode(t.getObject(), t.getPredicate(), current);
			
			if (!doNotQueue) {
				q.add(next);
			}
	

			if (t.getObject().equals(targetNode) && (current.filterEdge == null || t.getPredicate() == filterEdge)) {
				doNotQueue = true;
				Path p = trace(next, new Path(Collections.singleton(targetNode)));
				if (!reversed) {
					p.reverse();
					solutions.add(p);
				}
					
			}

		}

	}
	
	
	static Path trace( TNode n, Path p ){
		if( n.prev == null ){
			return p;
		}
		p.appendEdge( n.incomingEdge, n.prev.vertex );
		return trace(n.prev, p);
	}



    static class TNode {
    	Node vertex;
    	Node incomingEdge;
    	Node filterEdge = null;
		TNode prev = null;

		TNode(Node vertex, Node incomingEdge, TNode prev) {
	
			this.vertex = vertex;
			this.incomingEdge = incomingEdge;
			this.prev = prev;
			if (prev != null) {
				filterEdge = prev.filterEdge;
				if (prev.prev == null && incomingEdge == filterEdge) {
					//filtering condition already satisfied
					filterEdge = null;
				}
			}
		}

		public boolean visiteddVertex(Triple t) {
			if(prev!=null){
				return prev.vertex.equals(t.getObject());
			}
			return false;
		}

		static TNode createNode(Node vertex, Node incomingEdge, TNode prev){
			return new TNode(vertex,incomingEdge, prev);
		}

		//int cnt=0;
		boolean visitedEdge(Triple t) {
			if (prev != null) {
				 boolean visited=vertex.equals(t.getObject()) && incomingEdge.equals(t.getPredicate())
						&& prev.vertex.equals(t.getSubject()) || prev.visitedEdge(t);
				 //System.err.println(visited);
				 if(visited)
				 return visited;
			}
			return false;
		}
		public String toString(){
			return prev==null? "("+vertex+")"
							  : String.format("(%s)-%s-(%s)", prev.vertex, incomingEdge,vertex);
		}
	}

}
