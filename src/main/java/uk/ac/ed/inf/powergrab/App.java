package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

/**
 * Hello world!
 *
 */
public class App 
{
	
    public static void main( String[] args ) throws IOException
    {
       String dd = args[0];
       String mm = args[1];
       String yy = args[2];
       double latitude = Double.parseDouble(args[3]);
       double longitude = Double.parseDouble(args[4]);
       int seed = Integer.parseInt(args[5]);
       String droneType = args[6];
       String mapString = "http://homepages.inf.ed.ac.uk/stg/powergrab/" + yy + "/"
       							+ mm + "/" + dd + "/powergrabmap.geojson";
       
      // http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson
       
       URL mapUrl = new URL(mapString);
       HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
       conn.setReadTimeout(10000);
       conn.setConnectTimeout(15000);
       conn.setRequestMethod("GET");
       conn.setDoInput(true);
       conn.connect();
       
       Scanner scanner = new Scanner(conn.getInputStream());
       String mapSource = "";
       while (scanner.hasNext()) {
    	   mapSource += scanner.nextLine() + '\n';
       }
       
     // System.out.println(mapSource);
       
      FeatureCollection fc = FeatureCollection.fromJson(mapSource);
      List<Feature> features = fc.features();
      List<Station> stations = new ArrayList<>();
      for (Feature f : features) {
    	  double coins = Double.parseDouble(f.getStringProperty("coins"));
    	  double power = Double.parseDouble(f.getStringProperty("power"));
    	  Point p = (Point) f.geometry();
    	  stations.add(new Station(coins, power, p));
      }
      
//      for (Station s : stations) {
//    	  System.out.println(s.toString());
//      }
      
      if (droneType.equals("stateless")) {stateless(latitude, longitude, seed, stations, droneType, dd, mm , yy);}
      else if (droneType.equals("stateful")) {stateful(latitude, longitude, stations, droneType, dd, mm , yy);}
      else System.out.println("choose stateless or stateful drone type");
    }
    
    public static void stateless(double latitude, double longitude, int seed, List<Station> stations, String droneType, String dd, String mm, String yy) throws IOException {
    	StatelessDrone drone = new StatelessDrone(latitude, longitude, seed);
    	String fileName = droneType + "-" + dd + "-" + mm + "-" + 2019 + ".txt";
    	FileWriter file = new FileWriter(fileName);
    	while (drone.getPower() > 0.0 && drone.getMoves() < 250) {
    		Direction nextDirection = drone.calNextDirection(stations);
    		Position newPos = drone.position.nextPosition(nextDirection);
    		Position oldPos = drone.position;
    		drone.makeMove(newPos);
    		System.out.println("[ " + oldPos.longitude + "," + oldPos.latitude + "],");
//    		System.out.println("[ " + oldPos.latitude + "," + oldPos.longitude + "," + nextDirection + "," + drone.position.latitude + "," + drone.position.longitude + "," + drone.coins + "," + drone.power + " ],");
    		file.write(oldPos.latitude + "," + oldPos.longitude + "," + nextDirection + "," + drone.position.latitude + "," + drone.position.longitude + "," + drone.coins + "," + drone.power + "\n");
    	} 	
    	file.close();
    }
    
    public static void stateful(double latitude, double longitude, List<Station> stations, String droneType, String dd, String mm, String yy) throws IOException {
    	StatefulDrone drone = new StatefulDrone(latitude, longitude);
    	String fileName = droneType + "-" + dd + "-" + mm + "-" + 2019 + ".txt";
    	FileWriter file = new FileWriter(fileName);
    	for (String s : drone.write2file(stations)) {
    		System.out.println(s);
    		file.write(s + "\n");
    	}
    	file.close();
    }
    
}
