package org.insight.centre.feds2.feds2;



/**
 * Created by "Vadim Savenkov" on 07.12.16.
 */
public interface EdgeDictionary<E,K> {
   K edgeEntry(E key );
   E edgeKey(K entry );
}
