package org.insight.centre.feds2.feds2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import centre.insight.org.cache.CachePaths;
import centre.insight.org.cache.CacheStats;
import centre.insight.org.cache.PathsCache;
import centre.insight.org.cache.TestValues;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;


/**
 * Created by jumbrich on 01/06/16.
 */
public abstract class TopKAlgorithm<V,E> implements  ModelInit{

	Collection<List<Edge<V,E>>> solutions = new LinkedList<>();
    protected List<String> sourceToAllTemp = new LinkedList<>();
    
    List<Path> lst= new LinkedList<>();
    List<String> cachedP= new LinkedList<String>();
    
    abstract public void init(Properties config) throws IOException;

    /**
     * Is called before each topk method call
     */
    abstract void reset();

    public List<Node[]> topk(Node start, Node end, int k, org.apache.jena.sparql.path.Path path) throws Exception{
        System.out.println("TopK("+start+" , "+end+" , "+k+" )");

        reset();

        solutions = new LinkedList<>();

        return _topk(start, end, k, path);
    }

    abstract public List<Node[]> _topk(Node start, Node end, int k,org.apache.jena.sparql.path.Path path) throws Exception;
    abstract public Collection<List<Edge<V,E>>> run(Node start, Node end, Node filter, int k, org.apache.jena.sparql.path.Path path) throws Exception;
    
    
    // cache
    public LoadingCache<String, TestValues<V>> cache() {
    	
    LoadingCache <String, TestValues<V>> load=	CacheBuilder.newBuilder().maximumSize(Long.MAX_VALUE) // 150000000
    		.expireAfterAccess(80, TimeUnit.MINUTES)
    		.build(new PathCacheLoader<V>());    
    return load;
    }

}

 class PathCacheLoader <V> extends CacheLoader<String, TestValues<V>>{

	@Override
	public TestValues<V> load(String key) throws Exception {
		// TODO Auto-generated method stub
		return new TestValues <V>(key);
	}
	 
 } 
interface ModelInit{
    public void init(Model hdtIdx);
}