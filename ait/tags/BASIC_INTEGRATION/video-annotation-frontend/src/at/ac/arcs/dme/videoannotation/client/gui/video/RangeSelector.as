package at.ac.arcs.dme.videoannotation.client.gui.video
{
	import mx.controls.HSlider;
	import mx.controls.sliderClasses.SliderThumb;
	import mx.core.mx_internal;
	import mx.events.FlexEvent;
	import mx.events.SliderEvent;

	use namespace mx_internal;

	/**
	 * The RangeSelector is an HSlider that displays a highlight between two thumbs
	 * 
	 * @author Christian Sadilek
	 **/
	public class RangeSelector extends HSlider
	{
		public function RangeSelector()
		{
			super();
		}

		override protected function createChildren():void {
        	super.createChildren();
		}

		override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void {
        	super.updateDisplayList(unscaledWidth,unscaledHeight);
        	var yFrom:Number = this.getThumbAt(0).y;
			var yHeight:Number = this.getThumbAt(0).height;
			var xFrom:Number = this.getThumbAt(0).x+this.getThumbAt(0).width/2;
			var xWidth:Number = this.getThumbAt(1).x-xFrom+this.getThumbAt(0).width/2;
			this.graphics.clear();
			this.graphics.beginFill(0x125567,0.5);
			this.graphics.drawRect(xFrom,yFrom,xWidth,yHeight);
        }
	}
}