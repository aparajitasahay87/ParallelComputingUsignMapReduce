import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import java.util.Map.*;
import java.util.HashMap;


public class Inversedindexing {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> 
	{
		JobConf conf;
		public void configure( JobConf job ) 
		{
			this.conf = job;
		}
        private final static IntWritable one = new IntWritable(1);
        String filename_count;
        String keyWord;
        private Text keyword = new Text();
        private Text word = new Text();
					
        public void map(LongWritable docId, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
		{
        	// retrieve # keywords from JobConf
			int argc = Integer.parseInt( conf.get( "argc" ) );
			
			// get the current file name
			FileSplit fileSplit = ( FileSplit )reporter.getInputSplit();
			String filename = "" + fileSplit.getPath( ).getName( );
			
			String line = value.toString();
			//Getting keyword 
			for(int i=0;i<argc;i++)
			{
				int count=0;
				StringTokenizer tokenizer = new StringTokenizer(line);
				keyWord = conf.get("keyword" + i);
	           
				while (tokenizer.hasMoreTokens()) 
				{
					String token = tokenizer.nextToken();
					if(token.equals(keyWord))
					{
						count++;
					}
				}
				if (count > 0)
				{
					filename_count = filename+"_"+count;
					word.set(filename_count);
					keyword.set(keyWord);
					output.collect(keyword,word);
				}
			}
		}
	}
    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
	{
		//private Text docListText = new Text();
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException 
		{
			int sum = 0;
			StringBuilder sb = new StringBuilder();
			String docList;
			String seperator = " " ;
			HashMap<String,Integer> document_container = new HashMap<String,Integer>();
			
            while (values.hasNext()) 
			{
            	Text value = values.next();
				String[] fileCount = value.toString().split("_");
				String fileName = fileCount[0];
				int count = Integer.valueOf(fileCount[1]);
				//Read each filename_count from the value
				
				if(document_container.containsKey(fileName))
				{
					int currentCount = document_container.get(fileName);
					sum = currentCount+count;
					document_container.put(fileName,sum);
				}
				else
				{
					document_container.put(fileName,count);
				}
				
			}
            //Parse hash map 
            Set<Entry<String, Integer>> entrySet = document_container.entrySet();
            Iterator<Entry<String, Integer>> entrySetIterator = entrySet.iterator();
            while (entrySetIterator.hasNext()) 
            {
               Entry<String, Integer> entry = entrySetIterator.next();
               String filename = entry.getKey();
               Integer finalCount = entry.getValue();
               String Finalname_FinalCount = filename+"_"+finalCount;
               sb.append(seperator).append(Finalname_FinalCount);
            }

			//Convert stringbuilder to string
            docList = sb.toString();
            
			//store it in reduce output collect
			output.collect(key,new Text(docList));
        }
    }

    public static void main(String[] args) throws Exception 
	{
		//timer start
    	long start = System.currentTimeMillis();
        JobConf conf = new JobConf(Inversedindexing.class);
        conf.setJobName("InversedindexingJob");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		conf.set( "argc", String.valueOf( args.length - 2 ) ); 
		for ( int i = 0; i < args.length - 2; i++ )
		conf.set( "keyword" + i, args[i + 2] ); 

        JobClient.runJob(conf);
        //timer stop
        long stop = System.currentTimeMillis();
        long elapsedTime = stop - start;
        System.out.println("Total time\t" + elapsedTime);
        
    }
}
