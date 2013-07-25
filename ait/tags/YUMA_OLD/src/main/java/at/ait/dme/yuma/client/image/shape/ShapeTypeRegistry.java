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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A registry for shape types
 * 
 * @author Christian Sadilek
 */
public class ShapeTypeRegistry {

	private static final String[] ALL_TYPES = {Cross.class.getName(), Ellipse.class.getName(),
			Polygon.class.getName(), Polyline.class.getName(), Rectangle.class.getName(),
			VoidShape.class.getName()};

	private static final String[] GEO_TYPES = {GeoPoint.class.getName()};
	
	public static Set<String> allTypes() {
		return new HashSet<String>(Arrays.asList(ALL_TYPES));
	}
	
	public static Set<String> geoTypes() {
		return new HashSet<String>(Arrays.asList(GEO_TYPES));
	}
}
