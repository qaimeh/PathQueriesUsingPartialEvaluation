package org.insight.centre.feds.pathmerger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.insight.centre.feds2.feds2.Edge;
import org.insight.centre.feds2.feds2.Path;


import at.ac.wu.graphsense.Util;
import at.ac.wu.graphsense.rdf.RDFGraphIndex;
import at.ac.wu.graphsense.rdf.RDFPathExprFactory;
import at.ac.wu.graphsense.search.BidirectionalTopK;
import at.ac.wu.graphsense.search.patheval.PathArbiter;
import at.ac.wu.graphsense.search.patheval.RegExpPathArbiter;

public class CunccurenListUpdate {

	String startNode, targetNode;
	
	List<String> afterMergingLst= new ArrayList<>();
	
	
	public CunccurenListUpdate(String startNode, String targetNode){
		this.startNode=startNode;
		this.targetNode=targetNode;
	 	//this.recursion(list);
	}
	public static void main(String[] args) {
		
		CopyOnWriteArraySet<String> d1= new CopyOnWriteArraySet<>();
		CopyOnWriteArrayList<String> d2 = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<String> d3 = new CopyOnWriteArrayList<>();
		
		
		d1.add("http://node-A-<http://property-p7>-http://node-B");
		d1.add("http://node-A-<http://property-p7>-http://node-B-<http://property-p7>-http://node-E");
		d1.add("http://node-F-<http://property-p1>-http://node-K-<http://property-p3>-http://node-A");
		
		d1.add("http://node-C-<http://property-p6>-http://node-D");
		d1.add("http://node-D-<http://property-p6>-http://node-E");
		d1.add("http://node-B-<http://property-p8>-http://node-C");
		
		
		//createGraph(d1);
		
		//d1.add("B-C");
		//d1.add("A-C");
		
		
		//CopyOnWriteArrayList<String> allLists= new CopyOnWriteArrayList<>();
		//allLists.addAll(d1);
		//allLists.addAll(d2);
		//allLists.addAll(d3);

		String source= "http://node-F";
		String target= "http://node-E";
		CunccurenListUpdate cunc=new CunccurenListUpdate(source, target);
		cunc.recursion(d1);
		

	
		
	}
	
	public  Collection<List<Edge<Node, Node>>>  getCompletePath(CopyOnWriteArraySet<String> d1, org.apache.jena.sparql.path.Path pathExp) throws Exception{
		
		System.out.println("model creation starts");
		List<Path> paths;
		
		Model mdl= ModelFactory.createDefaultModel();
		
		Iterator<String> itr = d1.iterator();
	
		while(itr.hasNext()) {
			
			String singlePath = String.valueOf(itr.next());
			
			Pattern p = Pattern.compile("\\<.*?\\>");
		    Matcher m = p.matcher(singlePath);
		
		    
			String[] array = singlePath.replaceAll("\\-<.*?\\>-", ",").split(",");
			List<String> arrayList= Arrays.asList(array);
			
			CopyOnWriteArrayList <String>cpyWrLst= new CopyOnWriteArrayList(arrayList);
				
				while(m.find()){
					
					for(String outer: cpyWrLst){
						for(String inner: cpyWrLst){
						if(!outer.equals(inner)){
							Property prp= mdl.createProperty(m.group().replace("<", "").replace(">", ""));
							mdl.createResource(outer).addProperty(prp, mdl.createResource(inner));
							cpyWrLst.remove(outer);
							break;
							}
						}
						break;
					}
			
				}
		}
		
		RDFGraphIndex graph = new RDFGraphIndex((mdl.getGraph()));
		
		System.out.println("bidirectional algo starts!!");
		BidirectionalTopK bidir= new BidirectionalTopK<>(graph);
		if( pathExp!=null ){
            PathArbiter<Node,Node> parb =
                    new RegExpPathArbiter<>(RDFPathExprFactory.createPathExpr(pathExp));
            bidir.setPathArbiter(parb);
        }
		
	
		Collection<List<Edge<Node,Node>>> results = bidir.run(ResourceFactory.createResource(this.startNode).asNode(), ResourceFactory.createResource(this.targetNode).asNode() ,  32);
		//System.err.println(results);
		/*for( Collection<Edge<Node,Node>> r : results ){
			//System.out.println(Util.format(r,graph));
			System.out.println(Util.format(r,graph));
			}*/
		
		//System.out.println("baseline algo starts!!");
		//paths=new BaselineAlgo().run(this.startNode, this.targetNode, null, 20000, mdl);
		
		//mdl.write(System.out,"N-Triples");
		
		return results;
	}
	
/*	private void recursionRDF(CopyOnWriteArrayList<String> d1){
		
		for (String comList : d1) {

			for (String rpeat : d1) {
				if (!rpeat.equals(comList))
					if (d1.indexOf(comList) != (d1.lastIndexOf(d1.size() - 1))
							&& comList.substring(comList.length() - 1).equals(rpeat.substring(0, 1))) {

						String con = comList.concat("-").concat(rpeat);
						if (con.startsWith("F") && con.endsWith("E")) {
							System.out.println("full path: " + con);
							continue;
						}
						if (!d1.contains(con)) {
							d1.add(con);
						} else {
							// do nothing
						}

					}
			}
			d1.remove(comList);
			if (!d1.isEmpty())
				recursion(d1);
		}
	}*/
	
	public List<String> recursion(CopyOnWriteArraySet<String> d1){
		
		if(d1.contains(startNode) || d1.contains(targetNode)){
			System.out.println("contains either source OR target");
			return null;
		}
		for (String comList : d1) {

			for (String rpeat : d1) {
				if (!rpeat.equals(comList)){
					String comListStartNode = pathFirstNode(comList);
					String rpeatStartNode= pathFirstNode(rpeat);
					
					String comListLastEndNode= pathLastNode(comList);
					String rpeatLastNode= pathLastNode(rpeat);
							
					//if (d1.indexOf(comList) != (d1.lastIndexOf(d1.size() - 1))){
						
						if(comListLastEndNode.equals(rpeatStartNode)) {
								
						String con = comList.concat("--").concat(rpeat);
						
						
						if (!d1.contains(con)) {
							
							if(printFullPaths(con))
								d1.remove(con);
								//System.err.println("full");
							
							d1.add(con);
						} else {
							// do nothing
							
						}
					} else if(rpeatLastNode.equals(comListStartNode)){
						String con = rpeat.concat("--").concat(comList);
					
						if (!d1.contains(con)) {
							if(printFullPaths(con))
								d1.remove(con);
								//System.err.println("full");
							
							d1.add(con);
						} else {
							// do nothing
						
						}
					}
						
					
				//	}
			}
			}
			d1.remove(comList);
			if (!d1.isEmpty())
				recursion(d1);
		}
		
		return afterMergingLst;
	}
	
	private boolean printFullPaths(String path){
		if (path.startsWith(this.startNode) && path.endsWith(this.targetNode)) {
			System.err.println("full path: " + path);
			afterMergingLst.add(path);
			return true;
		}else{
			System.out.println("not found");
			return false;}
	}
	  private String pathLastNode(String path){
	    	
	    	return path.substring(path.lastIndexOf(">-") + 2);
	    }
	    
	    private String pathFirstNode(String path){
	    	return path.split("-<")[0];
	    	
	    }

}
