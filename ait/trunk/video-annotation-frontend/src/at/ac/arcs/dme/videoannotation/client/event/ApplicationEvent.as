package at.ac.arcs.dme.videoannotation.client.event
{
	import flash.events.Event;
	
	/**
	 * The ApplicationEvent class represents the events happening globally on the application level
	 * 
	 * @author Manuel Gay
	 */
	public class ApplicationEvent extends Event
	{
			public static const LANGUAGE_CHANGE:String = "languageChange";
			
			public static const START:String = "playStart";
			public static const STOP:String = "playStop";
			
			public static const LOAD_DATA:String = "loadData";
			
			public static const DISPLAY_FORM:String ="displayForm";
			
			public static const LIST_ANNOTATION:String = "listAnnotation";
			
			public function ApplicationEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false) {
			    super(type, bubbles, cancelable);
		    }

            private var _presentationData:Array;
            		    
		    public function set presentationData(presentationData:Array):void {
		        this._presentationData = presentationData;
		    }
		    
		    public function get presentationData():Array {
		        return this._presentationData;
		    }
	}
}