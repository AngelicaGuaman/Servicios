package sos.entrega2014.mr2.junio;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
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

public class FraudDetection
{
	public static void main(String[] args) throws IOException
	{
		//un job para ejecutar tu mr
		JobConf conf = new JobConf(FraudDetection.class);
		conf.setJobName("FraudDetection");

		//saldias del maper
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(Llamada.class);

		//salida del reducer
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		//ahora se indica quien es quien: mapper, combiner(opcional) y reducer
		conf.setMapperClass(FraudDetectionMapper.class);
		conf.setReducerClass(FraudDetectionReducer.class);

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

//clases auxiliares
class Llamada implements Serializable, Writable
{
	//guardamos la fecha y la coordenada de donde se realiza la llamada
	private String fechalarga;
	private Coordenada coordenada;

	//constructor por defecto
	public Llamada()
	{	
	}

	//constructor
	public Llamada(String fechaLarga, Coordenada coorXY)
	{
		this.fechalarga=fechaLarga;
		coordenada=coorXY;	
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		// TODO Auto-generated method stub
		//metodo para leer y rellenar nuestros valores
		fechalarga = input.readUTF();
		coordenada=new Coordenada(input.readInt(), input.readInt());

	}

	@Override
	public void write(DataOutput output) throws IOException 
	{
		// TODO Auto-generated method stub
		output.writeUTF(fechalarga);
		output.writeInt(coordenada.getX());
		output.writeInt(coordenada.getY());
	}

	//getter and setters de los atributos 
	public String getFechalarga() {
		return fechalarga;
	}

	public void setFechalarga(String fechalarga) {
		this.fechalarga = fechalarga;
	}

	public Coordenada getCoordenada() {
		return coordenada;
	}

	public void setCoordenada(Coordenada coordenada) {
		this.coordenada = coordenada;
	}
}

//clase auxiliar, con la coordenada de donde se realiza la llamada
class Coordenada
{
	private int x, y;

	//constructor por defecto
	public Coordenada(){}

	public Coordenada(int x, int y)
	{
		this.x=x;
		this.y=y;
	}

	//getter y setter de los atributos de la clase
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
//siempre  csv: long y text; salida del maper: key value 
class FraudDetectionMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Llamada>
{
	//atributos para recoger los datos
	private String fecha;
	private Coordenada posicionLLamante, posicionReceptor;

	private Text miLLamante;
	private Text miReceptor;

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Llamada> output, Reporter reporter)
	throws IOException 
	{
		/*
		 * nrollamante, nrollamado, fecha-hora inicio llamada, coordenadas antena llamante y receptor.
o Entrada:
􏰁 1, 3, 20130401150>k3, 100, 150, 120, 140*/
		miLLamante = new Text();
		miReceptor = new Text();

		String line = value.toString();
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

		//leemos los campos del CDR
		miLLamante.set(stringTokenizer.nextToken().trim());//llamante
		miReceptor.set(stringTokenizer.nextToken().trim());//receptor
		fecha = stringTokenizer.nextToken().trim();//cogemos la fecha
		stringTokenizer.nextToken().trim();//no necesito este campo por eso no lo guardo

		posicionLLamante= new Coordenada(Integer.parseInt(stringTokenizer.nextToken().trim()),Integer.parseInt(stringTokenizer.nextToken().trim()));
		posicionReceptor=  new Coordenada(Integer.parseInt(stringTokenizer.nextToken().trim()), Integer.parseInt(stringTokenizer.nextToken().trim()));

		//elementos de salida para el reduce
		output.collect(miLLamante, new Llamada(fecha, posicionLLamante));
		output.collect(miReceptor, new Llamada(fecha, posicionReceptor));
	}
}

class FraudDetectionReducer extends MapReduceBase implements Reducer <Text,Llamada,Text,Text>
{
	//atributos de la clase
	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmss");
	private HashMap<String, Coordenada> llamadas;
	private Llamada llamada;

	@Override
	public void reduce(Text key, Iterator<Llamada> values,
			OutputCollector<Text, Text> output, Reporter reporter) 
	throws IOException 
	{
		// TODO Auto-generated method stub
		llamadas = new HashMap<String, Coordenada>();

		//guardamos las llamadas 
		while(values.hasNext())
		{
			llamada=values.next();
			llamadas.put(llamada.getFechalarga(), llamada.getCoordenada());
		}

		boolean fraude=false;

		//vamos recorriendo entrada a entrada para calcular el tiempo y distancia
		for(Entry<String, Coordenada> aux : llamadas.entrySet())
		{
			String miFecha=aux.getKey();
			Coordenada miCoordenada = aux.getValue();

			for(Entry<String, Coordenada> sig : llamadas.entrySet())
			{
				String miFechaSig=sig.getKey();
				Coordenada miCoordenadaSig = sig.getValue();
				double esFraude=0.0;

				//mientras no sea fraude calculamos la distancia
				if(!fraude)
				{
					double horas, distancia1;

					horas = tiempo( miFechaSig,miFecha);
					distancia1 = distancia(miCoordenada, miCoordenadaSig);
					esFraude=distancia1/horas;

					//solo cuando es mayor que 1000 es cuando imprimimos los datos
					if(esFraude > 1000.0)
					{
						fraude=true;
						output.collect(key, new Text("alarma-fraude"));
					}
				}
			}
		}
	}

	//método para calcular el tiempo entre fechas
	private double tiempo(String next, String current) 
	{
		double time;
		Date currentDate = null, nextDate = null;

		try
		{		
			currentDate = formatDate.parse(current);
			nextDate = formatDate.parse(next);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		time = Math.abs(((nextDate.getTime() - currentDate.getTime())/3600000));

		return time;
	}

	//método para calcular la distancia entre dos coordenadas
	private double distancia(Coordenada origen, Coordenada destino) 
	{
		double distancia;

		distancia = Math.sqrt((origen.getX() - destino.getX()) * (origen.getX()-destino.getX()) + (origen.getY()-destino.getY()) * (origen.getY()- destino.getY()));

		return distancia;
	}

}