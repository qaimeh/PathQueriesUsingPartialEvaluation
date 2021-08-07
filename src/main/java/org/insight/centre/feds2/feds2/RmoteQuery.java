package org.insight.centre.feds2.feds2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.core.Var;
import org.insight.centre.feds2.feds2.BaselineTopKPath.PathContent;



public class RmoteQuery<V> implements Callable<List<PathContent<V>>> {

	Node pathNode, targetNode;
	String endpoint;
	int k;
	
	public RmoteQuery(Node pathNode, Node targetNode, String endpoint, int k ) {
		
		this.pathNode= pathNode;
		this.targetNode=targetNode;
		this.endpoint=endpoint;
		this.k=k;
	}
	
	/*public static List<String> FederateRequest(Node pathNode, Node targetNode, String endpoint, int k){
		
		List<String> lstRemotePaths= new ArrayList<>();
		
		String qry=  "PREFIX : <http://dbpedia.org/resource/>\n"
		        + "PREFIX ppfj: <java:org.centre.insight.property.path.>\n"
		        + "SELECT *"
		        + "WHERE {  ?path ppfj:topk (<"+pathNode+"> <"+targetNode+"> "+k+") }";
		        
			
	   Query query = QueryFactory.create(qry);
       try  {
    	   QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
           ResultSet results = qexec.execSelect() ;
           for ( ; results.hasNext() ; ) {

               QuerySolution soln = results.nextSolution();
               StringBuilder sb = new StringBuilder();

               for (Var v : query.getProjectVars() ) {
                    
                       RDFNode val = soln.get(v.getVarName());
                       sb.append(val==null? "(nil)" : "("+val.toString()+")");
                       lstRemotePaths.add(pathFormat(val.toString()));
               }
               System.out.println(sb);
           }
       
          
	}catch(QueryParseException parseExc) {parseExc.printStackTrace();}

	
       return lstRemotePaths;
	}
*/

	private  String pathFormat(String path) {
		
		String rmPath= path.replaceAll("\\[", "").replaceAll("\\]", "");
		
		return rmPath;
	}


	@Override
	public List<PathContent<V>> call() throws Exception {
	List<V> lstRemotePaths= new ArrayList<>();
		
		String qry=  "PREFIX : <http://dbpedia.org/resource/>\n"
		        + "PREFIX ppfj: <java:org.centre.insight.property.path.>\n"
		        + "SELECT *"
		        + "WHERE {  ?path ppfj:topk (<"+pathNode+"> <"+targetNode+"> "+k+") }";
		        
			
	   Query query = QueryFactory.create(qry);
       try  {
    	   QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
           ResultSet results = qexec.execSelect() ;
           for ( ; results.hasNext() ; ) {

               QuerySolution soln = results.nextSolution();
               StringBuilder sb = new StringBuilder();

               for (Var v : query.getProjectVars() ) {
                    
                       RDFNode val = soln.get(v.getVarName());
                       sb.append(val==null? "(nil)" : "("+val.toString()+")");
                       lstRemotePaths.add((V) pathFormat(val.toString()));
               }
               
           }
       
           qexec.close();
           
          
	}catch(QueryParseException parseExc) {parseExc.printStackTrace();}

	
       return (List<PathContent<V>>) lstRemotePaths;
       }
}
