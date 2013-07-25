package at.ac.arcs.dme.videoannotation.client.gui.button
{
	import flash.display.Shape;
	import flash.events.MouseEvent;

	import mx.core.UIComponent;

	/** Baseclass for buttons which own a icon
     *
     * @author Stefan Pomajbik
     **/
	public class Button extends UIComponent {
	    private var panelAlpha:Number = 0;
	    protected var icon:Shape;
	    private var _iconColor:uint = 0x125567;

		public function Button() {
        	super();
    	}

	    public function set iconColor(value:uint):void {
     	   _iconColor = value;
    	}

    	public function get iconColor():uint {
        	return _iconColor;
	    }

	    override public function get measuredWidth():Number {
        	return 21;
    	}

    	override public function get measuredHeight():Number {
        	return 21;
    	}

    	override protected function createChildren():void {
        	super.createChildren();

	        icon = new Shape();
    	    addChild(icon);
    	}

    	override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
        	super.updateDisplayList(unscaledWidth, unscaledHeight);

        	addEventListener(MouseEvent.ROLL_OVER, onRollOver);
        	addEventListener(MouseEvent.ROLL_OUT, onRollOut);

        	graphics.clear();
        	graphics.beginFill(_iconColor, panelAlpha);
        	graphics.drawRect(0, 0, unscaledWidth, unscaledHeight);
    	}

    	private function onRollOver(event:MouseEvent):void {
        	panelAlpha = .1;

        	invalidateDisplayList();
    	}

    	private function onRollOut(event:MouseEvent):void {
        	panelAlpha = 0;

        	invalidateDisplayList();
	    }

    	protected function centerIcon():void {
        	icon.x = int((unscaledWidth - icon.width)/2);
        	icon.y = int((unscaledHeight - icon.height)/2);
    	}
	}
}