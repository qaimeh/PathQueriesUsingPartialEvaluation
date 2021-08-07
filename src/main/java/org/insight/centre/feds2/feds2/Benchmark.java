package org.insight.centre.feds2.feds2;
/*import com.opencsv.CSVWriter;
import org.apache.commons.cli.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.DoubleArray;

import java.io.*;
import java.util.*;


*//**
 * Created by jumbrich on 02/06/16.
 *//*
public class Benchmark {


    private static Map<String,TopKAlgorithmFactory> algoFactories= new HashMap<String,TopKAlgorithmFactory>();
    static{
        algoFactories.put("bfs", new BaselineTopKPathFactory());
        algoFactories.put("bibfs", new BaselineBidirectionalTopKFactory());
    }


    private static List<File> getTaskFiles(CommandLine cmd){
        List<File> tasks= new ArrayList<File>();
        if(cmd.hasOption("task"))
            tasks.add(new File(cmd.getOptionValue("task")));
        if(cmd.hasOption("tasks")){
            File dir= new File(cmd.getOptionValue("tasks"));
            for(File f: dir.listFiles()){
                if(f.getName().endsWith(".props"))
                    tasks.add(f);
            }
        }
        return tasks;
    }

    *//**
     *
     * @param args
     *//*
     public static void main(String[] args) throws IOException {
         CommandLine cmd = parseCMDs(args);

         //parse task file(s) from cmd
         List<File> taskfiles=getTaskFiles(cmd);

         //parse the algo backend config
         Properties config = null;
         File configFile=null;
         if(cmd.hasOption("config")){
             configFile=new File(cmd.getOptionValue("config"));
             config=new Properties();
             config.load(new FileReader(configFile));
         }

         //OUtputfile
         File output = new File(cmd.getOptionValue("out"));
         System.out.println("Writing results to: "+output);
         String [] stats={"task", "algo","config", "correctRuns","count","min(ms)","mean(ms)","max(ms)","stddev(ms)","25%(ms)","50%(ms)","75%(ms)"
                 ,"min(bytes)","mean(bytes)","max(bytes)","stddev(bytes)","25%(bytes)","50%(bytes)","75%(bytes)"};
         PrintWriter resWriter= new PrintWriter(new FileWriter(output), true);
         CSVWriter writer = new CSVWriter(resWriter, ',');
         writer.writeNext(stats);
         writer.flush();

         String algoId=cmd.getOptionValue("algo");
         System.out.println("Loading config for Algorithm: "+algoId);
         TopKAlgorithmFactory f= algoFactories.get(algoId);
         TopKAlgorithm algo = f.newInstance(config);


         //this can be used to reinit the backend
         //algo.init(config);

         for(File taskFile: taskfiles){
             System.out.println("Starting benchmark for "+taskFile);
             try {
                 //load specific tasks
                 Properties task = new Properties();
                 task.load(new FileReader(taskFile));

                 //Parse solution file
                 File expected = new File(taskFile.getParentFile(), task.getProperty("expected"));
                 Scanner s = new Scanner(expected);
                 List<List<String>> expectedSolutions = new ArrayList();
                 while (s.hasNext()) {
                     String line = s.nextLine().replace("[", "").replace("]", "").replace("\"", "").replace(" ", "");
                     String[] t = line.split(",");
                     expectedSolutions.add(new ArrayList(Arrays.asList(t)));
                 }

                 int runs=Integer.valueOf(cmd.getOptionValue("runs","1"));
                 DescriptiveStatistics time = new DescriptiveStatistics();
                 DescriptiveStatistics memory = new DescriptiveStatistics();
                 int cur=1;

                 int correctRuns=0;
                 while(cur<=runs) {
                     //Memory and time measure init
                     MemoryThread mt = new MemoryThread();mt.start();
                     long start = System.currentTimeMillis();
                     algo.reset();
                     List<Path> solutions = algo.run( task.getProperty("startnode")
                                                          , task.getProperty("endnode")
                                                            , null
                                                          , Integer.parseInt(task.getProperty("topk")));
                     long end = System.currentTimeMillis();
                     mt.shutdown();
                     long timeelapsed = end - start;

                     time.addValue(timeelapsed);
                     memory.addValue(mt.maxMemoryUsage);

                     //verify solutions
                     List<List<String>> returnedSolutions = new ArrayList();
                     for (String[] path : solutions)
                         returnedSolutions.add(new ArrayList(Arrays.asList(path)));

                    if(cmd.hasOption("results")){
                        File rdir=new File(cmd.getOptionValue("results"));
                        File r = new File(rdir, taskFile.getName()+"-"+configFile.getName()+"-"+algoId+"-"+cur+".results" );
                        PrintWriter pw = new PrintWriter(r);
                        for (String[] path : solutions)
                            pw.println(Arrays.toString(path));
                        pw.close();


                    }
                     for(List a: expectedSolutions)
                        System.out.println(a);
                        System.out.println("----");
                    for(List a: returnedSolutions)
                        System.out.println(a);

                     expectedSolutions.removeAll(returnedSolutions);
                     boolean correct = expectedSolutions.size() == 0;
                     if(correct) correctRuns++;
                     System.out.println("["+taskFile.getName()+"]>Run: "+cur+"/"+runs);
                     System.out.println("   Memory increased (in bytes): " + mt.maxMemoryUsage + " " + humanReadableByteCount(mt.maxMemoryUsage, true));
                     System.out.println("   Time elapsed (in ms): " + timeelapsed);
                     System.out.println("   Received results: " + solutions.size());
                     //System.out.println("   Correct results: " + correct);
                     cur++;
                 }

                 String [] stats1={"count","min","mean","max","stddev","25%","50%","75%"};
                 Double[] timeStats={
                         Double.valueOf(time.getN()), time.getMin(), time.getMean(), time.getMax(),time.getStandardDeviation(),
                         time.getPercentile(25),time.getPercentile(50),time.getPercentile(75)
                 };
                 Double[] memStats={
                         Double.valueOf(memory.getN()), memory.getMin(), memory.getMean(), memory.getMax(),memory.getStandardDeviation(),
                         memory.getPercentile(25),memory.getPercentile(50),memory.getPercentile(75)
                 };

                 ArrayList<String> allStats=new ArrayList();
                 allStats.add(taskFile.getName());
                 allStats.add(algoId);
                 allStats.add(configFile.getName());
                 allStats.add(""+correctRuns);
                 for(Double a: timeStats) allStats.add(a.toString());
                 for(int i=1; i<memStats.length;i++) allStats.add(memStats[i].toString());

                 System.out.println("["+taskFile.getName()+"]>Average over : "+runs+" runs");
                 System.out.println ("              Stats: "+Arrays.toString(stats1));
                 System.out.println ("        Time(in ms): "+Arrays.toString(timeStats));
                 System.out.println ("   Memory(in bytes): "+Arrays.toString(memStats));

                 writer.writeNext(allStats.toArray(new String[allStats.size()]));
                 writer.flush();

             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         writer.close();

     }

    public static CommandLine parseCMDs(String [] args){
         Options options = new Options();
         options.addOption("h", "help", false, "show help.");
         HelpFormatter formatter = new HelpFormatter();


         options.addOption(
                 OptionBuilder.withArgName( "task" )
                 .hasArg()
                 .withDescription(  "task definition" )
                 .create( "task" )
            );

        options.addOption(
                OptionBuilder.withArgName( "tasks" )
                        .hasArg()
                        .withDescription(  "tasks directory" )
                        .create( "tasks" )
        );

        options.addOption(
                OptionBuilder.withArgName( "config" )
                        .hasArg()
                        .withDescription(  "additional config, if needed (e.g., HDT file)" )
                        .create( "config" )
        );
         options.addOption(
                 OptionBuilder.withArgName( "out" )
                         .hasArg()
                         .withDescription(  "where to store benchmark statistics" )
                         .create( "out" )
         );
        options.addOption(
                OptionBuilder.withArgName( "results" )
                        .hasArg()
                        .withDescription(  "where to store benchmark results" )
                        .create( "results" )
        );
         options.addOption(
                 OptionBuilder.withArgName( "algo" )
                         .hasArg()
                         .withDescription(  "which algorithm to use" )
                         .create( "algo" )
         );

        options.addOption(
                OptionBuilder.withArgName( "runs" )
                        .hasArg()
                        .withDescription(  "How many runs?" )
                        .create( "runs" )
        );
         // create the parser
         CommandLineParser parser = new DefaultParser();
         try {
             // parse the command line arguments
             CommandLine cmd = parser.parse( options, args );
             if (cmd.hasOption("h")) {
                 formatter.printHelp("benchmark", options);
                 System.exit(0);
             }
             return cmd;
         }
         catch( ParseException exp ) {
             // oops, something went wrong
             System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
             formatter.printHelp( "benchmark", options );
             System.exit(0);
         }
        return null;
     }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}

abstract class TopKAlgorithmFactory{
     abstract public TopKAlgorithm newInstance(Properties config );
}
class BaselineTopKPathFactory extends TopKAlgorithmFactory{

    public TopKAlgorithm newInstance(Properties config ){
        try {
            TopKAlgorithm algo= new BaselineTopKPath();
            algo.init(config);
            return algo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}

class BaselineBidirectionalTopKFactory extends TopKAlgorithmFactory{

    public TopKAlgorithm newInstance(Properties config ){
        try {
            TopKAlgorithm algo= new BaselineBidirectionalTopK();
            algo.init(config);
            return algo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}

class MemoryThread extends Thread{
    private final long usedMemoryBefore;

    public long maxMemoryUsage=0;
    boolean run=true;
    Runtime runtime = Runtime.getRuntime();
    public MemoryThread(){
        runtime = Runtime.getRuntime();
        System.gc();System.gc();System.gc();System.gc();
        System.gc();System.gc();System.gc();System.gc();
        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
    }

    public void shutdown(){
        run=false;
        System.gc();System.gc();System.gc();System.gc();System.gc();
    }
    public void run(){
        while(run){
            try {
                long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memory=(usedMemoryAfter-usedMemoryBefore);
                if(maxMemoryUsage < memory)
                    maxMemoryUsage=memory;
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}*/