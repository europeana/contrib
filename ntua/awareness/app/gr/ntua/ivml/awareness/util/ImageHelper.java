package gr.ntua.ivml.awareness.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

/**
 * Takes an original Image and provides calls to generate Story views, thumbnails etc.
 * @author Arne Stabenau 
 *
 */
public class ImageHelper {
	private static final Logger log = Logger.getLogger( ImageHelper.class );
	private static final int HEXRADIUS = 32;
	
	public BufferedImage original;
	
	public ImageHelper() {
		
	}
	
	public ImageHelper(File image ){ 
		try{
		   FileInputStream fos=new FileInputStream(image);	
		   this.original = ImageIO.read( fos );
		}catch (Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public ImageHelper(BufferedImage imageIn ){
		try{
		   original = imageIn;
		}catch (Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public ImageHelper(InputStream imageStream ){
		try{
		   original = ImageIO.read( imageStream );
		}catch (Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	public BufferedImage getTest() {
		BufferedImage bi = new BufferedImage( 300, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics gr = bi.getGraphics();
		gr.setColor(new Color( 255,255,255,220));
		gr.fillRect(0, 0, 300, 200 );
		gr.dispose();
		return bi;
	}
	
	public BufferedImage getOriginal() {
		return original;
	}
	
	
	public BufferedImage getScaledBox( int maxWidth, int maxHeight ) {
		if( original == null ) return null;
		int orgWidth = original.getWidth();
		int orgHeight = original.getHeight();
		
		float scaleWidth = (float)maxWidth / orgWidth;
		float scaleHeight = (float)maxHeight / orgHeight;
		
		int targetWidth, targetHeight;
		// we want to multiply with the smaller number so both numbers stay under the max
		if( scaleWidth < scaleHeight ) {
			targetWidth = maxWidth;
			targetHeight = (int)(orgHeight * scaleWidth );
		} else {
			targetHeight = maxHeight;
			targetWidth = (int)(orgWidth*scaleHeight);
		}
		
		// now created target Image
		BufferedImage result = new BufferedImage( targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = result.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, result.getWidth(), result.getHeight(), null);
        g2.dispose();

        return result;
	}
	
	public BufferedImage getScaledWidth( int fixedWidth ) {
		if( original == null ) return null;
		int orgWidth = original.getWidth();
		/*if original width is smaller that 70% of the final width return the original image*/
		if(orgWidth < fixedWidth*7/10){return original;}
		int orgHeight = original.getHeight();
		
		float scaleWidth = (float)fixedWidth / orgWidth;
		
		int targetWidth, targetHeight;
		targetWidth = fixedWidth;
		targetHeight = (int)(orgHeight * scaleWidth );

		// now created target Image
		BufferedImage result = new BufferedImage( targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = result.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, result.getWidth(), result.getHeight(), null);
        g2.dispose();

        return result;
	}
	
	public BufferedImage addWhiteLayer( BufferedImage in ) {
        Graphics2D g2 = in.createGraphics();
        Color c = new Color(255,255,255,128);
        g2.setColor(c);
        g2.fillRect(0, 0, in.getWidth(), in.getHeight());
        g2.dispose();
        return in;
	}
	
	public static ConvolveOp getGaussianBlurFilter(int radius,
			boolean horizontal) {
		if (radius < 1) {
			throw new IllegalArgumentException("Radius must be >= 1");
		}

		int size = radius * 2 + 1;
		float[] data = new float[size];

		float sigma = radius / 3.0f;
		float twoSigmaSquare = 2.0f * sigma * sigma;
		float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
		float total = 0.0f;

		for (int i = -radius; i <= radius; i++) {
			float distance = i * i;
			int index = i + radius;
			data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
			total += data[index];
		}

		for (int i = 0; i < data.length; i++) {
			data[i] /= total;
		}        

		Kernel kernel = null;
		if (horizontal) {
			kernel = new Kernel(size, 1, data);
		} else {
			kernel = new Kernel(1, size, data);
		}
		return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	}

	// add a shadow bottom and right of given width 
	public static BufferedImage dropShadow( BufferedImage in, int shadowWidth ) {
		
		
		// move bottom right
		// gray the image, 
		// blur it a bit
		// paint over original

		BufferedImage result = new BufferedImage( in.getWidth()+shadowWidth, in.getHeight()+shadowWidth,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = result.createGraphics();
		g.drawImage(in, shadowWidth>>1, shadowWidth>>1, null);
		// g.drawImage(in, 0, 0, null);

		// now gray out the image, keep alpha
		int[] pixels = new int[result.getWidth()*result.getHeight()];
		result.getRGB(0, 0, result.getWidth(), result.getHeight(), pixels, 0 /*offset*/, result.getWidth() /*scansize */ );
		
		for( int i=0; i<pixels.length; i++ ) {
			// keep only alpha channel and some grey
			pixels[i] = (pixels[i] & (0xff000000)) | 0x00505050;
		}		
		result.setRGB(0, 0, result.getWidth(), result.getHeight(), pixels, 0 /*offset*/, result.getWidth() /*scansize */ );
		g.dispose();
		
		// blur the opaque gray into the transparent surrounding
		result = getGaussianBlurFilter((int)(shadowWidth*1.2), true).filter(result, null);
        result = getGaussianBlurFilter((int)(shadowWidth*1.2), false).filter(result, null);
        g = result.createGraphics();
		g.drawImage(in, 0, 0, null);
		
		g.dispose();
		return result;
	}
	
	public static BufferedImage cropToHeight( BufferedImage in, int maxHeight ) {
		if( in.getHeight() <= maxHeight ) return in;
		BufferedImage result = new BufferedImage( in.getWidth(), maxHeight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = result.createGraphics();

		int cropTotal = in.getHeight()-maxHeight;
		int sourceY1 = cropTotal >> 1;
		int sourceY2 = in.getHeight()-cropTotal+sourceY1;
		
		
		gr.drawImage(in, 0, 0, result.getWidth(), result.getHeight(), 
				0 /*sx1*/, sourceY1 /*sy1*/, 
				in.getWidth() /* sx2 */, sourceY2 /* sy2 */, null );
		
		return result;
	}
	
	public static BufferedImage shadedHexagon( BufferedImage in ) {
		BufferedImage result = new BufferedImage( (int)(HEXRADIUS*Math.sqrt(3.0)+6), (int) (HEXRADIUS*2+5),
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D gr = result.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		gr.drawImage(in, 0, 0, result.getWidth(), result.getHeight(), 
				0 /*sx1*/, 0 /*sy1*/, 
				in.getWidth() /* sx2 */, in.getHeight() /* sy2 */, null );
		

		Area mask = new Area( new Rectangle( 0,0,result.getWidth(), result.getHeight()));
		// create the clipping hexagon shape
		int midx = result.getWidth()/2;
		int midy = result.getHeight()/2;
		int dx = (int) Math.round( HEXRADIUS*Math.sqrt(3.0)/2.0 );
		int dy = HEXRADIUS/2;
		
		int[][] hexOutline = {{midx, midy-HEXRADIUS},{midx+dx, midy-dy},{midx+dx, midy+dy},
				{midx, midy+HEXRADIUS},{midx-dx, midy+dy},{midx-dx, midy-dy}};
		
		Polygon hex= new Polygon();
		for( int[] point:hexOutline) {
			hex.addPoint(point[0], point[1]);
		}

		mask.subtract(new Area( hex ));

		gr.setColor(Color.WHITE);
		gr.setComposite(AlphaComposite.Clear);
		
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Clear the cut out area
        gr.fill(mask);
        
        // light grey overlay on left side
        Polygon cubeFaceLeft = new Polygon();
        cubeFaceLeft.addPoint( midx-dx, midy-dy  );
        cubeFaceLeft.addPoint( midx, midy  );
        cubeFaceLeft.addPoint( midx, midy+HEXRADIUS  );
        cubeFaceLeft.addPoint( midx-dx, midy+dy );
        mask = new Area( cubeFaceLeft );
        
		gr.setColor( new Color( 100,100,100,180 ));
		gr.setComposite(AlphaComposite.SrcOver);
  
        gr.fill(mask);
        
		// darker grey overlay on right side
        Polygon cubeFaceRight = new Polygon();
        cubeFaceRight.addPoint( midx+dx, midy-dy  );
        cubeFaceRight.addPoint( midx, midy  );
        cubeFaceRight.addPoint( midx, midy+HEXRADIUS  );
        cubeFaceRight.addPoint( midx+dx, midy+dy );
        mask = new Area( cubeFaceRight );
        
		gr.setColor( new Color( 50,50,50,180 ));
        gr.fill(mask);
        
        // some lines
        gr.setColor(Color.black);
        gr.setStroke( new BasicStroke( 0.8f));
        //gr.drawLine(midx, midy, midx-dx, midy-dy);
        //gr.drawLine(midx, midy, midx+dx, midy-dy);
        //gr.drawLine(midx, midy, midx, midy+HEXRADIUS);

        // outline
        for( int i=0; i<6; i++ ) {
        	// gr.drawLine( hexOutline[i][0], hexOutline[i][1],
        	//		hexOutline[(i+1)%6][0], hexOutline[(i+1)%6][1]);
        }
		gr.dispose();
        
        // some drop shadow
		result = dropShadow(result, 4 );
		return result;
	}
		
	public BufferedImage shadedHexagon() {
		return shadedHexagon(original);
	}
	
	
	public static String getMimeType(File file) {
        String mimetype = "";
        if (file.exists()) {
            if (getSuffix(file.getName()).equalsIgnoreCase("png")) {
                mimetype = "image/png";
            } else {
                javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
                mimetype = mtMap.getContentType(file);
            }
        }
        return mimetype;
    }
	
	public static String getSuffix(String filename) {
        String suffix = "";
        int pos = filename.lastIndexOf('.');
        if (pos > 0 && pos < filename.length() - 1) {
            suffix = filename.substring(pos + 1);
        }
        System.out.println("suffix: " + suffix);
        return suffix;
    }
	
	public static void  main( String args[]) {
		// render some result to watch
		FileInputStream fis = null;
		ImageHelper ih = null;
		try {
			fis = new FileInputStream("public/images/background.jpg");
			ih = new ImageHelper(fis);
		} catch( Exception e ) {
			log.error( "Problem",e);
		} finally {
			if( fis != null ) try { fis.close(); } catch( Exception e2 ) {}
		}
		
		final BufferedImage render = ih.cropToHeight( ih.getScaledWidth( 500 ), 100 ); 
				// ih.dropShadow( ih.addWhiteLayer( ih.getScaledWidth( 300 )), 6 );

		final Color light = new Color( 200,200,200,255 );
		final Color dark = new Color( 100,100,100,255 );
		final int rectSize = 10;
		
		JFrame main = new JFrame() {
			
			@Override
			public void paint(Graphics gr1) {
				Graphics2D gr = (Graphics2D) gr1;
				// paint light grey checkered background
				// paintBackground( gr );
				Color w = new Color( 255,255,255,255 );
				gr.setColor(w);
				gr.fillRect(0, 0, gr.getDeviceConfiguration().getBounds().width, gr.getDeviceConfiguration().getBounds().height);
				paintImage( gr );
			}
			
			private void paintBackground( Graphics2D gr ) {
				boolean colodd = false;
				for( int x=0; x<gr.getDeviceConfiguration().getBounds().width; x+=rectSize ) {
					boolean lineodd = colodd;
					for( int y=0; y<gr.getDeviceConfiguration().getBounds().height; y+= rectSize ) {
						Color c= lineodd?light:dark;
						gr.setColor(c);
						gr.fillRect(x, y,rectSize, rectSize );
						lineodd = !lineodd;
					}
					colodd = !colodd;
				}	
			}
			
			private void paintImage( Graphics2D gr ) {
				gr.drawImage(render, 25, 45, null );
			}
		};
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setSize( render.getWidth()+50, render.getHeight()+70);
		main.repaint();
		main.setVisible(true);
	}
}
