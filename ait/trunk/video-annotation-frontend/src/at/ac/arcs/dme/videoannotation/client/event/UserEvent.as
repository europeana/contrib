package at.ac.arcs.dme.videoannotation.client.event
{
    import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
    
    import flash.events.Event;
    
    /**
     * A user event represents an event triggered by the user (clicking a button, changing a slider position, ...)
     * 
     * @author Manuel Gay
     */
    public class UserEvent extends Event
    {

   		public static const CLICK_NEW_ANNOTATION:String = "clickNewAnnotation";
   		public static const CLICK_EDIT_ANNOTATION:String = "clickEditAnnotation";
   		public static const CLICK_DELETE_ANNOTATION:String = "clickDeleteAnnotation";
   		public static const CLICK_REPLY_ANNOTATION:String = "clickReplyAnnotation";
   		public static const CLICK_PLAY_FRAGMENT:String = "clickPlayFragment";
   		
   		// user selects an annotation in the annotation tree
        public static const SELECT_ANNOTATION_TREE:String = "selectAnnotation";

        // user cancels the form
        public static const CLICK_CANCEL_ANNOTATION:String ="clickCancel";
        
        // user selects an annotation in the fragment bar
        public static const SELECT_ANNOTATION_FRAGMENTBAR:String = "selectAnnotationFragmentbar";
   		
   		// user select tools in the annotation form
   		public static const SELECT_NO_SHAPE_TOOL:String = "selectNoAnnotation";
		public static const SELECT_RECTANGLE_SHAPE_TOOL:String = "selectRectangleAnnotation";
		public static const SELECT_ELLIPSE_SHAPE_TOOL:String = "selectEllipseAnnotation";
        public static const SELECT_ANNOTATION_COLOR:String = "selectAnnotationColor";
        
        private var _vAnnotation:VideoAnnotation;
        
		public function UserEvent(type:String, annotation:VideoAnnotation=null) {
			super(type,true);
			this._vAnnotation = annotation;
		}

		public function get annotation():VideoAnnotation {
			return this._vAnnotation;
		}

		// Override the inherited clone() method.
        override public function clone():Event {
            return new AnnotationEvent(type, _vAnnotation);
        }

    }
}