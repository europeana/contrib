package at.ac.ait.dme.gtv.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Representation of a location
 *
 * @author Manuel Bernhardt
 */
public class Location implements IsSerializable {

    private static final long serialVersionUID = 3271155816904636479L;

    public enum PlaceType implements IsSerializable {
        COUNTRY, TOWN, STATE, COUNTY
    }

    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private PlaceType type;

    public Location() {
    }

    public Location(String name, Double latitude, Double longitude, PlaceType type) {
        super();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCountry() {
        return this.type == PlaceType.COUNTRY;
    }


}
