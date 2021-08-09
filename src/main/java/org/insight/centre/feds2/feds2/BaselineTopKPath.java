package org.insight.centre.feds2.feds2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.insight.centre.feds.pathmerger.CunccurenListUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import centre.insight.org.cache.TestValues;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BaselineTopKPath < V, E > extends TopKAlgorithm implements ModelInit {

  final Logger logger = LoggerFactory.getLogger(BaselineTopKPath.class);
  LinkedList < TNode > q;

  boolean reversed = false, LOCAL = false, REMOTE = false;
  protected Node startNode, targetNode;
  protected Node filterEdge;
  protected Model gIdx;
  protected org.apache.jena.sparql.path.Path pathExp;
  LoadingCache < String, TestValues < V >> loadcache;

  String LOCAL_ENDPOINT = "http://localhost:3040/test1/query";
  String ENDPOINT1 = "http://localhost:3041/test2/query";
  String ENDPOINT2 = "http://localhost:3042/test3/query";
  String ENDPOINT3 = "http://localhost:3043/test4/query";

  //--------

  /*String LOCAL_ENDPOINT="http://10.196.2.224:3011/AT4/query";

      String ENDPOINT1="http://10.196.2.224:3012/BT4/query";
  	String ENDPOINT2="http://10.196.2.224:3013/CT4/query";
  	String ENDPOINT3= "http://10.196.2.224:3014/DT4/query";*/

  //--------

  /*// this is for the evaluation dataset*/
  /*	String LOCAL_ENDPOINT="http://10.196.2.224:3001/eva/query";
      String ENDPOINT1="http://10.196.2.224:3002/evb/query";
      String ENDPOINT2="http://10.196.2.224:3003/evc/query";
      String ENDPOINT3="http://10.196.2.224:3004/evd/query";
      String ENDPOINT4="http://10.196.2.224:3005/eve/query";
      String ENDPOINT5="http://10.196.2.224:3006/evf/query";
      String ENDPOINT6="http://10.196.2.224:3007/evg/query";
      String ENDPOINT7="http://10.196.2.224:3008/evh/query";
      String ENDPOINT8="http://10.196.2.224:3009/evi/query";
      String ENDPOINT9="http://10.196.2.224:3010/evj/query";*/

  /*String LOCAL_ENDPOINT="http://10.196.2.224:3055/disease/query";
  String ENDPOINT1="http://10.196.2.224:3051/gene/query";
  String ENDPOINT2= "http://10.196.2.224:3052/panther/query";
  String ENDPOINT3= "http://10.196.2.224:3053/protein/query";
  String ENDPOINT4= "http://10.196.2.224:3054/do/query";
  String ENDPOINT5= "http://10.196.2.224:3057/variant/query";
  String ENDPOINT6= "http://10.196.2.224:3056/hpo/query";
  String ENDPOINT7="http://10.196.2.224:3050/phenotype/query";*/

  //String LOCAL_ENDPOINT="http://10.196.2.224:3001/ev1/query";	
  //String ENDPOINT1="http://10.196.2.224:3031/EB/query";
  //String ENDPOINT2= "http://10.196.2.224:3032/EC/query";
  //String ENDPOINT3= "http://10.196.2.224:3033/ED/query";

  /*	String LOCAL_ENDPOINT="http://localhost:3055/disease/query";
  	String ENDPOINT1="http://localhost:3051/gene/query";
  	String ENDPOINT2= "http://localhost:3052/panther/query";
  	String ENDPOINT3= "http://localhost:3053/protein/query";
  	String ENDPOINT4= "http://localhost:3054/do/query";
  	String ENDPOINT5= "http://localhost:3057/variant/query";
  	String ENDPOINT6= "http://localhost:3056/hpo/query";
  	String ENDPOINT7="http://localhost:3050/phenotype/query";*/

  List < String > beforeFilterEndp = new ArrayList < > ();
  List < String > afterFilterEndp = new ArrayList < > ();

  public BaselineTopKPath(Model hdtIdx) {
    gIdx = hdtIdx;
  }
  @Override
  public void init(Properties config) throws IOException {

  }

  public void init(Model hdtIdx) {
    gIdx = hdtIdx;
  }

  public void reset() {
    solutions = new LinkedList < > ();
    q = new LinkedList < > ();

  }

	public List<Node[]> _topk(Node start, Node end, int k, org.apache.jena.sparql.path.Path path) throws Exception {
		Collection<List<Edge<V, E>>> results = run(start, end, null, k, path);

		return null; // Util.formatToString(results,gIdx);
	}

	public Collection<List<Edge<V, E>>> run(Node start, Node end, Node filter, int k,
			org.apache.jena.sparql.path.Path path) throws Exception {

		startNode = start;
		targetNode = end;
		this.pathExp = path;
		filterEdge = filter == null ? null : null; // gIdx.getProperty(filter.toString()).asNode();

		beforeFilterEndp.add(LOCAL_ENDPOINT);
		beforeFilterEndp.add(ENDPOINT1);
		beforeFilterEndp.add(ENDPOINT2);
		beforeFilterEndp.add(ENDPOINT3);
		/*
		 * beforeFilterEndp.add(ENDPOINT4); beforeFilterEndp.add(ENDPOINT5);
		 * beforeFilterEndp.add(ENDPOINT6); beforeFilterEndp.add(ENDPOINT7);
		 * beforeFilterEndp.add(ENDPOINT8); beforeFilterEndp.add(ENDPOINT9);
		 */

		// check the local endpoint if source is available
		if (ASKRequest.ASK(start.toString(), LOCAL_ENDPOINT)) {
			LOCAL = true;
			return queueItr(k);

		} else {

			REMOTE = true;
			return queueItr(k);
		}

	}

	private Collection<List<Edge<V, E>>> queueItr(int k) throws Exception {

		// print memory used
		PrintHeapUsage.printGB();
		// load cache
		loadcache = cache();

		TNode n = TNode.createNode(startNode, null, null);
		q = new LinkedList<>();
		q.add(n);

		long startTime = System.currentTimeMillis();

		int qsize = 0;
		while (!q.isEmpty()) {
			if (qsize < q.size()) {
				qsize = q.size();
			}
			TNode current = q.poll();
			visit(current);
			if (solutions.size() >= k || q.size() > 1000) {
				System.out.println("Max queue size: " + qsize);
				break;
			}
		}

		// write federated paths to file
		logger.info("traversal Done!");
		// Util.writePathToFile(sourceToAllTemp);

		CopyOnWriteArraySet<String> mergeAllPaths = new CopyOnWriteArraySet<String>();

		for (Entry<String, TestValues<V>> outerMap : loadcache.asMap().entrySet()) {

			// loadcache.get(LOCAL_ENDPOINT).getpPaths();
			// loadcache.get(LOCAL_ENDPOINT)
			for (Entry<String, TestValues<V>> innerMap : loadcache.asMap().entrySet()) {

				// System.out.println("outer visited
				// nodes"+outerMap.getValue().getVisitedNodes());
				// System.out.println("inner visited
				// nodes"+innerMap.getValue().getVisitedNodes());

				// compare two list and find disjunction element from oute to
				// inner
				if (outerMap.getValue().getVisitedNodes() == null || innerMap.getValue().getVisitedNodes() == null)
					continue;
				if (outerMap.getKey().equals(innerMap.getKey()))
					continue;

				Collection<?> diffElem = CollectionUtils.disjunction(outerMap.getValue().getVisitedNodes(),
						innerMap.getValue().getVisitedNodes());
				if (diffElem.isEmpty())
					continue;

				Set<Node> newSet = new HashSet<Node>((Collection<? extends Node>) diffElem);

				logger.debug("dijunction elements: {} " , newSet.size());
				List<String> endp = new ArrayList<>();
				endp.add(outerMap.getKey());
				endp.add(innerMap.getKey());

				getpathFromRemotesNewFunction(newSet, endp);
			}

			Set<String> temp;
			if (outerMap.getValue().getFullPaths() != null)
				solutions.add(new ArrayList<>(outerMap.getValue().getFullPaths()));
			if (outerMap.getValue().getpPaths() != null)

				if (loadcache.get(LOCAL_ENDPOINT).getpPaths() != null) {
					mergeAllPaths.addAll(outerMap.getValue().getpPaths());
					mergeAllPaths.addAll(loadcache.get(LOCAL_ENDPOINT).getpPaths());

				}
		}

		long end0 = System.currentTimeMillis();
		System.err.println("federation is over: with time taken:" + (end0 - startTime));
		Collection<List<Edge<Node, Node>>> mergedPaths = new CunccurenListUpdate(this.startNode.toString(),
				this.targetNode.toString()).getCompletePath(mergeAllPaths, this.pathExp);

		System.err.println(mergedPaths.size());
		for (Collection<Edge<Node, Node>> pth : mergedPaths) {
			ArrayList tempPLst = new ArrayList<>();
			tempPLst.add(pth);
			solutions.add(tempPLst);
		}
		return solutions;
	}

	int i = 0;

	private void visit(TNode current) throws Exception {

		i++;

		if (LOCAL) {

			TNode next = null;

			for (StmtIterator stmtItr = gIdx.listStatements(gIdx.getResource(current.vertex.toString()), null,
					(RDFNode) null); stmtItr.hasNext();) {

				Triple triple = stmtItr.next().asTriple();

				if (triple.getPredicate().getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
					continue;
				}

				if (q.contains(triple)) {
					continue;
				}
				if (triple.getObject().isLiteral()) {
					continue;
				}

				if (current.visitedEdge(triple)) {
					continue;
				}

				if (current.visitedVertex(triple)) {
					continue;
				}

				next = TNode.createNode(triple.getObject(), triple.getPredicate(), current);

				// first hop: if object of current triple is the actual target
				// node
				if (triple.getObject().equals(targetNode)) {

					Path localPath = trace(next, new Path(Collections.singleton(targetNode)));

					if (!reversed) {
						localPath.reverse();
					}

					Set<String> pathList = new HashSet<>();
					pathList.add(replaceBrkts(localPath.toString()));

					ArrayList<Node> fPathsNodesList = localPath.vertSeq;

					// visited nodes for this path
					Set<Node> visitedNodes = new LinkedHashSet<>(fPathsNodesList);

					for (Node node : visitedNodes) {
						// if current node is target node SKIP
						if (node.equals(targetNode))
							continue;

						if (!loadcache.asMap().containsKey(LOCAL_ENDPOINT)) {

							TestValues<V> t1Values = new TestValues<V>(visitedNodes, pathList, null);
							loadcache.put(LOCAL_ENDPOINT, t1Values);
						} else {
							if (loadcache.get(LOCAL_ENDPOINT).getFullPaths() == null) {
								loadcache.get(LOCAL_ENDPOINT).setFullPaths(pathList);
								loadcache.get(LOCAL_ENDPOINT).getVisitedNodes().addAll(visitedNodes);
							} else {
								loadcache.get(LOCAL_ENDPOINT).getFullPaths().addAll(pathList);
								loadcache.get(LOCAL_ENDPOINT).getVisitedNodes().addAll(visitedNodes);
							}

						}

						getpathFromRemotesNewFunction(visitedNodes, beforeFilterEndp);

					}

					// lst= new LinkedList<>();
					// lst.add(replaceBrkts(localPath.toString()));
					// solutions.add(replaceBrkts(localPath.toString()));
					// solutions.add(lst);

				} else {
					// if object of the current triple is not the actual target
					// node call the remote requests
					Path partialPaths = trace(next, new Path(Collections.singleton(next.vertex)));
					if (!reversed) {
						partialPaths.reverse();
					}

					List<Path> pathList = new LinkedList<>();
					pathList.add(partialPaths);
					// System.out.println("patial path");
					ArrayList<Node> pPathsNodesList = partialPaths.vertSeq;

					// visited nodes for this path
					Set<Node> visitedNodes = new LinkedHashSet<>(pPathsNodesList);
					Set<String> pPathList = new HashSet<>();
					pPathList.add(replaceBrkts(partialPaths.toString()));

					if (!loadcache.asMap().containsKey(LOCAL_ENDPOINT)) {

						TestValues<V> t1Values = new TestValues<V>(visitedNodes, null, pPathList);
						loadcache.put(LOCAL_ENDPOINT, t1Values);

					} else {
						if (loadcache.get(LOCAL_ENDPOINT).getpPaths() == null) {
							loadcache.get(LOCAL_ENDPOINT).setpPaths(pPathList);
							loadcache.get(LOCAL_ENDPOINT).getVisitedNodes().addAll(visitedNodes);
						} else {
							loadcache.get(LOCAL_ENDPOINT).getpPaths().addAll(pPathList);
							loadcache.get(LOCAL_ENDPOINT).getVisitedNodes().addAll(visitedNodes);
						}
					}

					getpathFromRemotesNewFunction(visitedNodes, beforeFilterEndp);

					q.add(next);
				}
			}

			if (!current.vertex.equals(targetNode) && i < 5) {
				Set<Node> vistedNodes = new HashSet<>();
				vistedNodes.add(current.vertex);
				// skip if i >5
				System.err.println(i);

				Collection<String> filtered = Collections2.filter(beforeFilterEndp,
						Predicates.not(Predicates.equalTo(LOCAL_ENDPOINT)));
				List<String> withOutLocalEndp = Lists.newArrayList(filtered);
				getpathFromRemotesNewFunction(vistedNodes, beforeFilterEndp);
			}
		}
		// if source doesn't exist in local dataset
		if (REMOTE) {

			Set<Node> strNode = new HashSet<>();
			strNode.add(startNode);

			getpathFromRemotesNewFunction(strNode, beforeFilterEndp);
		}

	}

	private void getpathFromRemotesNewFunction(Set<Node> visitedNodes, List<String> beforeFilterEndp2)
			throws InterruptedException, ExecutionException {

		for (String endp : beforeFilterEndp2) {
			// if local dataset skip it
			// if(endp.equals(LOCAL_ENDPOINT))
			// continue;
			if (loadcache.asMap().containsKey(endp)) {
				for (Node node : visitedNodes) {
					if (loadcache.asMap().get(endp).getVisitedNodes().contains(node)) {
						// do nothing
						continue;
					} else {

						// send remote request and update the cache
						/* get list of paths */
						Map<String, Set<Node>> pathsWithNodes = remoteRequest(node, targetNode, endp, 1);

						if (pathsWithNodes.isEmpty()) {
							// only update the visited node for this endpoint

							loadcache.get(endp).getVisitedNodes().add(node);
						} else {
							// filter out full and partial paths from this
							// results and update cache accordingly

							for (Map.Entry<String, Set<Node>> pathsContnt : pathsWithNodes.entrySet()) {

								List<Node> firstLast = new ArrayList<>(pathsContnt.getValue());
								// full paths
								if (firstLast.get(0).equals(startNode.toString())
										&& firstLast.get(firstLast.size() - 1).equals(targetNode.toString())) {

									if (loadcache.get(endp).getFullPaths() == null) {
										Set<String> fpaths = new HashSet<>();
										fpaths.add(pathsContnt.getKey());
										loadcache.get(endp).setFullPaths(fpaths);
										loadcache.get(endp).getVisitedNodes().addAll(pathsContnt.getValue());
									} else {
										loadcache.get(endp).getFullPaths().add(pathsContnt.getKey());
										loadcache.get(endp).getVisitedNodes().addAll(pathsContnt.getValue());
									}
								} else {
									// partial paths

									if (loadcache.get(endp).getpPaths() == null) {
										Set<String> ppaths = new HashSet<>();
										ppaths.add(pathsContnt.getKey());
										loadcache.get(endp).setpPaths(ppaths);
										loadcache.get(endp).getVisitedNodes().addAll(pathsContnt.getValue());
									} else {
										loadcache.get(endp).getpPaths().add(pathsContnt.getKey());
										// System.err.println("partial
										// paths"+loadcache.get(endp).getpPaths());
										loadcache.get(endp).getVisitedNodes().addAll(pathsContnt.getValue());
									}
								}
							}

						}

					}

				}

			} else {
				// if endpoint doesn't exist send request to remote endpoints
				for (Node node : visitedNodes) {

					Map<String, Set<Node>> pathsWithNodes = remoteRequest(node, targetNode, endp, 1);
					if (pathsWithNodes.isEmpty()) {
						// only update the visited node for this endpoint

						if (!loadcache.asMap().containsKey(endp)) {
							Set<Node> singleNodeLst = new HashSet<>();
							singleNodeLst.add(node);

							TestValues<V> t1Values = new TestValues<V>(singleNodeLst, null, null);
							loadcache.put(endp, t1Values);
						} else {
							loadcache.get(endp).getVisitedNodes().add(node);
						}

					} else {
						// filter out full and partial paths from this results
						// and update cache accordingly

						for (Map.Entry<String, Set<Node>> pathsContnt : pathsWithNodes.entrySet()) {

							List<Node> firstLast = new ArrayList<>(pathsContnt.getValue());
							Set<Node> thisPathVisitedNodes = new HashSet<>(firstLast);
							// full paths
							if (firstLast.get(0).equals(startNode.toString())
									&& firstLast.get(firstLast.size() - 1).equals(targetNode.toString())) {

								if (!loadcache.asMap().containsKey(endp)) {
									Set<String> fullPathSingleLst = new HashSet<>();
									fullPathSingleLst.add(pathsContnt.getKey());
									TestValues<V> t1Values = new TestValues<V>(thisPathVisitedNodes, fullPathSingleLst,
											null);

									loadcache.put(endp, t1Values);

								} else {
									loadcache.get(endp).getVisitedNodes().addAll(thisPathVisitedNodes);
									loadcache.get(endp).getFullPaths().add(pathsContnt.getKey());
								}
							} else {
								// partial paths

								if (!loadcache.asMap().containsKey(endp)) {
									Set<String> pPathSingleLst = new HashSet<>();
									pPathSingleLst.add(pathsContnt.getKey());
									TestValues<V> t1Values = new TestValues<V>(thisPathVisitedNodes, null,
											pPathSingleLst);
									loadcache.put(endp, t1Values);
								} else {
									if (loadcache.get(endp).getpPaths() == null) {
										Set<String> partPathSingleLst = new HashSet<>();
										partPathSingleLst.add(pathsContnt.getKey());

										loadcache.get(endp).setpPaths(partPathSingleLst);
										loadcache.get(endp).setVisitedNodes(thisPathVisitedNodes);
									} else {
										loadcache.get(endp).getpPaths().add(pathsContnt.getKey());
										loadcache.get(endp).getVisitedNodes().addAll(thisPathVisitedNodes);
									}
								}

							}
						}

					}

				}

			}
		}

	}

	private Map<String, Set<Node>> remoteRequest(Node node, Node targetNode2, String endp, int i)
			throws InterruptedException, ExecutionException {
		ExecutorService execService = Executors.newFixedThreadPool(10);
		Map<String, Set<Node>> rmtPaths = null;

		RmoteQuery<V> rmReq = new RmoteQuery<V>(node, targetNode, endp, i);
		Future<List<PathContent<V>>> future = execService.submit(rmReq);

		rmtPaths = breakIntoNode(future.get());
		// shutdown the service
		execService.shutdown();
		return rmtPaths;

	}

	private String replaceBrkts(String path) {

		path = path.replaceAll("\\[", "").replaceAll("\\]", "");
		return path;
	}

	private Map<String, Set<Node>> breakIntoNode(List<PathContent<V>> rmPaths) {

		Map<String, Set<Node>> pathWithNodes = new HashMap<>();

		Iterator<PathContent<V>> itr = rmPaths.iterator();
		while (itr.hasNext()) {

			String singlePath = String.valueOf(itr.next());
			String[] array = singlePath.replaceAll("\\-<.*?\\>-", ",").split(",");
			List<String> arrayList = Arrays.asList(array);

			Set<Node> nodes = new LinkedHashSet<>();

			for (String arrayLoop : arrayList) {
				nodes.add(NodeFactory.createURI(arrayLoop));
			}
			// nodes.addAll(Arrays.asList(array));

			pathWithNodes.put(singlePath, nodes);
		}

		return pathWithNodes;

	}

	private

	static Path trace(TNode n, Path p) {
		if (n.prev == null) {
			return p;
		}
		p.appendEdge(n.incomingEdge, n.prev.vertex);
		return trace(n.prev, p);
	}

	public static class PathContent<V> {

		List<V> partialPaths;
		Set<V> pathNodes;

		public PathContent(List<V> partialPaths, Set<V> pPathsNodes) {

			this.partialPaths = partialPaths;
			this.pathNodes = pPathsNodes;

		}

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
					// filtering condition already satisfied
					filterEdge = null;
				}
			}
		}

		public boolean visitedVertex(Triple triple) {
			if (prev != null) {
				return prev.vertex.equals(triple.getObject());
			}
			return false;
		}

		static TNode createNode(Node vertex, Node incomingEdge, TNode prev) {
			return new TNode(vertex, incomingEdge, prev);
		}

		boolean visitedEdge(Triple t) {
			if (prev != null) {
				return vertex == t.getObject() && incomingEdge == t.getPredicate() && prev.vertex == t.getSubject()
						|| prev.visitedEdge(t);
			}
			return false;
		}

		public String toString() {
			return prev == null ? "(" + vertex + ")" : String.format("(%s)-%s-(%s)", prev.vertex, incomingEdge, vertex);
		}
	}

}