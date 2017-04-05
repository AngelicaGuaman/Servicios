package sos.entrega2014.mr3.junio;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.Path;
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


public class TopTres
{
	public static void main(String[] args) throws IOException
	{
		//un job para ejecutar tu mr
		JobConf conf = new JobConf(TopTres.class);
		conf.setJobName("TopTres");

		//saldias del mapeer
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(LLamada.class);

		//salida del reducer
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		//ahora se indica quien es quien: mapper, combiner(opcional) y reducer
		conf.setMapperClass(TopTresMapper.class);
		conf.setReducerClass(TopTresReducer.class);

		//configuracion de hdfs, siempre las mismas
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		//estos se pasan como argumentos al main, siempre igual.
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		//y arrancas al job! 
		JobClient.runJob(conf);
	}
}

//clase auxiliar para las llamadas
class LLamada implements Serializable, Writable
{
	//atributos de la clase
	private int numero_receptor;
	private int minutos;

	//constructor por defecto
	public LLamada(){}

	public LLamada(int numero_receptor, int minutos)
	{
		this.numero_receptor=numero_receptor;
		this.minutos=minutos;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		numero_receptor=in.readInt();
		minutos=in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(numero_receptor);
		out.writeInt(minutos);
	}

	//get y set de las variables de la clase
	public int getNumero_receptor() {
		return numero_receptor;
	}

	public void setNumero_receptor(int numero_receptor) {
		this.numero_receptor = numero_receptor;
	}

	public int getMinutos() {
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}


}

class TopTresMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, LLamada>
{
	//atributos de la clase
	private Text llamante;
	private Text receptor;
	private int minutos;

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, LLamada> output, Reporter reporter)
	throws IOException 
	{
		// TODO Auto-generated method stub
		llamante = new Text();
		receptor = new Text();

		String linea = value.toString();
		StringTokenizer stringTokenizer = new StringTokenizer(linea, ",");

		llamante.set(stringTokenizer.nextToken().trim());//recogemos el numero de llamante
		receptor.set(stringTokenizer.nextToken().trim());//numero del receptor
		stringTokenizer.nextToken().trim();//no utilizamos este elemento por eso no lo guardo
		minutos = Integer.parseInt(stringTokenizer.nextToken().trim());//minutos hablados

		int numero1 = Integer.parseInt(llamante.toString());//llamante
		int numero2 = Integer.parseInt(receptor.toString());//receptor

		output.collect(llamante, new LLamada(numero2,minutos));//datos para reducer
		output.collect(receptor, new LLamada(numero1,minutos));
	}
}

class TopTresReducer extends MapReduceBase implements Reducer<Text, LLamada, Text, Text>
{
	//atributos de la clase
	private HashMap<Integer, Integer> llamadas;
	private LLamada llamada;
	private Comparator compara;

	@Override
	public void reduce(Text key, Iterator<LLamada> values,
			OutputCollector<Text, Text> output, Reporter reporter) 
	throws IOException 
	{
		// TODO Auto-generated method stub
		llamadas = new HashMap<Integer, Integer>();
		compara = new Comparador();
		//variable para guardar las llamadas según los minutos hablados
		PriorityQueue<Entry<Integer, Integer>> queue = new PriorityQueue<Entry<Integer, Integer>>(50, compara);

		//CONSTRUCTOR DE LLAMADAS RARO
		while(values.hasNext())
		{
			llamada=values.next();
			int minutos=0;
			//vamos acumulando los minutos de las mismas llamadas
			if(llamadas.containsKey(llamada.getNumero_receptor()))
				minutos=llamadas.get(llamada.getNumero_receptor());

			llamadas.put(llamada.getNumero_receptor(), minutos+llamada.getMinutos());
		}

		//ordenamos las llamadas según los minutos hablados
		for(Entry<Integer, Integer> aux : llamadas.entrySet())
			queue.add(aux);

		//mostramos las 3 primeras llamadas con más minutos 
		for(int i=0; !queue.isEmpty() && i< 3 ; i++)
		{
			Entry<Integer,Integer> elemento = queue.poll();	
			String linea = elemento.getKey()+", "+elemento.getValue();

			output.collect(key, new Text(linea));
		}
	}
}

/*http://stackoverflow.com/questions/683041/java-how-do-i-use-a-priorityqueue*/
class Comparador implements Comparator<Entry<Integer,Integer>>
{
	//metodo para organizar las llamadas según sus minutos
	@Override
	public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) 
	{
		int resultado=0;
		// TODO Auto-generated method stub
		if (o1.getValue() < o2.getValue())
			resultado= 1;
		if (o1.getValue() > o2.getValue())
			resultado= -1;

		return resultado;
	}
}