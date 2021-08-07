package centre.insight.org.cache;


import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;


public class CacheStats<V> {


	V endp;

	Set<V> visitedNodes;
	List<FullPaths<V>> fP;
	List<PartialPaths<V>> pP;
	
	
	public CacheStats(V endp, List<FullPaths<V>> fpL, List<PartialPaths<V>> ppL, Set<V> visitedNodes) {
		
		this.endp=endp;
		this.visitedNodes=visitedNodes;
		this.fP=fpL;
		this.pP=ppL;
		
	}

	public CacheStats(Node key) {
		// TODO Auto-generated constructor stub
	}

	public List<FullPaths<V>> getfP() {
		return fP;
	}
	public void setfP(List<FullPaths<V>> fP) {
		this.fP = fP;
	}


	public List<PartialPaths<V>> getpP() {
		return pP;
	}
	public void setpP(List<PartialPaths<V>> pP) {
		this.pP = pP;
	}


	public V getEndp() {
		return endp;
	}
	public void setEndp(V endp) {
		this.endp = endp;
	}







	public Set<V> getVisitedNodes() {
		return visitedNodes;
	}

	public void setVisitedNodes(Set<V> visitedNodes) {
		this.visitedNodes = visitedNodes;
	}







	public static class FullPaths <V> {
		
		V fullP;
		V startNode;
		V endNode;
		
		public FullPaths(V fullP, V startNode, V endNode  ) {
			
			this.fullP=fullP;
			this.startNode=startNode;
			this.endNode=endNode;
		}

		public V getFullP() {
			return fullP;
		}

		public void setFullP(V fullP) {
			this.fullP = fullP;
		}

		public V getStartNode() {
			return startNode;
		}

		public void setStartNode(V startNode) {
			this.startNode = startNode;
		}

		public V getEndNode() {
			return endNode;
		}

		public void setEndNode(V endNode) {
			this.endNode = endNode;
		}
		
		
		
		
		
	}
	
	
	
	
	public static class PartialPaths <V> {
		
		V partialP;
		V startNode;
		V endNode;
		
		public PartialPaths(V partialP, V startNode, V endNode  ) {
			
			this.partialP=partialP;
			this.startNode=startNode;
			this.endNode=endNode;
		}

		public V getPartialP() {
			return partialP;
		}

		public void setPartialP(V partialP) {
			this.partialP = partialP;
		}

		public V getStartNode() {
			return startNode;
		}

		public void setStartNode(V startNode) {
			this.startNode = startNode;
		}

		public V getEndNode() {
			return endNode;
		}

		public void setEndNode(V endNode) {
			this.endNode = endNode;
		}
		
		
		
		
	}
	
}


