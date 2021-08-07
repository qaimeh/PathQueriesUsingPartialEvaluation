package centre.insight.org.cache;

import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;

public class TestValues <V> {

	Set<Node> visitedNodes;
	Set<String> fullPaths;
	Set<String> pPaths;
	public TestValues(Set<Node> visitedNodes2, Set<String> fullPaths, Set<String> pPaths) {
		super();
		this.visitedNodes = visitedNodes2;
		this.fullPaths = fullPaths;
		this.pPaths = pPaths;
	}
	public TestValues(String key) {
		// TODO Auto-generated constructor stub
	}
	public Set<Node> getVisitedNodes() {
		return visitedNodes;
	}
	public void setVisitedNodes(Set<Node> visitedNodes) {
		this.visitedNodes = visitedNodes;
	}
	public Set<String> getFullPaths() {
		return fullPaths;
	}
	public void setFullPaths(Set<String> fullPaths) {
		this.fullPaths = fullPaths;
	}
	public Set<String> getpPaths() {
		return pPaths;
	}
	public void setpPaths(Set<String> pPaths) {
		this.pPaths = pPaths;
	}
}
