package at.ac.arcs.dme.videoannotation.client.gui.video
{
	import at.ac.arcs.dme.videoannotation.client.event.AnnotationEvent;
	import at.ac.arcs.dme.videoannotation.client.event.RangeEvent;
	import at.ac.arcs.dme.videoannotation.client.event.UserEvent;
	import at.ac.arcs.dme.videoannotation.client.gui.button.PlayPauseButton;
	
	import mx.controls.VideoDisplay;
	import mx.events.SliderEvent;

    /**
     * An AbstractPlayer
     * 
     * @author Christian Sadilek
     * @author Manuel Gay
     **/
	public class AbstractPlayer extends VideoDisplay
	{	
	    
	    /** display objects**/
		[Bindable] public var playheadSlider:ProgressSlider;
		protected var playPauseButton:PlayPauseButton;
		protected var rangeSelector:RangeSelector;

        /** properties **/	
		protected var _rangePlayMode:Boolean = false;
		protected var _playPressed:Boolean;

		protected function set playPressed(value:Boolean):void {
        	_playPressed = value;
            (value) ? playPauseButton.state = "pause" : playPauseButton.state = "play"
    	}

    	protected function get playPressed():Boolean {
        	return _playPressed;
    	}
		
    	public function set rangePlayMode(value:Boolean):void {
        	_rangePlayMode = value;
    	}
    	
    	public function get rangePlayMode():Boolean {
    	    return _rangePlayMode;
    	}
    		 
		public function showFragment(value:Boolean):void {}
		
 	    public function resetRangeSelector():void {}	
 	    
 	    public function skipToFragment():void {}

        public function togglePlayPause():void {
	        if(playPressed) {
		    	pause();
		        playPressed = false;
		    } else {
	            super.play();
	            playPressed = true;
	            _rangePlayMode=false;
	        }
        }
     	    
 	    public function handleDisplayFrame(event:AnnotationEvent):void {
 	        // this is a bit of a hack but there doesn't seem any built-in functionality to display one single frame
		    this.skipToFragment();
		    //trace('Setting playheadtime to ' + event.annotation.videoFragment.timeFrom);
			this.play();
			this.togglePlayPause();
 	    }
 	    
 	    public function handlePlayFragment(event:UserEvent):void {
 	        this.rangePlayMode=true;
			this.skipToFragment();			
			this.play();	
 	    }
 	    
        public function handleUpdateRangeSelector(event:Event):void {
            
            var fromT:Number;
            var toT:Number;
            
            if(event is UserEvent) {
                fromT = (event as UserEvent).annotation.videoFragment.timeFrom;
                toT  = (event as UserEvent).annotation.videoFragment.timeTo;
            } else if(event is RangeEvent) {
                fromT = (event as RangeEvent).range[0];
                toT = (event as RangeEvent).range[1];
            }
            
			if (isNaN(fromT)) fromT = 0;
			if (isNaN(toT)) toT = fromT;
			setRangeSelector(fromT,toT);
    	}

    	protected function onRangeSelected(evt:SliderEvent):void {
 	    	trace(this.rangeSelector.values);
    		var value:Array = rangeSelector.values;
    		var e:RangeEvent = new RangeEvent(RangeEvent.CHANGE_PLAYER_RANGE,value);
        	systemManager.dispatchEvent(e);
 	    }


    	
    	private function setRangeSelector(fromTime:Number, toTime:Number):void {
        	playheadSlider.markFragment(fromTime,toTime);
        	rangeSelector.values = [ fromTime, toTime ];
        }

	}
}