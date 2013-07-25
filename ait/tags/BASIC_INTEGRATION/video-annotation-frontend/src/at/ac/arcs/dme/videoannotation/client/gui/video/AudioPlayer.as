package at.ac.arcs.dme.videoannotation.client.gui.video
{
	import at.ac.arcs.dme.videoannotation.client.event.RangeEvent;
	import at.ac.arcs.dme.videoannotation.client.gui.button.FullScreenButton;
	import at.ac.arcs.dme.videoannotation.client.gui.button.PlayPauseButton;
	import at.ac.arcs.dme.videoannotation.client.gui.button.SkipButton;
	import at.ac.arcs.dme.videoannotation.client.gui.skins.RangeSliderTrackSkin;
	
	import flash.display.BlendMode;
	import flash.display.StageDisplayState;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.events.FullScreenEvent;
	import flash.events.MouseEvent;
	import flash.events.ProgressEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.media.Sound;
	import flash.media.SoundChannel;
	import flash.net.URLRequest;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFormat;
	
	import mx.core.UIComponent;
	import mx.core.mx_internal;
	import mx.events.MetadataEvent;
	import mx.events.ResizeEvent;
	import mx.events.SliderEvent;
	
	
	use namespace mx_internal;

	// The color of the control bar. @default 0x555555
	[Style(name="controlBarBackColor", type="uint", format="Color", inherit="no")]

	// The color of the buttons on the controlbar. @default 0xeeeeee
	[Style(name="controlBarButtonColor", type="uint", format="Color", inherit="no")]

	// The height of the control bar. @default 21
	[Style(name="controlBarHeight", type="Number", inherit="no")]

	// The name of the font used in the timer. @default "Verdana"
	[Style(name="timerFontName", type="String", inherit="no")]

	// The size of the font used in the timer. @default 9
	[Style(name="timerFontSize", type="Number", inherit="no")]

	[Event("rangeChange", type="at.ac.arcs.dme.videoannotation.client.event.RangeEvent")]

    /**
    * AudioPlayer
    * 
    * @author Stefan Pomajbik
    * @author Christian Sadilek
    * @author Manuel Gay
    * **/
	public class AudioPlayer extends AbstractPlayer {	
		/** constants **/
		private static var timerLabelDefault:String = "Loading";

		private var _url:String;
		private var _stopped:Boolean=false;
		
		private	var _snd:Sound;
		private var _req:URLRequest;
		private	var _channel:SoundChannel;
 	 	
		/** value holder **/
		private var loadProgress:Number = 0;
		private var thumbPosHooked:Boolean = true;
		private var flo:Boolean = true;
		private var keyFrameTimes:Array;


		/** display Objects **/
		private var controlBar:UIComponent;
		private var skipButton:SkipButton;
		private var fullscreenButton:FullScreenButton;
		private var timerTextField:TextField;
		private var videoArea:UIComponent;
		private var _scaleMode:String;

		/** format objects **/
		private var timerTextFormat:TextFormat;

		/** style variables **/
		private var _controlBarHeight:uint;
		private var _controlBarBackColor:uint;
		private var _controlBarButtonColor:uint;
		private var _timerFontName:String;
		private var _timerFontSize:Number;

		/** properties **/
		private var _embedControls:Boolean = true;
		
		private var _frameCounter:Number = 0;
		
		public function AudioPlayer()
		{
			super();
			this.timerTextFormat = new TextFormat();
		}
          
    	public function set url(value:String):void {
        	_url = value;
    	}    	

    	public function set eembedControls(value:Boolean):void {
    		this._embedControls = value;
    		invalidateDisplayList();
    	}

    	// Creates any child components of the component.
		override protected function createChildren():void {
        	super.createChildren();
        	// set the default values for the style variables
	        if(getStyle("controlBarButtonColor") == undefined) setStyle("controlBarButtonColor", 0xcccccc);
        	_controlBarButtonColor = getStyle("controlBarButtonColor");

        	 _controlBarHeight = getStyle("controlBarHeight");
		    if(!_controlBarHeight)  _controlBarHeight = 28;

        	if(getStyle("controlBarBackColor") == undefined) setStyle("controlBarButtonColor", 0x555555);
		    _controlBarBackColor = getStyle("controlBarBackColor");

			_timerFontName = getStyle("timerFontName");
        	if(!_timerFontName) _timerFontName = "Verdana";

        	_timerFontSize = getStyle("timerFontSize");
        	if(!_timerFontSize)  _timerFontSize = 9;

			videoArea = new UIComponent();
    	    addChild(videoArea);

        	controlBar = new UIComponent();
	        addChild(controlBar);

	        playheadSlider = new ProgressSlider();
	        controlBar.addChild(playheadSlider);

	        playheadSlider.addEventListener(SliderEvent.THUMB_PRESS, onThumbPress);
    	    playheadSlider.addEventListener(SliderEvent.THUMB_RELEASE, onThumbRelease);
        	playheadSlider.addEventListener(MouseEvent.MOUSE_DOWN, playheadSlider_onMouseDown);
        	playheadSlider.addEventListener(SliderEvent.THUMB_DRAG, onThumbDrag);

	        playPauseButton = new PlayPauseButton();
	        controlBar.addChild(playPauseButton);

	        playPauseButton.addEventListener(MouseEvent.CLICK, playPauseButton_onClick);
	        (autoPlay) ? playPressed = true : playPressed = false

	        skipButton = new SkipButton();
	        controlBar.addChild(skipButton);
	        skipButton.addEventListener(MouseEvent.CLICK, skipButton_onClick);

	        fullscreenButton = new FullScreenButton();
	        controlBar.addChild(fullscreenButton);
	        fullscreenButton.addEventListener(MouseEvent.CLICK, fullscreenButton_onClick);

	        timerTextField = new TextField();
	        controlBar.addChild(timerTextField);

	        rangeSelector = new RangeSelector();
	        rangeSelector.thumbCount = 2;
	        rangeSelector.snapInterval = 0.001;	        

	        rangeSelector.setStyle("trackSkin", RangeSliderTrackSkin);
	        rangeSelector.showDataTip=false;

	        controlBar.addChild(rangeSelector);

	        rangeSelector.addEventListener(SliderEvent.CHANGE, onRangeSelected);
	        rangeSelector.addEventListener(SliderEvent.THUMB_RELEASE, onRangeSelected);		      		      	                        
		}

		// Draws the object and/or sizes and positions its children.
		// This is an advanced method that you might override
		// when creating a subclass of UIComponent.
		override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void {
			unscaledHeight=40;                                                  	
            if (_embedControls) {
        		super.updateDisplayList(unscaledWidth,unscaledHeight - _controlBarHeight);
            } else {
            	super.updateDisplayList(unscaledWidth,unscaledHeight);
            }

			// draw the controlBar
        	controlBar.graphics.clear();
	        controlBar.graphics.beginFill(_controlBarBackColor, 0.9);
    	    controlBar.graphics.drawRect(0, 0, unscaledWidth, _controlBarHeight);

			/** size the child components */
			// controlbar
			controlBar.setActualSize(unscaledWidth, _controlBarHeight);
	        playPauseButton.setActualSize(_controlBarHeight, _controlBarHeight);
	        skipButton.setActualSize(_controlBarHeight, _controlBarHeight);
	        fullscreenButton.setActualSize(_controlBarHeight,_controlBarHeight);
	        playheadSlider.setActualSize(unscaledWidth - playPauseButton.width - skipButton.width - fullscreenButton.width  -12, 9);
			rangeSelector.setActualSize(playheadSlider.width + rangeSelector.getThumbAt(0).width,playheadSlider.height);

			// we have to resize the rangeselector to match the width of the playheadSlider
			// therefore we need to add the width of the tumb (because the rangeSelector reduces its size on both side by thumb.width/2)
			rangeSelector.width = playheadSlider.width + rangeSelector.getThumbAt(0).width;
			rangeSelector.getThumbAt(0).toolTip = "start of the fragment";
			rangeSelector.getThumbAt(1).toolTip = "end of the fragment";

	        // allign the child components
	        controlBar.x = 0;
	        controlBar.y = unscaledHeight - _controlBarHeight;

        	playPauseButton.x = 0;
        	playPauseButton.y = 0;

        	skipButton.x = playPauseButton.width;
        	skipButton.y = 0;

			playheadSlider.x = skipButton.x + skipButton.width + 2;
	        playheadSlider.y = (controlBar.height - playheadSlider.height)/2;

			rangeSelector.x = playheadSlider.x - rangeSelector.getThumbAt(0).width / 2 + playheadSlider.sliderThumbWidth / 2;
	        rangeSelector.y = playheadSlider.y + playheadSlider.height - rangeSelector.height/2;

	        timerTextField.x = playheadSlider.x + playheadSlider.sliderPos - (timerTextField.width / 2);
	        timerTextField.y = playheadSlider.y - 22;

			fullscreenButton.x = controlBar.width - fullscreenButton.width;
        	fullscreenButton.y = 0;        	        			
		}

		//Commits any changes to component properties
		override protected function commitProperties():void {
        	super.commitProperties();

        	playPauseButton.iconColor = _controlBarButtonColor;
        	skipButton.iconColor = _controlBarButtonColor;
        	fullscreenButton.iconColor = _controlBarButtonColor;

        	timerTextFormat.color = _controlBarButtonColor;
	        timerTextFormat.font = _timerFontName;
    	    timerTextFormat.size = _timerFontSize;

        	timerTextField.defaultTextFormat = timerTextFormat;
        	timerTextField.text = timerLabelDefault;
        	timerTextField.selectable = false;

        	timerTextField.autoSize = TextFieldAutoSize.LEFT;
        	timerTextField.backgroundColor = 0x888888;
        	timerTextField.background = true;
        	timerTextField.border
        	timerTextField.blendMode = BlendMode.HARDLIGHT;
		}

		private function onLoadProgress(event:ProgressEvent):void {
			loadProgress = Math.round(100 * (event.bytesLoaded / event.bytesTotal));	
			playheadSlider.progress = loadProgress;		
		}
		
		private function onLoadComplete(event:Event):void {
			playheadSlider.maximum =_snd.length / 1000;
        	rangeSelector.maximum = _snd.length / 1000;	
        	totalTime = _snd.length / 1000;
        	dispatchEvent(new MetadataEvent(MetadataEvent.METADATA_RECEIVED));        	
		}
		
		private function onProgress(event:Event):void {
			if(_stopped) return;
			// ENTER.FRAME is fired too often for the UI to stay responsive. So 
			// we ignore every second frame. It is not relevant for the playheadSlider
			// and timer.
			if((_frameCounter++%2)!=0) return;
			trace("on progress");
			
			if(_channel!=null && _snd !=null) {									
				if(_rangePlayMode) {					
					playheadSlider.value = (_channel.position + playheadSlider.fragementFromTime) / 1000;
					if(playheadSlider.value >= rangeSelector.values[1]) stop();				
				} else {
					playheadSlider.value = _channel.position / 1000;					
				} 				
				updateTimer();
			}
		
     	   	if(flo) {
            	thumbPosHooked = false;
        	}

        	if(flo && playheadTime > 0) {
            	thumbPosHooked = true;
	            stop();

    	        flo = false;

	            if(_playPressed) {
                	play();
            	}
        	}        	
		}

		private function updateTimer():void {
			var estimatedLength:int = 
   				 Math.ceil(_snd.length / (_snd.bytesLoaded / _snd.bytesTotal));
				
			timerTextField.text = formatTime(_channel.position)+" / "+formatTime(estimatedLength);
			invalidateDisplayList();
		}
		
    	private function formatTime(value:int):String {
        	var minutes:String =  Math.floor(value / 60000).toString();
        	if(minutes.length==1) minutes = "0" + minutes;

        	var seconds:String = Math.floor((value % 60000) / 1000).toString();
        	
        	var result:String;
        	if (seconds.length == 1)
            	result = minutes + ":0" + seconds;
        	else
            	result = minutes + ":" + seconds;
            	
        	return result;
    	}


        /** Mouse Events **/

        private function playPauseButton_onClick(event:MouseEvent):void  {	        
	        if(playPressed) {
		    	pause();
		        playPressed = false;
		        _rangePlayMode=false;		        
		    } else {
	            play();
	            playPressed = true;	            
	        }
	    }
	    
	    private function skipButton_onClick(event:MouseEvent):void {
	    	skipToFragment();
	    	invalidateDisplayList();
	    }

		private function fullscreenButton_onClick(event:MouseEvent):void {

            if(stage.displayState == StageDisplayState.NORMAL) {
                try {
                	 var pt:Point = new Point(this.x,this.y);
                	 pt = localToGlobal(pt);
                	 // stage.fullScreenSourceRect = new Rectangle(50, 145, 500, 375);
                	 stage.fullScreenSourceRect = new Rectangle(pt.x, pt.y, width, height);
                	 this._scaleMode = this.stage.scaleMode
                	 this.stage.scaleMode = StageScaleMode.NO_BORDER;
                     stage.displayState = StageDisplayState.FULL_SCREEN;
                     stage.addEventListener(FullScreenEvent.FULL_SCREEN, FullScreenUpdate);

                 } catch (e:SecurityError) {
                     trace ("A security error occurred while switching to full screen: " + event);
                 }
            }else {
            	this.stage.scaleMode = this._scaleMode;
                stage.displayState = StageDisplayState.NORMAL;
            }
        }

        private function playheadSlider_onMouseDown(event:MouseEvent):void {
        	thumbPosHooked = false;
        }

        private function onThumbRelease(event:SliderEvent):void {
        	thumbPosHooked = true;
        }

        private function onThumbPress(event:SliderEvent):void {
        	thumbPosHooked = false;
        }

        private function onThumbDrag(event:SliderEvent):void {
        	timerTextField.text = formatTime(event.value)+" / "+formatTime(totalTime);
        	invalidateDisplayList();
        }

        override public function skipToFragment():void {
        	removeEventListener(Event.ENTER_FRAME,onProgress);
        	_frameCounter=0;
        	_rangePlayMode = true;
    	 	playheadSlider.value=playheadSlider.fragementFromTime;   
    	 	validateNow();
    	  	 
    	    trace("pht:"+playheadTime.toString()+", timeFrom:"+playheadSlider.fragementFromTime.toString());
    	}

 	    override public function showFragment(value:Boolean):void {
 	    	playheadSlider.showFragment = value;
 	    	invalidateDisplayList();
 	    }

 	    override public function resetRangeSelector():void {
 	    	var posToSet:Number = 0;//Math.max( 0, playheadTime);
 	    	this.rangeSelector.values = [ posToSet , posToSet ];
 	    	invalidateDisplayList();
 	    }

 	    private function onResize(evt:ResizeEvent):void {
 	    	if (this.stage != null) {

 	    		if (this._scaleMode == null) {
 	    			this._scaleMode = this.stage.scaleMode;
 	    		}
 	    		this.stage.scaleMode = this._scaleMode;
 	    	}
 	    }

 	    private function FullScreenUpdate(event:FullScreenEvent):void {
		    if (this.stage == null) return;
		    if (!event.fullScreen) {
      	  		this.stage.scaleMode = this._scaleMode;
	  	  	}
 	    }
 	    
 	    
 	 	override public function play():void {	
 	 		if(!_stopped) stop();
 	 		removeEventListener(Event.ENTER_FRAME, onProgress);
 	 		_frameCounter = 0; 	 					 	 											
 	 		if(_rangePlayMode){
				_channel = _snd.play(playheadSlider.value*1000);							
			} else {
				_channel = _snd.play();
 	 		}
 	 		playPressed=true;
 	 		_stopped=false;
 	 		addEventListener(Event.ENTER_FRAME, onProgress); 	 		 	 	
 	 	}
 	 	
 	 	override public function stop():void {
 	 		removeEventListener(Event.ENTER_FRAME, onProgress);
			_frameCounter=0;
			_channel.stop();			
			_stopped=true;
			playPressed=false;			
 	 	}
 	 	
 	 	override public function pause():void {			
			_channel.stop();
			_stopped=true;			
 	 	} 	 	
 	 	
 	 	override public function set source(value:String):void {
			_url = value;			
 	 	}
 	 	
 	 	override public function load():void {
 	 		_req = new URLRequest(_url);
 	 		_snd = new Sound();		
			_snd.addEventListener(ProgressEvent.PROGRESS, onLoadProgress);
			_snd.addEventListener(Event.COMPLETE, onLoadComplete);
			addEventListener(Event.ENTER_FRAME, onProgress); 				 			
 	 		_snd.load(_req);
        	_channel = _snd.play();        			 	 		 	 	
        	_channel.stop();        	
 	 	}
 	 	
	}
}