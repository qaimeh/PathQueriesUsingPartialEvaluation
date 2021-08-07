package org.insight.centre.feds2.feds2;




import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.NotFoundException;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.util.FileManager;

public class StartTopK {
    public static void main(String[] args) throws IOException, NotFoundException {
        HashMap<String, String> testcases = new LinkedHashMap<>();

        boolean doPrint = true;
        boolean doCheck = false;

        //Task 1
      // testcases.put("sample", "3,http://dbpedia.org/resource/a,http://dbpedia.org/resource/b,no");
     //  testcases.put("test", "7,http://node-F,http://node-E,no");
        testcases.put("test", "10,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,no");
       
        //testcases.put("test", "1,http://purl.obolibrary.org/obo/HP_0004942,http://www.human-phenotype-ontology.org/hpoweb/showterm?id=HP:0001626,no");
        // path from drugbank and pharmgkb
       //Q1  testcases.put("drugbank1","2,http://bio2rdf.org/drugbank:DB00072,http://bio2rdf.org/clinicaltrials:NCT01959490,no");
        
        //path from drugbank and hgnc
      //Q2   testcases.put("drugbank2","2,http://bio2rdf.org/drugbank:DB01268,http://bio2rdf.org/hgnc.symbol:FLT3,no");
         
        // path from drugbank and pharmgkb
       //Q3  testcases.put("drugbank3","2,http://bio2rdf.org/drugbank:DB00134,http://bio2rdf.org/kegg:D04983,no");
         //testcases.put("drugbank3","2,http://bio2rdf.org/drugbank:DB00134,http://bio2rdf.org/ligandbox:D04983,no");
         
        /*this works and bring back the results within the drugbank*/
        //testcases.put("drugbank3","2,http://bio2rdf.org/drugbank:DB00515,http://bio2rdf.org/drugbank_resource:DB00515_DB00695,no");
        
        /*this works and bring back the results within the drugbank*/
       // testcases.put("drugbank3","2,http://bio2rdf.org/drugbank:DB08865,http://bio2rdf.org/kegg:D09731,no");
       
        /*this works and bring back the results both from drugbank & hgnc*/
      // testcases.put("drugbank2","2,http://bio2rdf.org/drugbank:BE0002362,http://bio2rdf.org/hgnc.symbol:CYP3A5,no");
        
        /* CYP2C9 and rs1057910 this works and bring back the results within drugbank*/
       // testcases.put("drugbank3","2,http://bio2rdf.org/drugbank:DB00222,http://bio2rdf.org/pharmgkb:PA449761,no");
        /* finds path locally within drugbank as well as both from kegg and pharmgkb*/
      //Q5  testcases.put("drugbank3","2,http://bio2rdf.org/drugbank:DB00222,http://bio2rdf.org/kegg:C07669,no");
       
        /*works within in the omim datset and bring back the 2 paths*/
       // testcases.put("drugbank3","2,http://bio2rdf.org/omim:147470,http://bio2rdf.org/hgnc.symbol:GRDF,no");
        
        /*works wihtin omim and bring back the 3 paths*/
      //Q6.testcases.put("drugbank3","2,http://bio2rdf.org/omim:147470,http://bio2rdf.org/hgnc.symbol:IGF2,no");
        
       // testcases.put("drugbank3","2,http://bio2rdf.org/kegg:hsa_1435,http://bio2rdf.org/hgnc:2432,no");
        
        // Model model = RDFDataMgr.loadModel("data/sample.nt") ;       
  
    /*    Location loc= new Location("data/sample");
        Dataset dataset= TDBFactory.createDataset(loc);
        Model model= dataset.getDefaultModel();
        InputStream in= FileManager.get().open("data/sample.nt");
        model.read(in,null,"N-TRIPLE");
        System.out.println("model read done!");*/
      /*  StmtIterator stm = model.listStatements();
         while (stm.hasNext()){
        	 System.err.println(stm.nextStatement());
         }*/

       Model model= ModelFactory.createDefaultModel();
       InputStream in= FileManager.get().open("data/A.nt");
       //TDB.sync(dataset);
       model.read(in,null,"N-TRIPLE");
       
        for (Map.Entry<String, String> entry : testcases.entrySet()) {
           
        	String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Testcase: " + key + " Conditions: " + value);
            String[] cond = value.split(",");
            int k = Integer.parseInt(cond[0]);
            String root = cond[1];
            String target = cond[2];
            String edge = cond[3];
            if (edge.equalsIgnoreCase("no")) {
                edge = null;
            }
            
           BaselineTopKPath topK= new BaselineTopKPath(model);
        
           	topK.init(model);

            List<Path> results;
            try {
            	topK.run( ResourceFactory.createResource(root).asNode(), ResourceFactory.createResource(target).asNode(), ResourceFactory.createResource(edge).asNode(), k,null);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }


           if( doPrint ) {
             
               // Util.printPaths(results, model, key);
             
            }

        }
    }

  }
