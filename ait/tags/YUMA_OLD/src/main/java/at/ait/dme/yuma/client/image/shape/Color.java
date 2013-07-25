/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.client.image.shape;

import java.io.Serializable;

/**
 * represents a rgb color
 * 
 * @author Christian Sadilek
 */
public class Color implements Serializable {
	private static final long serialVersionUID = 6998584396915480113L;
	
	private int r, g, b;
	
	public Color() {}

	/**
	 * parses a hex color
	 * 
	 * @param hexValue
	 */
	public Color(String hexValue) {
		if(hexValue.length()!=6) throw new NumberFormatException("invalid color format");
		
		r = Integer.parseInt(hexValue.substring(0,2),16);
		g = Integer.parseInt(hexValue.substring(2,4),16);
		b = Integer.parseInt(hexValue.substring(4,6),16);		
	}
	
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Color)) return false;
		if(this==obj) return true;
		
		Color color = (Color)obj;
		if(this.r!=color.getR()) return false;
		if(this.g!=color.getG()) return false;
		if(this.b!=color.getB()) return false;
	
		return true;
	
	}

	public int hashCode() {
		return r ^ g ^ b;
	}		
	
	public String toString() {
		return "red=" + r + ", green=" + g + ", blue=" + b;
	}
	
	public String toRGBString() {
		//gwt does not support String.format
		String red = Integer.toHexString(r);
		if(red.length()==1) red = "0" + red;
		String green = Integer.toHexString(g);
		if(green.length()==1) green = "0" + green;
		String blue = Integer.toHexString(b);
		if(blue.length()==1) blue = "0" + blue;

		return "#"+red+green+blue;
	}
}
