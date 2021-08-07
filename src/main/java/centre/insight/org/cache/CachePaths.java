package centre.insight.org.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.insight.centre.feds2.feds2.BaselineTopKPath.PathContent;
import org.insight.centre.feds2.feds2.Path;

public class CachePaths<V> {

	Node startNode, endNode;
	//List<Node> visitedNodes;
	
	List<Path> completePaths;
	
	//Map<String, List<String>> rmtPaths;
	
	Map<String, List<PathContent<V>>> rmtContent;
	
	public CachePaths(Node key) {
		// TODO Auto-generated constructor stub
	}


	public CachePaths(Node startNode, Node endNode, List<Path> completePaths, Map<String, List<PathContent<V>>> rmtContents) {
		
		
		this.startNode=startNode;
		this.endNode=endNode;
		this.completePaths=completePaths;
		this.rmtContent=rmtContents;
		
	}

	public Node getStartNode() {
		return startNode;
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}

	public List<Path> getCompletePaths() {
		return completePaths;
	}

	public void setCompletePaths(List<Path> completePaths) {
		this.completePaths = completePaths;
	}

	public Map<String, List<PathContent<V>>> getRmtContent() {
		return rmtContent;
	}

	public void setRmtContent(Map<String, List<PathContent<V>>> rmtContent) {
		this.rmtContent = rmtContent;
	}




	
	
	
	
	
	
}
