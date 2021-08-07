package centre.insight.org.cache;


public class LocalPaths {

	String localPath;
	String startNode;
	String endNode;
	
	public LocalPaths(String localPath, String startNode, String endNode) {
		
		this.localPath= localPath;
		this.startNode=startNode;
		this.endNode= endNode;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getStartNode() {
		return startNode;
	}

	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	public String getEndNode() {
		return endNode;
	}

	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}
	
	
	
}
