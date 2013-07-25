package at.ait.dme.gwt.openlayers.client;

/**
 * Simple wrapper class for OpenLayers 'style' properties
 * 
 * @author Rainer Simon
 *
 */
public class Style {
	
	public String name;
	
	public int pointRadius = Integer.MIN_VALUE;
	
	public String fillColor = null;
	
	public String strokeColor = null;
	
	public double fillOpacity = Double.NEGATIVE_INFINITY;
	
	public int strokeWidth = Integer.MIN_VALUE;
	
	/**
	 * Creates a new empty style with a specified name.
	 * 
	 * @param name the name
	 */
	public Style(String name) {
		this.name = name;
	}
	
	/**
	 * Constructs the JavaScript representation of this style.
	 * @return the JavaScript
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();	
		sb.append("\n  \"" + name + "\":new $wnd.OpenLayers.Style({\n");
		
		// Add all properties that were set
		if (pointRadius != Integer.MIN_VALUE)
			sb.append("    pointRadius:" + pointRadius + ",\n");
		
		if (fillColor != null)
			sb.append("    fillColor:\"" + fillColor + "\",\n");
		
		if (fillOpacity != Double.NEGATIVE_INFINITY)
			sb.append("    fillOpacity:" + fillOpacity + ",\n");
		
		if (strokeColor != null)
			sb.append("    strokeColor:\"" + strokeColor + "\",\n");
		
		if (strokeWidth != Integer.MIN_VALUE)
			sb.append("    strokeWidth:" + strokeWidth + ",\n");		
		
		// Remove last ','
		sb.deleteCharAt(sb.length() - 2);
		
		// Close JS hash
		sb.append("  }),");
		return sb.toString();
	}

}
