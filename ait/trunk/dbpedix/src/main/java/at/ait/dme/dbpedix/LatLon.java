package at.ait.dme.dbpedix;

/**
 * A simple wrapper class for a latitude/longitude coordinate pair.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class LatLon {
	
	private double lat, lon;
	
	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof LatLon))
			return false;
		
		LatLon ll = (LatLon) other;
		if (ll.lat != lat)
			return false;
		
		if (ll.lon != lon)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return (lat + "/" + lon).hashCode();
	}
	
	@Override
	public String toString() {
		return lat + "/" + lon;
	}

}
