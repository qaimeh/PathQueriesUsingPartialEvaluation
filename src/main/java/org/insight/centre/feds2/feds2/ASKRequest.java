package org.insight.centre.feds2.feds2;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;

public class ASKRequest {

	
	/*
	 * ASK if only source is available
	 * @param the URI to check 
	 */
	public static boolean ASK(String source, String endpoint) {
		
		//String qry= "ASK where {<"+source+"> ?p ?o}";
		String qry = "SELECT * WHERE{?s ?p ?o "
    			+ "Filter(?s = <"+source+">  || ?o= <"+source+"> ) "
    			+ "}";
		return qryASKExec(qry, endpoint);
	}
	
	/**
	 * ASK if source OR target do exist 
	 * @param source
	 * @param target
	 */
	public static boolean ASK(String source, String target, String endpoint) {
		
		
		String qry= "ASK where {?s ?p ?o."
				+ "FILTER (?s=<"+source+"> || ?o=<"+source+">}";
		return qryASKExec(qry, endpoint);
	}
	
	/*
	 * execute the ask query and return TRUE or FALSE
	 */
	private static boolean qryASKExec(String query, String endpoint) {
		
		QueryExecution qex=null;
		Query qry= null;
		
		boolean chk= false;
		
		try {
			
			qry= QueryFactory.create(query);
			qex= QueryExecutionFactory.sparqlService(endpoint, qry);
			
			chk=qex.execSelect().hasNext();//.execAsk();
			
		}catch (QueryParseException prExcpt) {
			prExcpt.printStackTrace();
		}
		
		if(chk) {
			chk=true;
		}

		qex.close();
		return chk;
	}
	
	
	
}
