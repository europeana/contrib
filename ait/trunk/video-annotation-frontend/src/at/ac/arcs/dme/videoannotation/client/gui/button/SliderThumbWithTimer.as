package at.ac.arcs.dme.videoannotation.client.gui.button
{
	import flash.display.Graphics;
	import flash.events.MouseEvent;

	[Style(name="sliderThumbColor", type="uint", format="Color", inherit="no")]

    /**
    * Slider thumb used to point the specific time frame being played
    * 
    * @author Stefan Pomajbik
    **/
	public class SliderThumbWithTimer extends Button
	{
		private var _sliderThumbColor:uint;

		public function SliderThumbWithTimer()
		{
			super();
			buttonMode = true;
		}

		// Creates any child components of the component.
		override protected function createChildren():void {
        	super.createChildren();

        	// set the default values for the style variables
	        if(getStyle("sliderThumbColor") == undefined) setStyle("sliderThumbColor", 0xFFFF00);
        	_sliderThumbColor = getStyle("sliderThumbColor");
  		}

		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);

	        var g:Graphics = icon.graphics;

	        g.clear();
	        g.beginFill(iconColor);


			// slider (vertical bar)
            g.lineStyle(1,iconColor, 1);
			g.moveTo(4,6);
			g.lineTo(4,unscaledHeight);

			// slider (arrow which points to the line)
			g.lineStyle(1,iconColor, 1);
			g.moveTo(4,4);
			g.lineTo(0,0);
			g.lineTo(8,0);
			g.lineTo(4,4);

	        centerIcon();
	    }

	    //Commits any changes to component properties
		override protected function commitProperties():void {
        	super.commitProperties();

        	this.iconColor = _sliderThumbColor;
		}

//		override protected function measure():void {
//        	super.measure();
//	        measuredWidth = 19;
//    	    measuredHeight = unscaledHeight/2 +2;
//    	}
//
//    	override public function get measuredWidth():Number {
//        	return 19;
//    	}
//
//    	override public function get measuredHeight():Number {
//        	return unscaledHeight/2 +2;
//    	}

	}
}