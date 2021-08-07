package org.insight.centre.feds2.feds2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



public interface GraphIndex<V,E> {

	 Iterator<Edge<V,E>> lookupEdges(V source, V target);
}
