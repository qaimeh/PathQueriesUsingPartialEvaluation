package centre.insight.org.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PathsCache {
	
	//String relevantEndp;
	//List<String> rmtpaths;
	//List<String> endp;
	String node;
	Map<String, List<String>> rmtContents;
	String localPath;
	
	//Set<String> nodesSet;
	
	public PathsCache(String relevantEndp) {
		
		//this.relevantEndp= relevantEndp;
		
		
	}
	
	public PathsCache(String node, String relevantEndp,  List<String> paths) {
		
		//this.relevantEndp= relevantEndp;
		//this.rmtpaths=paths;
		this.node=node;
		
		
	}
	
	public PathsCache(String node,  String localPath,  Map<String, List<String>> rmtContents) {
		
		this.node=node;
		this.rmtContents= rmtContents;
		this.localPath=localPath;
	}


	public PathsCache() {
		// TODO Auto-generated constructor stub
	}


	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Map<String, List<String>> getRmtContents() {
		return rmtContents;
	}

	public void setRmtContents(Map<String, List<String>> rmtContents) {
		this.rmtContents = rmtContents;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	

	
	

}

