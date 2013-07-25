package at.ac.arcs.dme.videoannotation.client.gui
{
	/** The AnnotationButton shows a "pen on a paper icon"
    *
    * @author Stefan Pomajbik
    **/
	public class AnnotationButton extends Button {

		 public function AnnotationButton() {
	        super();
	    }

	    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);

	        icon.graphics.clear();
			icon.graphics.lineStyle(1,iconColor,1);
			icon.graphics.moveTo(1,0);
			icon.graphics.lineTo(9,0);
			icon.graphics.moveTo(10,1);
			icon.graphics.lineTo(10,4);
			icon.graphics.lineTo(12,2);
			icon.graphics.lineTo(14,4);
			icon.graphics.lineTo(8,10);
			icon.graphics.lineTo(6,8);
			icon.graphics.lineTo(6,10);
			icon.graphics.lineTo(8,10);
			icon.graphics.moveTo(6,8);
			icon.graphics.lineTo(10,4);
			icon.graphics.moveTo(10,8);
			icon.graphics.lineTo(10,13);
			icon.graphics.lineTo(9,14);
			icon.graphics.lineTo(1,14);
			icon.graphics.lineTo(0,13);
			icon.graphics.lineTo(0,1);
	        centerIcon();
	    }
	}
}