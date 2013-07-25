package at.ac.arcs.dme.videoannotation.client.gui.video
{
	import at.ac.arcs.dme.videoannotation.client.event.UserEvent;
	import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
	
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	
	import mx.core.UIComponent;
	import mx.events.FlexEvent;
	import mx.events.IndexChangedEvent;

	[Style(name="fragmentColor", type="uint", format="Color", inherit="no")]

	[Event(name="change", type="mx.events.IndexChangedEvent")]


	/** The FragmentBar lets you display a colored section (fragment)
	 *  of a bar.
   	 *
   	 * @author Stefan Pomajbik
   	 * @author Christian Sadilek
   	 * @author Manuel Gay
   	 **/
	public class FragmentBar extends UIComponent
	{
		/** value holders **/
		private var g:Graphics;
		private var _curFramentFrom:Number=0;
		private var _curFragmentTo:Number=0;

		/** display objects **/
		protected var bound:Sprite;

		/** style variables **/
		private var _fragmentColor:uint;
		private var _selectionColor:uint;

		/** properties **/
		private var _reference:*;
		private var _maximum:Number = 0;
		private var _highlighted:Boolean=false;
		private var _selected:Boolean=false;

		public function FragmentBar() {
			super();
		}

		public function get maximum():Number {
			return this._maximum;
		}

		public function set maximum(value:Number):void {
			this._maximum = value;
			this.invalidateProperties();
			this.invalidateDisplayList();
		}

		// Creates any child components of the component.
		override protected function createChildren():void {
        	super.createChildren();

        	if(getStyle("fragmentColor")  == undefined) setStyle("fragmentColor", 0x125567);
        	_fragmentColor = getStyle("fragmentColor");

        	if(getStyle("selectionColor")  == undefined) setStyle("selectionColor", 0xFFFF0);
        	_selectionColor = getStyle("selectionColor");

        	bound = new Sprite();
	        addChild(bound);

	        bound.addEventListener(MouseEvent.CLICK,onMouseClick);
			bound.addEventListener(MouseEvent.MOUSE_OUT,onMouseOut);
			bound.addEventListener(MouseEvent.MOUSE_OVER,onMouseOver);
  		}

  		override protected function commitProperties():void {
        	super.commitProperties();
        }

		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
	        super.updateDisplayList(unscaledWidth, unscaledHeight);

			g = bound.graphics;
            g.clear();

			var _color:uint = 0x125567;
            var barheight:Number = unscaledHeight;

			var fragmentFrom:Number =  Math.max(0,(unscaledWidth/this._maximum)*(_curFramentFrom));
			var fragmentWidth:Number = Math.max(0,(unscaledWidth/this._maximum)*(_curFragmentTo-_curFramentFrom));

			// the framentbar must never succeed the border
			if (fragmentWidth > unscaledWidth - fragmentFrom) fragmentWidth = unscaledWidth -fragmentFrom;

			if (isNaN(fragmentFrom)) fragmentFrom = 0;
			if (isNaN(fragmentWidth)) fragmentWidth = 0;

    		// we draw a rect which indicates the total amount of time
    		g.lineStyle(1,1,1);
    		g.drawRect(0,0,unscaledWidth,barheight);

    		// we draw a filled rect which indicates the fragment
    		g.beginFill(_fragmentColor,1);
    		g.drawRect(fragmentFrom,0,fragmentWidth,barheight);
			g.endFill();

			// we draw the highlight if its highlighted
    		if (this._highlighted) {
    			g.lineStyle(2, _selectionColor, 1);
				g.beginFill(_selectionColor,0.1);
				g.drawRect(0,0,unscaledWidth,barheight);
				g.endFill();
    		}

    		// we draw the selection if its selected
    		if (this._selected) {
    			g.lineStyle(1,_selectionColor, 0.5);
				g.beginFill(_selectionColor,0.3);
				g.drawRect(0,0,unscaledWidth,barheight);
				g.endFill();
    		}

	    }

		public function setFragment(timeFrom:Number, timeTo:Number):void {
			this._curFragmentTo=timeTo;
			this._curFramentFrom=timeFrom;
			invalidateDisplayList();
		}

    	override public function get measuredHeight():Number {
        	return this.height;
    	}

    	public function set relatedObject (object:*):void {
    		this._reference=object;
    		if (object is VideoAnnotation) {
    			this.toolTip = (object as VideoAnnotation).title;
    		}
    	}

    	public function get relatedObject():* {
    		return this._reference;
    	}

		// wether this fragmentBar is selected or not
    	public function set selected(isSelected:Boolean):void {
    		this._selected=isSelected;
    		invalidateDisplayList();
    	}

		// wether this fragmentBar is highlighted or not
    	private function set highlight(isHighlighted:Boolean):void {
    		this._highlighted=isHighlighted;
    		invalidateProperties();
    		invalidateDisplayList();
    		dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    	}

       	private function onMouseClick(event:MouseEvent):void {
            var selectionChangedEvent:UserEvent = new UserEvent(UserEvent.SELECT_ANNOTATION_FRAGMENTBAR, this.relatedObject as VideoAnnotation);
    		dispatchEvent(selectionChangedEvent);    	
    	}

    	private function onMouseOut (event:MouseEvent):void {
    		highlight = false;
    		invalidateDisplayList();
    	}

    	private function onMouseOver (event:MouseEvent):void {
    		highlight = true;
    		invalidateDisplayList();
    	}

	}
}