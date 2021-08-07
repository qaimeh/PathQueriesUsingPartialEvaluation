package centre.insight.org.cache;

public class RemotePaths {

	String remotePath;
	String startNode;
	String endNode;
	
	public RemotePaths(String remotePath, String startNode, String endNode) {
		
		this.remotePath=remotePath;
		this.startNode=startNode;
		this.endNode=endNode;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
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
