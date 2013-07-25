package at.ac.arcs.dme.videoannotation.client.gui.button
{
	import flash.events.MouseEvent;

	import mx.controls.Alert;

	/** The PlayPauseButton shows a "triangle" for play and "two vertical lines" for pause
     *
     * @author Stefan Pomajbik
     **/
	public class PlayPauseButton extends Button {
    	private var _state:String = "pause";

		public function PlayPauseButton() {
			super();
    	}


	    public function set state(value:String):void {
	        _state = value;
	        invalidateDisplayList();
	    }

	    public function get state():String {
	        return _state;
	    }

	    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);

	        icon.graphics.clear();
	        icon.graphics.beginFill(iconColor);

	        if(_state == "play") {
	            var x:uint = 0;
	            var y:uint = 0;
	            var w:int = 1;
	            var h:int = 9;

	            for(; h>0; h -= 2)
	                icon.graphics.drawRect(x++, y++, w, h);
	        }

	        if(_state == "pause") {
	            icon.graphics.drawRect(0, 0, 3, 7);
	            icon.graphics.drawRect(4, 0, 3, 7);
	        }

	        centerIcon();
	    }

	}
}