package main;

import data_structures.HashSet;
import data_structures.HashTableSC;
import data_structures.LinkedListStack;
import data_structures.SimpleHashFunction;
import interfaces.List;
import interfaces.Map;
import interfaces.Set;
import interfaces.Stack;

/**
 * This is the Station class. 
 * @author Luis J. Cruz
 */

public class Station {
	
	private String name;
	private int distance;
	 
	/**
	 * Constructor that creates a station. The station will have the format (City Name,Distance).
	 * Note: What the station name and distance represent will be dependent on the context in which it is used.
	 * @param name String of the name of the city the station is located in.  
	 * @param dist Integer number of the distance in kilometers from the station to another point.
	**/
	public Station(String name, int dist) {
		this.name=name;
		this.distance=dist;
	}
	
	/**
	 * Note: What the station name represent will be dependent on the context in which it is used.
	 * @return String of the name of the city the station is located in.  
	**/
	public String getCityName() {
		return this.name;
	}
	
	/**
	 * Note: What the station name represent will be dependent on the context in which it is used.
	 * Sets a new name for the city the station is located in.
	 * @param cityName String of the name of the city the station is located in.  
	**/
	public void setCityName(String cityName) {
		this.name=cityName;
	}
	
	/**
	 * Note: What the station distance represent will be dependent on the context in which it is used.
	 * @return Integer number of the distance in kilometers from the station to another point.
	**/
	public int getDistance() {
		return this.distance;
	}
	
	/**
	 * Note: What the station distance represent will be dependent on the context in which it is used.
	 * Sets a new distance in kilometers from the station to another point.
	 * @param cityName String of the name of the city the station is located in.  
	**/
	public void setDistance(int distance) {
		this.distance=distance;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		return this.getCityName().equals(other.getCityName()) && this.getDistance() == other.getDistance();
	}
	@Override
	public String toString() {
		return "(" + this.getCityName() + ", " + this.getDistance() + ")";
	}

}
