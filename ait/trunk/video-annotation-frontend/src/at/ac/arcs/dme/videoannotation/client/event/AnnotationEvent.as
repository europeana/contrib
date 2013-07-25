package at.ac.arcs.dme.videoannotation.client.event
{

	import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
	
	import flash.events.Event;

	/** The AnnotationEvent class represents the event object passed to the event listener for changes of an annotation 
     *
     * @author Stefan Pomajbik
     * @author Christian Sadilek
     * @author Manuel Gay
     **/
	public class AnnotationEvent extends Event
	{
        public static const NEW:String = "annotationNew";
		public static const EDIT:String = "annotationEdit";			
		public static const SAVE:String = "annotationSave";
		public static const DELETE:String = "annotationDelete";
		public static const REPLY:String = "annotationReply";
		
		public static const SELECTED:String = "annotationSelected";
		public static const PLAY_FRAGMENT:String = "annotationPlayFragment";
		public static const DISPLAY_FRAME:String= "annotationDisplayFrame";
		
		// special event used to transfer an annotation to components, e.g. the form
		public static const LOAD_ANNOTATION:String = "loadAnnotation";
		
		// events related to the annotation canvas
		public static const SHAPE_READONLY:String = "readOnly";
		public static const SHAPE_NEW:String = "shapeNew";
		public static const SHAPE_EDIT:String = "shapeEdit";
	
	    // events related to the annotation enrichment data	
    	public static const GET_ANNOTATION_ENRICHEMENT_DATA:String = "getAnnotationEnrichementData";
		public static const LOAD_ANNOTATION_ENRICHMENT_DATA:String = "loadAnnotationEnrichmentData";

		private var _vAnnotation:VideoAnnotation;
		private var _timeFrom:Number;
        private var _timeTo:Number;
        private var _annotationDescription:String;
        private var _annotationEnrichmentData:XML;

        
        public function get timeFrom():Number {
            return this._timeFrom;
        }
        
        public function get timeTo():Number {
            return this._timeTo;
        }
        
        public function set timeFrom(timeFrom:Number):void {
            this._timeFrom = timeFrom;
        }

        public function set timeTo(timeTo:Number):void {
            this._timeTo = timeTo;
        }

		public function get annotation():VideoAnnotation {
			return this._vAnnotation;
		}
		
		public function set annotation(a:VideoAnnotation):void {
		    this._vAnnotation = a;
		}
		
		public function set annotationDescription(annotationDescription:String):void {
	        this._annotationDescription = annotationDescription;
	    }
	    
	    public function get annotationDescription():String {
	        return this._annotationDescription;
	    }
	    
	    public function set annotationEnrichmentData(data:XML):void {
	        this._annotationEnrichmentData = data;
	    }
	    
	    public function get annotationEnrichmentData():XML {
	        return this._annotationEnrichmentData;
	    }
	    
  		public function AnnotationEvent(type:String, annotation:VideoAnnotation=null, timeFrom:Number=0, timeTo:Number=0)	{
			super(type,true);
			this._vAnnotation = annotation;
			this.timeFrom = timeFrom;
			this.timeTo = timeTo;		
		}


		// Override the inherited clone() method.
        override public function clone():Event {
            return new AnnotationEvent(type, _vAnnotation);
        }
	}
}