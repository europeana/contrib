package at.ac.arcs.dme.videoannotation.client.model
{
    import flash.display.Shape;
    

	/** VideoFragment represents a video fragment.
     *
     * @author Stefan Pomajbik
     * @author Christian Sadilek
     * @author Manuel Gay
     **/
	[Bindable] public class VideoFragment
	{

		// the timespan of this annotation
		// timeFrom in milliseconds
		private var _timeFrom:Number=0;
		// timeFrom in milliseconds
		private var _timeTo:Number=0;
		// used to mark an area of the movie
		private var _shape:AnnotationShape=null;
		
		public function VideoFragment(timeFrom:Number, timeTo:Number)
		{
			_timeFrom=timeFrom;
			_timeTo=timeTo;
			_shape = new AnnotationShape();
		}

		public function set timeFrom(timeFrom:Number):void {
			this._timeFrom=timeFrom;
		}

		public function get timeFrom():Number {
			return this._timeFrom;
		}
		
    	public function set timeTo(timeTo:Number):void {
			this._timeTo=timeTo;
		}

		public function get timeTo():Number {
			return this._timeTo;
		}


		public function get formattedTimeFrom():String {
			return formatTime(_timeFrom);
		}

		public function set formattedTimeFrom(formattedTime:String):void {
			if(formattedTime.length < 9) return;
			this._timeFrom= parseTime(formattedTime);
		}
		
		public function get formattedTimeTo():String {
			return formatTime(_timeTo);
		}

		public function set formattedTimeTo(formattedTime:String):void {
			if(formattedTime.length < 9) return;
			this._timeTo= parseTime(formattedTime);
		}

		public function set shape(shape:AnnotationShape):void {
			this._shape=shape;
		}

		public function get shape():AnnotationShape {
			return this._shape;
		}

		public function toString():String {
			return "timeFrom: "+ timeFrom + ", " +
				"timeTo: "+ timeTo + ", " +
				"shape: "+shape +" \n";
		}
		
		// there seems to be no string formatting utilities in actionscript :(		  
		private function formatTime(time:Number):String {
        	var formattedTime:String;
        	var minutes:String =  Math.floor(time / 60).toString();        	
        	var seconds:String = (time % 60).toString();
        	var milliSeconds:String;
        	
        	var msSeparatorPos:Number = seconds.indexOf(".");
        	if(msSeparatorPos>-1) {        		
        		milliSeconds = seconds.substr(msSeparatorPos+1);
        		seconds = seconds.substr(0,msSeparatorPos); 
        	} else {
        		milliSeconds = "000";
        	}
        	
        	if(minutes.length==1) minutes = "0" + minutes;
        	if(seconds.length==1) seconds = "0" + seconds;
        	if(milliSeconds.length==2) milliSeconds += "0";
        	if(milliSeconds.length==1) milliSeconds += "00";        	
    
    		return minutes + ":" + seconds + "." + milliSeconds.substr(0,3);
		}
		
		private function parseTime(formattedTime:String):Number {								 
			var minutes:Number = new Number(formattedTime.substr(0,2));
			var seconds:Number = new Number(formattedTime.substr(3));
			seconds += 60 * minutes;
			return seconds;						
		}
	}
}