package at.researchstudio.dme.imageannotation.client.test;

import at.researchstudio.dme.imageannotation.client.Application;
import at.researchstudio.dme.imageannotation.client.image.ImageRect;
import at.researchstudio.dme.imageannotation.client.image.StandardImageComposite;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

public class ImageZoomTest extends GWTTestCase {
	private static final int IMAGE_WIDTH = 499;
	private static final int IMAGE_HEIGHT = 223;
		
	public String getModuleName() {
		return "at.researchstudio.dme.imageannotation.Application";
	}

	public void testZoomInOut() {	      
		final Application app = new Application("http://dme.arcs.ac.at/test.jpg");		
		
		Timer timer = new Timer() {
			public void run() {
				final StandardImageComposite imageComposite = 
					(StandardImageComposite)app.getImageComposite();
				if(!imageComposite.isServerSideZoomEnabled()) finishTest();				
				assertEquals(imageComposite.getCurrentRect(), 
						new ImageRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT));
				assertEquals(imageComposite.getImageRect(), imageComposite.getCurrentRect());

				imageComposite.zoom(true);
				assertEquals(imageComposite.getCurrentRect(), 
						new ImageRect(0 -  Math.round(imageComposite.getZoomStepWidth() / 2.0f), 
								0 - Math.round(imageComposite.getZoomStepHeight() / 2.0f), 
								IMAGE_WIDTH+Math.round(imageComposite.getZoomStepWidth()), 
								IMAGE_HEIGHT+Math.round(imageComposite.getZoomStepHeight())));				
				assertEquals(imageComposite.getImageRect(), imageComposite.getCurrentRect());					
				
				imageComposite.zoom(false);
				assertEquals(imageComposite.getCurrentRect(), 
						new ImageRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT));							
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
