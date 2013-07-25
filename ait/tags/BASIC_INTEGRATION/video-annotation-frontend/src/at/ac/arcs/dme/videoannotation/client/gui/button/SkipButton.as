package at.ac.arcs.dme.videoannotation.client.gui.button
{
	/** The SkipButton shows a "triangle pointing to a vertical line"
     *
     * @author Stefan Pomajbik
     **/
	public class SkipButton extends Button {

		 public function SkipButton() {
	        super();
	    }

	    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);

	        icon.graphics.clear();
	        icon.graphics.beginFill(iconColor);

			var x:uint = 0;
	        var y:uint = 0;
	        var w:int = 1;
	        var h:int = 9;

            for(; h>0; h -= 2)
               icon.graphics.drawRect(x++, y++, w, h);


			icon.graphics.lineStyle(2,iconColor,1);
			icon.graphics.moveTo(6,0);
			icon.graphics.lineTo(6,9);


	        centerIcon();
	    }
	}
}