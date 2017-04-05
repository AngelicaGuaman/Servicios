package sos.entrega2014.mr1.junio;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;


public class Facturacion 
{
	public static void main(String[] args) throws IOException
	{
		//un job para ejecutar tu mr
		JobConf conf = new JobConf(Facturacion.class);
		conf.setJobName("Facturation");

		//saldias del maper
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(IntWritable.class);

		//salida del reducer
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		//ahora se indica quien es quien: mapper, combiner(opcional) y reducer
		conf.setMapperClass(FacturacionMapper.class);
		conf.setReducerClass(FacturacionReducer.class);

		//configuracion de hdfs, siempre las mismas
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		//estos se pasan como argumentos al main, siempre igual.
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		//y arrancas al job! 
		JobClient.runJob(conf);
	}//fin del main.

}//fin de la clase main.



class FacturacionMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable>
{
	private Text numeroLlamante; //la key
	private IntWritable duracionLlamada;//el value

	//el mapper es quien recibe los csvs y los procesa, emitiendo una salida<key, value>
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
	throws IOException 
	{
		numeroLlamante= new Text();
		duracionLlamada=new IntWritable();

		//es para leer el csv
		String line = value.toString();

		StringTokenizer tokenizer = new StringTokenizer(line, ",");

		//1,645093009,20130503124520,12,47857,56721,51488,34456

		numeroLlamante.set(tokenizer.nextToken().trim());
		//ahora tienes que ver, como coges la duracion: mira tu csv!

		tokenizer.nextToken().trim();tokenizer.nextToken().trim();
		duracionLlamada.set(Integer.parseInt(tokenizer.nextToken().trim()));

		output.collect(numeroLlamante, duracionLlamada);
	}//fin map

}//fin de la clase del mapper


class FacturacionReducer extends MapReduceBase
implements Reducer<Text, IntWritable, Text, IntWritable>
//del mapper-key y value- y las ultimdas del reducer: key y value 'finales'
{
	@Override
	public void reduce(Text key, Iterator<IntWritable> values,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
	throws IOException
	{
		// TODO Auto-generated method stub
		int sum=0;
		while(values.hasNext())
		{
			sum += (values.next().get()+10);
		}

		output.collect(key, new IntWritable(sum));
	}

}//fin del reducer.