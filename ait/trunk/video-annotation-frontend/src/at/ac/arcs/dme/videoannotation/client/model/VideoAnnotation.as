package at.ac.arcs.dme.videoannotation.client.model
{
	import com.ericfeminella.collections.HashMap;
	
	import mx.formatters.DateFormatter;

	Date.prototype.getFormatedString = function (format:String):String {
			var dFormatter:DateFormatter = new DateFormatter();
			dFormatter.formatString = format;
			return dFormatter.format(this);
		};

	/** VideoAnnotation represents a video annotation.
     *
     * @author Stefan Pomajbik
     * @author Christian Sadilek
     * @author Manuel Gay 
     **/
	[Bindable] public class VideoAnnotation 
	{				
		
		public static const NEW_ANNOTATION_ID:String = "newAnnotationId";
		
		// id of this annotation
		private var _Id:String=null;
		// if this annotation is is part of a discussion thread
		// then replyToId references another resource.
		// (Except the first one :-)
		private var _ReplyToId:String=null;
		// Every resource that is part of a discussion thread
        // should have a root property to identify the first resource
        // in the thread.  Every resource in the thread will have the same
        //resource as its root.
		private var _RootId:String=null;
		// url of the annotated resource
		private var _AboutId:String=null;
		// tel+ context id
		private var _TelPlusId:String=null;

		private var _CreationDate:Date=null;
		private var _UpdateDate:Date=null;

		private var _Creator:String=null;
		public var _Title:String=null;
		public var _Text:String=null;

		private var _Scope:Scope = Scope.PUBLIC;
		
		// holds the annotations which are in reply to this one
		private var replies:Array=null;

		// identifies the fragment of the video this annotation is about
		private var _videofragment:VideoFragment=null;
		
		private var _resources:HashMap=null;

		public function VideoAnnotation(videofragment:VideoFragment) {
			this._CreationDate= new Date();
   	    	this._UpdateDate=new Date();
			_videofragment = videofragment;
			_Id = NEW_ANNOTATION_ID;
			this._resources=new HashMap();
		}

		public function set creationDate( _date:Date):void {
			_CreationDate=_date;
		}

		public function get creationDate():Date {
			return _CreationDate;
		}

		public function set updateDate( _date:Date):void {
			_UpdateDate=_date;
		}

		public function get updateDate():Date {
			return _UpdateDate;
		}

		public function set creator( _creator:String):void {
			_Creator =_creator;
		}

		public function get creator():String {
			return _Creator;
		}

		public function set title( _title:String):void {
			_Title=_title;
		}

		public function get title():String {
			return _Title;
		}

		public function set text( _text:String):void {
			_Text=_text;
		}

		public function get text():String {
			return _Text;
		}

		public function get scope():Scope {
			return _Scope;
		}
		
		public function set scope(_scope:Scope):void {
			_Scope = _scope
		}
		
		public function set id( _id:String):void {
			_Id = _id;
		}

		public function get id():String {
			return _Id;
		}

		public function set aboutId( _id:String):void {
			_AboutId = _id;
		}

		public function get aboutId():String {
			return _AboutId;
		}

		public function set replyToId( _id:String):void {
			_ReplyToId = _id;
		}
		
		
		public function get replyToId():String {
			return _ReplyToId;
		}

		public function set children(_replies:Array):void {
			replies = _replies;
		}
		
		public function get children():Array {
			return replies;
		}
		
		public function set rootId( _id:String):void {
			_RootId = _id;
		}

		public function get rootId():String {
			return _RootId;
		}

		public function set telPlusId( _id:String):void {
			_TelPlusId = _id;
		}

		public function get telPlusId():String {
			return _TelPlusId;
		}

		public function set videoFragment(videoFragment:VideoFragment):void {
			this._videofragment=videoFragment;
		}

		public function get videoFragment():VideoFragment {
			return this._videofragment;
		}
		
		public function addResource(resource:AnnotationResource):void {
		    this._resources.put(resource.name, resource);
		}
		
		public function get resources():HashMap {
		    return this._resources;
		}
		
		public function set resources(r:HashMap):void {
		    this._resources = r;
		}



		public function shallowCopy():VideoAnnotation {
			
			var fragment:VideoFragment; 
			
			if(_videofragment!=null) {
				fragment = new VideoFragment(_videofragment.timeFrom, _videofragment.timeTo);
				fragment.shape = _videofragment.shape;
			}
			
			var copy:VideoAnnotation = new VideoAnnotation(fragment);
			copy.aboutId = _AboutId;
			copy.children = replies;
			copy.creationDate = _CreationDate;
			copy.creator =  _Creator;
			copy.id = _Id;
			copy.replyToId = _ReplyToId;
			copy.rootId = _RootId;
			copy.scope = _Scope;
			copy.telPlusId = _TelPlusId;
			copy.text = _Text;
			copy.title = _Title;
			copy.updateDate = _UpdateDate;
			copy.resources = _resources;
			
			return copy;
			
		}
		
		public function toString():String {
			return "id: "+_Id + " \n" +
				"aboutId: "+aboutId + " \n" +
				"replyToId: "+replyToId +" \n" +
				"rootId: "+rootId +" \n" +
				"telPlusId: "+telPlusId +" \n" +
				"creator: "+creator +" \n" +
				"creationDate: "+creationDate +" \n" +
				"updateDate: "+updateDate +" \n" +
				"title: "+title +" \n" +
				"text: "+text +" \n" +
				"videofragment: [\n"+ videoFragment +"]";
		}

	}
}