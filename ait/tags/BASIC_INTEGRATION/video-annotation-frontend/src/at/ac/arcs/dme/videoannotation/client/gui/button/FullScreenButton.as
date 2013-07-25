package at.ac.arcs.dme.videoannotation.client.gui.button
{
	import flash.display.Graphics;

	/** The FullScreenButton shows a "rectangle in the middle and four arrows pointing to the edges of the button"
     *
     * @author Stefan Pomajbik
     **/
	public class FullScreenButton extends Button
	{
		public function FullScreenButton()
		{
			super();
		}

		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);

	        var g:Graphics = icon.graphics;
	        g.clear();
	        g.lineStyle(1,iconColor,1);
	        g.beginFill(iconColor,1);
	        g.drawRect(5,5,2,2);
	        g.endFill();

			// arrow 4th quadrant
			g.moveTo(4,4);
			g.lineTo(0,0);
			g.lineTo(4,0);
			g.moveTo(0,0);
			g.lineTo(0,4);

			// arrow 3rd quadrant
			g.moveTo(4,8);
			g.lineTo(0,12);
			g.lineTo(0,8);
			g.moveTo(0,12);
			g.lineTo(4,12);

			// arrow 1st quadrant
			g.moveTo(8,4);
			g.lineTo(12,0);
			g.lineTo(8,0);
			g.moveTo(12,0);
			g.lineTo(12,4);

			// arrow 2nd quadrant
			g.moveTo(8,8);
			g.lineTo(12,12);
			g.lineTo(12,8);
			g.moveTo(12,12);
			g.lineTo(8,12);

	        centerIcon();
	    }
	}
}