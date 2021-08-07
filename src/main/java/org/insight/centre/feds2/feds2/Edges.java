package org.insight.centre.feds2.feds2;
/**
 * Created by vadim on 18.05.16.
 */
public interface Edges<T> {
    Iterable<T> vertexSequence();
    Iterable<Edge> edgeSequence();
}
