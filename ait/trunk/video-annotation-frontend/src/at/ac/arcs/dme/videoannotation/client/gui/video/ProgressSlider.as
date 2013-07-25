package at.ac.arcs.dme.videoannotation.client.gui.video
{
	import at.ac.arcs.dme.videoannotation.client.gui.button.Button;
	import at.ac.arcs.dme.videoannotation.client.gui.button.SliderThumbWithTimer;
	import at.ac.arcs.dme.videoannotation.client.gui.skins.SliderProgressSkin;
	
	import flash.display.DisplayObject;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	
	import mx.core.IFlexDisplayObject;
	import mx.core.UIComponent;
	import mx.core.mx_internal;
	import mx.events.SliderEvent;
	import mx.styles.ISimpleStyleClient;
	
	use namespace mx_internal;

	[Event(name="change", type="mx.events.SliderEvent")]
	[Event(name="thumbDrag", type="mx.events.SliderEvent")]
	[Event(name="thumbPress", type="mx.events.SliderEvent")]
	[Event(name="thumbRelease", type="mx.events.SliderEvent")]


	[Style(name="progressColor", type="uint", format="Color", inherit="no")]

	[Style(name="progressSkin", type="Class", inherit="no")]

	/** The ProgressSlider is a combined thumb control and a progress bar
     *
     * @author Stefan Pomajbik
     * @author Christian Sadilek
     **/
	public class ProgressSlider extends UIComponent
	{
		/** constants **/
		//private static var timerLabelDefault:String = "Loading";

		/** value holder **/
		private var valueChanged:Boolean = false;
		private var g:Graphics;
		private var skinClass:Class;
		private var thumbIsDown:Boolean = false;
		// holds the offset of the click
		private var xOffset:Number;

		/** display Objects **/
		protected var bound:Sprite;
		private var progressBar:IFlexDisplayObject;
		private var sliderThumb:Button;

		/** properties **/
		// the maximum value that can be set (the value which indicates the very right position on the slider)
		private var _maximum:Number = 100;
		private var _value:Number = 0;
		private var _progress:Number = 20;
		private var _showFragment:Boolean = true;
		private var _fragmentFromTime:Number = 0;
		private var _fragmentToTime:Number = 0;

		public function ProgressSlider()
		{
			super();
		}

		public function set showFragment(value:Boolean):void {
			this._showFragment = value;
			invalidateDisplayList();
		}

		public function get fragementFromTime():Number {
			return this._fragmentFromTime;
		}
		
		public function get fragmentToTime():Number {
		    return this._fragmentToTime;
		}

		public function get maximum():Number {
			return this._maximum;
		}

		public function set maximum(value:Number):void {
			this._maximum = value;
		}

		public function get sliderThumbWidth():Number {
			return this.sliderThumb.width;
		}

		public function get value():Number {
        	return (maximum / unscaledWidth) * (sliderThumb.x);
    	}

		public function set value(value:Number):void {
			this._value = value;
			valueChanged = true;

			invalidateProperties();
			invalidateDisplayList();
		}

		private function get boundMin():Number {
        	return 0;
    	}

    	private function get boundMax():Number {
        	return Math.max(sliderThumb.width/2, bound.width);
    	}

		// Creates any child components of the component.
		override protected function createChildren():void {
        	super.createChildren();

   	       	bound = new Sprite();
	        addChild(bound);

			if(!getStyle("progressColor")) setStyle("progressColor", 0x444444);

	        if(!getStyle("progressSkin")) setStyle("progressSkin", SliderProgressSkin)

	        if (!progressBar) {
            	skinClass = getStyle("progressSkin");
                progressBar =  new skinClass();

            	if (progressBar is ISimpleStyleClient)
                	ISimpleStyleClient(progressBar).styleName = this;

				// we add the progresBar to the back
            	addChildAt(DisplayObject(progressBar), 0);
        	}

        	systemManager.addEventListener(MouseEvent.MOUSE_UP, sliderThumb_onMouseUp);

        	sliderThumb = new SliderThumbWithTimer();
        	addChild(sliderThumb);
        	sliderThumb.addEventListener(MouseEvent.MOUSE_DOWN, sliderThumb_onMouseDown);

        	addEventListener(MouseEvent.MOUSE_OUT, sliderThumb_onMouseOut);
			addEventListener(MouseEvent.MOUSE_UP, sliderThumb_onMouseUp);
		}

		public function set progress(value:Number):void {
        	this._progress = value;
	        invalidateDisplayList();
    	}

    	override protected function commitProperties():void {
        	super.commitProperties();
        }

        public function get sliderPos():Number {
        	return sliderThumb.x + (sliderThumb.width / 2);
        }

		// Draws the object and/or sizes and positions its children.
		// This is an advanced method that you might override
		// when creating a subclass of UIComponent.
		override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void {
        	super.updateDisplayList(unscaledWidth,unscaledHeight);
			var w:Number;

			g = bound.graphics;
            g.clear();
        	var c:Number = getStyle("progressColor");
        	g.beginFill(c, 0.3);

        	(_progress == 100) ? w = unscaledWidth : w = Math.max(_progress*unscaledWidth/100, 0)
        	progressBar.setActualSize(Math.round(_progress*unscaledWidth/100), unscaledHeight/2);
			// we move the progressBar to the middle of the thumb
			progressBar.x = sliderThumb.width/2;

            // the timeline starts from the middle of the thumb
            g.drawRect(sliderThumb.width/2, 0, unscaledWidth, unscaledHeight/2);

            if (_showFragment) {
				var fFrom:Number = (_fragmentFromTime/_maximum)*(unscaledWidth) + sliderThumb.width/2;
				var fWidth:Number = ((_fragmentToTime/_maximum)*(unscaledWidth) + sliderThumb.width/2) - fFrom;
				// in case of a wrong set _maximum (NaN)
				if (fWidth > unscaledWidth-fFrom) fWidth = unscaledWidth + sliderThumb.width/2 - fFrom;
				g.beginFill(0xFFFF00,0.4);
				g.drawRect(fFrom,0,fWidth,unscaledHeight/2);
			}

			// slider
			// if the value was changed, we have to reposition the slider
			if(valueChanged) {
            	sliderThumb.x = (_value/_maximum)*(unscaledWidth);
            	valueChanged = false;
        	}

			// position
			sliderThumb.x = Math.max(0,sliderThumb.x);
			sliderThumb.y = -8;

			// size
			sliderThumb.setActualSize(8, unscaledHeight+8);

        }

        private function isSliderHit(pt:Point):Boolean {
        	var sw:Number = sliderThumb.width;
        	if ((pt.x >= (sliderThumb.x - sw/2) ) && (pt.x <= (sliderThumb.x + sw/2))) {
        		return true;
        	}
        	return false;
        }

        private function sliderThumb_onMouseUp(event:MouseEvent):void {
        	systemManager.removeEventListener(MouseEvent.MOUSE_MOVE, sliderThumb_onMouseMove, true);
        	if (thumbIsDown) {
	        	var e:SliderEvent = new SliderEvent(SliderEvent.THUMB_RELEASE);
	       		e.value = value;
	       		dispatchEvent(e);

	        	e = new SliderEvent(SliderEvent.CHANGE);
	            e.value = value;
	            dispatchEvent(e);

	        	thumbIsDown = false;
        	}

        }

        private function sliderThumb_onMouseDown(event:MouseEvent):void {
        	systemManager.addEventListener(MouseEvent.MOUSE_MOVE, sliderThumb_onMouseMove, true);
        	xOffset = event.localX;

        	var pt:Point = new Point(event.stageX, event.stageY);
        	pt = globalToLocal(pt);

        	thumbIsDown = true;
       		var e:SliderEvent = new SliderEvent(SliderEvent.THUMB_PRESS);
  			e.value = value;
   			dispatchEvent(e);
        }

        private function sliderThumb_onMouseOut(event:MouseEvent):void {
        	cursorManager.removeBusyCursor();
        }

        private function sliderThumb_onMouseMove(event:MouseEvent):void {
			if (!thumbIsDown) return;
			var pt:Point = new Point(event.stageX, event.stageY);
			pt = globalToLocal(pt);

            sliderThumb.x = Math.min(Math.max(pt.x - xOffset, boundMin), boundMax);

			invalidateDisplayList();
            var e:SliderEvent = new SliderEvent(SliderEvent.THUMB_DRAG);
        	e.value = value;
        	dispatchEvent(e);
        }

         public function markFragment(fromTime:Number,toTime:Number):void {
        	this._fragmentFromTime = fromTime;
        	this._fragmentToTime = toTime;
        	this.showFragment = true;
        	invalidateProperties();
        	invalidateDisplayList();
        }
	}
}