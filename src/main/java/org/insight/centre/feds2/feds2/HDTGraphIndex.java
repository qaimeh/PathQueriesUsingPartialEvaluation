package org.insight.centre.feds2.feds2;
/*
import java.io.IOException;
import java.util.*;

class HDTGraphIndex implements GraphIndex {
    
    public final Dictionary dict;


    public HDTGraphIndex(String dataset) throws IOException {
        this(dataset,false);
    }

    public HDTGraphIndex(String dataset, boolean indexed) throws IOException {
        hdt = indexed?  HDTManager.loadIndexedHDT(dataset, null)
                      : HDTManager.loadHDT(dataset, null);
        dict = hdt.getDictionary();
    }

    public int calcNumPaths(List<Integer> solutionpaths, int edge) throws NotFoundException {
        int totalpaths = 0;
        //for (List<Integer> s : solutionpaths) {
            //ArrayList tmp = new ArrayList(s);
            int possiblepaths = 1;
            for(int i = 0; i < solutionpaths.size() - 1; i++) {
                int from = (int) solutionpaths.get(i);
                int to = (int) solutionpaths.get(i + 1);
                IteratorTripleID result = null;
                if(i == 0 && edge != 0) {
                    result = lookUp(from, edge, to);
                } else {
                    result = lookUp(from, 0, to);
                }
                int countedges = 0;
                while(result.hasNext()) {
                    TripleID ti = result.next();
                    countedges++;
                }
                possiblepaths = possiblepaths * countedges;
            }
            totalpaths = totalpaths + possiblepaths;
        //}
        return totalpaths;
    }

    public int calculateNumberOfPaths(Set<List<Integer>> solutionpaths, int edge) throws NotFoundException {
        int totalpaths = 0;
        for (List<Integer> s : solutionpaths) {
            ArrayList tmp = new ArrayList(s);
            int possiblepaths = 1;
            for(int i = 0; i < tmp.size() - 1; i++) {
                int from = (int) tmp.get(i);
                int to = (int) tmp.get(i + 1);
                IteratorTripleID result = null;
                if(i == 0 && edge != 0) {
                    result = lookUp(from, edge, to);
                } else {
                    result = lookUp(from, 0, to);
                }
                int countedges = 0;
                while(result.hasNext()) {
                    TripleID ti = result.next();
                    countedges++;
                }
                possiblepaths = possiblepaths * countedges;
            }
            totalpaths = totalpaths + possiblepaths;
        }
        return totalpaths;
    }



    public int initLookUp(String element, String type) throws IOException, NotFoundException {
        int el = 0;
        if(type == "subject") {
            el = dict.stringToId(element, TripleComponentRole.SUBJECT);
        }
        else if(type == "object") {
            el = dict.stringToId(element, TripleComponentRole.OBJECT);
        }
        else if(type == "predicate") {
            el = dict.stringToId(element, TripleComponentRole.PREDICATE);
        }
        return el;
    }

    public IteratorTripleID lookUp(int subject, int predicate, int object) throws NotFoundException {
        TripleID tripleid = new TripleID(subject, predicate, object);
        IteratorTripleID it = hdt.getTriples().search(tripleid);
        return it;
    }

    public Collection<Integer> getOutlinks(int nodename, int edge, int target) throws NotFoundException {
        Set<Integer> outNodes = new LinkedHashSet<>();
        IteratorTripleID result = null;
        result = lookUp(nodename, edge, 0);
        while(result.hasNext()) {
            TripleID ti = result.next();
            int o = ti.getObject();
            //Ignore literals
            if(o > hdt.getDictionary().getNshared() && o != target) {
                continue;
            }
            outNodes.add(o);
        }
        return outNodes;
    }

    @Override
    public Dictionary getDict() {
        return dict;
    }

    public int noOfSequences(ArrayList<Integer> one, ArrayList<Integer> two) {
        int seqInOne = 0;
        int start = one.indexOf(two.get(0));
        if(start > 0) {
            for(int i = start; i < one.size(); i++) {
                Integer i_one = one.get(i);
                if(i_one.equals(two.get(0))) {
                    boolean seq = true;
                    for(int a = 1; a < two.size(); a++) {
                        if((i + a) >= one.size() || !one.get(i + a).equals(two.get(a))) {
                            seq = false;
                            break;
                        }
                    }
                    if(seq) {
                        seqInOne++;
                    }
                }
            }
        }
        return seqInOne;
    }

    @Override
    public boolean validPath(ArrayList<Integer> oldPath, ArrayList<Integer> newPath) throws NotFoundException {
        int nos = noOfSequences(oldPath, newPath);
        boolean add = false;
        if(nos == 0) {
            add = true;
        } else {
            IteratorTripleID edges = null;
            edges = lookUp(newPath.get(0), 0, newPath.get(1));
            int c = 0;
            while(edges.hasNext()) {
                edges.next();
                c++;
            }
            add = (nos < c);
        }
        return add;
    }

}
*/