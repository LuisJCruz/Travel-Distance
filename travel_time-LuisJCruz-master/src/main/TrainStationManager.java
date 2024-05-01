package main;

/**
 * This is the TrainStationManeger class. 
 * @author Luis J. Cruz
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import data_structures.ArrayList;
import data_structures.HashSet;
import data_structures.HashTableSC;
import data_structures.LinkedListStack;
import data_structures.SimpleHashFunction;
import data_structures.SinglyLinkedList;
import interfaces.List;
import interfaces.Map;
import interfaces.Set;
import interfaces.Stack;

public class TrainStationManager {
	
	private Stack<Station> toVisit = new LinkedListStack<Station>();
	private Map<String, List<Station>> stations = new HashTableSC<String, List<Station>>(1,new SimpleHashFunction<>());
	private Map<String, Station> shortestRoute = new HashTableSC<String, Station>(1,new SimpleHashFunction<>());
	private Set<Station> visited = new HashSet<Station>();
	private Map<String, Double> travelTimes = new HashTableSC<String, Double>(1, new SimpleHashFunction<>());
	
	/**
	 * Constructor that receives the name of the stations and applies 
	 * the election logic. Note: The files should be found in the input folder. 
	 * The file should have the following format: src_city,dest_city,distance
	 * @param station_file String of the name of the file in which the city names and distance are written in.
	**/
	public TrainStationManager(String station_file) {
		String stationsFile = "inputFiles/"+station_file;
        try (BufferedReader reader = new BufferedReader(new FileReader(stationsFile))) {
            String line=reader.readLine();
            while ((line = reader.readLine()) != null) {
            	if (!stations.containsKey(line.split(",")[0])) {
					stations.put(line.split(",")[0], new ArrayList<Station>());
				}
            	if (!stations.containsKey(line.split(",")[1])) {
					stations.put(line.split(",")[1], new ArrayList<Station>());
				}
				stations.get(line.split(",")[0]).add(new Station(line.split(",")[1],Integer.parseInt(line.split(",")[2])));
				stations.get(line.split(",")[1]).add(new Station(line.split(",")[0],Integer.parseInt(line.split(",")[2])));
				
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		findShortestDistance();
	}
	
	/**
	 * Void function that calculates the shortest route from “Westside” to every other station following 
	 * Dijkstra’s algorithm. It populates the shortest route map. 
	**/
	private void findShortestDistance() {
		for(String station : stations.getKeys()) {
			shortestRoute.put(station,new Station("Westside", Integer.MAX_VALUE));
		}
		shortestRoute.get("Westside").setDistance(0);
		toVisit.push(shortestRoute.get("Westside"));
		while(!toVisit.isEmpty()) {
			Station currentStation = toVisit.pop();
			visited.add(currentStation);
			for(Station neighbor : stations.get(currentStation.getCityName())) {
				int curshortDist = shortestRoute.get(neighbor.getCityName()).getDistance();
				int statshortDist = currentStation.getDistance();
				int neighstatDist = neighbor.getDistance();
				if (curshortDist>statshortDist+neighstatDist) {
					shortestRoute.get(neighbor.getCityName()).setDistance(statshortDist+neighstatDist);
					if(!visited.isMember(neighbor)) {
						sortStack(new Station(neighbor.getCityName(),statshortDist+neighstatDist),toVisit);
					}
					shortestRoute.get(neighbor.getCityName()).setCityName(currentStation.getCityName());
				}	
			}
			
		}
	}
	
	/**
	 *Receives a Stack that needs to remain sorted and the station we want to add. 
	 *The stack is be sorted by distance. The smaller that distance the closer 
	 *to the top the value is, the top of the stack is the station with the shortest distance.
	 *@param station Station thats being added to the stack
	 *@param stackToSort Name of the stack that is being sorted and the station is being added to.
	**/
	public void sortStack(Station station, Stack<Station> stackToSort) {
		Stack<Station> tempStack = new LinkedListStack<Station>();
		stackToSort.push(station);
		while(!stackToSort.isEmpty()) {
            Station tempStat = stackToSort.pop();
            while(!tempStack.isEmpty() && tempStack.top().getDistance() > tempStat.getDistance()) {
            	stackToSort.push(tempStack.pop());
            }
            tempStack.push(tempStat);
        }
        while(!tempStack.isEmpty()) {
        	stackToSort.push(tempStack.pop());
        }
	}
	
	/**
	 * @return
	 A Map where the key is the station name, and the value is the time it takes to reach that station. 
	 The time will be calculated in minutes. The calculation will work as follows: 
	 It takes 2.5 minutes per kilometer and it takes 15 minutes for each station between Westside and the destination.
	**/
	public Map<String, Double> getTravelTimes() {
		for (String station : shortestRoute.getKeys()){
			travelTimes.put(station, (2.5*shortestRoute.get(station).getDistance()));
			String next = shortestRoute.get(station).getCityName();
			while(next!="Westside") {
				Double val = travelTimes.get(station)+15;
				travelTimes.put(station, val);
				next=shortestRoute.get(next).getCityName();
			}	
		}
		return travelTimes;
	}

	/**
	 * @return Map that will represent how all the stations are connected. 
	 * The key will be the name of the city where the station is located, 
	 * and the value is a List of all the stations that neighbor it. 
	 * The distance in this case represents the distance between that key and its neighbor.
	**/
	public Map<String, List<Station>> getStations() {
		return stations;
		
	}

	/**
	 *Sets Map that will represent how all the stations are connected. 
	 *The key will be the name of the city where the station is located, 
	 * and the value is a List of all the stations that neighbor it. 
	 * The distance in this case represents the distance between that key and its neighbor.
	 * @param cities Map that will represent how all the stations are connected. 
	**/
	public void setStations(Map<String, List<Station>> cities) {
		this.stations=cities;
	}

	/**
	 * @return A Map with the shortest route for each station from a given starting position. 
	 * The key is the city the station is in, the value is the station that gives us the shortest distance. 
	 * Notice that in this case the Station we store as a value will hold the name of the station that 
	 * gave us the shortest route and the distance is the current shortest distance for the key.
	**/
	public Map<String, Station> getShortestRoutes() {
		return shortestRoute;
		
	}

	/**
	 * Sets Map with the shortest route for each station from a given starting position. 
	 * The key is the city the station is in, the value is the station that gives us the shortest distance. 
	 * Notice that in this case the Station we store as a value will hold the name of the station that 
	 * gave us the shortest route and the distance is the current shortest distance for the key.
	 * @param shortestRoutes Map with the shortest route for each station from a given starting position. 
	**/
	public void setShortestRoutes(Map<String, Station> shortestRoutes) {
		this.shortestRoute=shortestRoutes;
	}
	
	/**
	 * BONUS EXERCISE THIS IS OPTIONAL
	 * Returns the path to the station given. 
	 * The format is as follows: Westside->stationA->.....stationZ->stationName
	 * Each station is connected by an arrow and the trace ends at the station given.
	 * 
	 * @param stationName - Name of the station whose route we want to trace
	 * @return (String) String representation of the path taken to reach stationName.
	 */
	public String traceRoute(String stationName) {
		if (stationName=="Westside") {return stationName;}
		String output="Westside";
		String next = shortestRoute.get(stationName).getCityName();
		Stack<String> cities  = new LinkedListStack<String>();
		while(next!="Westside") {
			cities.push(next);
			next=shortestRoute.get(next).getCityName();
		}
		while(!cities.isEmpty()) {
			output+="->"+cities.pop();
		}
		output+="->"+stationName;
		return output;
	}

}