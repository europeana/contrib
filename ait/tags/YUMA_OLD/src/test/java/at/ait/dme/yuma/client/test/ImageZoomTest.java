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

package at.ait.dme.yuma.client.test;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.image.ImageRect;
import at.ait.dme.yuma.client.image.StandardImageComposite;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

public class ImageZoomTest extends GWTTestCase {
	private static final int IMAGE_WIDTH = 499;
	private static final int IMAGE_HEIGHT = 223;
		
	public String getModuleName() {
		return "at.ait.dme.yuma.Application";
	}

	public void testZoomInOut() {	      
		final Application app = new Application("http://dme.arcs.ac.at/test.jpg");		
		
		Timer timer = new Timer() {
			public void run() {
				final StandardImageComposite imageComposite = 
					(StandardImageComposite)app.getImageComposite();
				if(!imageComposite.isServerSideZoomEnabled()) finishTest();				
				assertEquals(imageComposite.getCurrentRect(), new ImageRect(
						0, 0, IMAGE_WIDTH, IMAGE_HEIGHT));
				assertEquals(imageComposite.getImageRect(), imageComposite.getCurrentRect());

				imageComposite.zoom(true);
				assertEquals(imageComposite.getCurrentRect(), new ImageRect(
						0 -  Math.round(imageComposite.getZoomStepWidth() / 2.0f), 
						0 - Math.round(imageComposite.getZoomStepHeight() / 2.0f), 
						IMAGE_WIDTH+Math.round(imageComposite.getZoomStepWidth()), 
						IMAGE_HEIGHT+Math.round(imageComposite.getZoomStepHeight())));				
				assertEquals(imageComposite.getImageRect(), imageComposite.getCurrentRect());					
				
				imageComposite.zoom(false);
				assertEquals(imageComposite.getCurrentRect(), new ImageRect(
						0, 0, IMAGE_WIDTH, IMAGE_HEIGHT));							
				assertEquals(imageComposite.getImageRect(), imageComposite.getCurrentRect());

				Timer timer = new Timer() {
					public void run() {
						assertTrue(imageComposite.isServerSideZoomEnabled());					
						finishTest(); 
					}
				};
				timer.schedule(500);
			}
		};
		timer.schedule(100);
		
		delayTestFinish(3000);
	}
}
