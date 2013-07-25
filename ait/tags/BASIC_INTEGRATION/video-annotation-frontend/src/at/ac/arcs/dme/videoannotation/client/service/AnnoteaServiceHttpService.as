package at.ac.arcs.dme.videoannotation.client.service
{
	import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
	
	import flash.events.EventDispatcher;
	
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;

	/** AnnoteaServiceHttpService implements the AnnoteaService by making use the HTTPService class
     *
     * @author Stefan Pomajbik
     * @author Christian Sadilek
     **/
	public class AnnoteaServiceHttpService extends EventDispatcher implements AnnoteaService
	{
		private var _serviceUrl:String;
		private const HTTP_GET:String="GET";
		private const HTTP_POST:String="POST";
		private const HTTP_PUT:String="PUT";
		private const HTTP_DELETE:String="DELETE";

		private const ANNOTEA_PATH:String="Annotation/";
		private const ANNOTEA_ANNOTATES:String="Annotation?w3c_annotates=";
		private const ANNOTEA_BODY_PATH:String="body/";

		public function set serviceUrl(serviceUrl:String):void {
			this._serviceUrl=serviceUrl;
		}

		public function get serviceUrl():String {
			return this._serviceUrl;
		}

		private function createHTTPService(_requestUrl:String):HTTPService {
	    	var httpService:HTTPService = new HTTPService();
	    	httpService.url = _requestUrl;
	    	httpService.requestTimeout = 15;
	    	httpService.resultFormat = "e4x";	    	
	    	return httpService;
    	}

    	// responsible for sending delete requests which is only possible thru custom socket connection
    	//
    	// this function makes use of an external library which was implemented by gabrielh
    	// see http://code.google.com/p/as3httpclientlib/
    	//
    	private function sendHTTPDelete(annoteaBody:XML,servicePath:String,handler:Function,asPost:Boolean):void {
      		var httpService:HTTPService = createHTTPService(serviceUrl+servicePath+"?_method=DELETE");
      		httpService.method=HTTP_POST;
      		httpService.contentType=HTTPService.CONTENT_TYPE_XML;
			httpService.addEventListener(FaultEvent.FAULT,handler);  			  		
			httpService.addEventListener(ResultEvent.RESULT,handler);
			trace("sendDelete with URI:"+httpService.url+" ,asPost:"+asPost);
	  		httpService.send(null);	  		
      		//client.request(uri, request);
	    }

	    // responsible for sending put requests which is only possible thru custom socket connection
    	//
    	// this function makes use of an external library which was implemented by gabrielh
    	// see http://code.google.com/p/as3httpclientlib/
    	//
    	private function sendHTTPPut(annoteaBody:XML,servicePath:String,handler:Function,asPost:Boolean):void {
      		var httpService:HTTPService = createHTTPService(serviceUrl+servicePath+"?_method=PUT");
            httpService.method = HTTP_POST;
            httpService.contentType = HTTPService.CONTENT_TYPE_XML;
            httpService.addEventListener(FaultEvent.FAULT, handler);
            httpService.addEventListener(ResultEvent.RESULT,handler);
            trace("Sent ["+httpService.method.toString() +"] request to:"+httpService.url.toString()+"\nbody {"+annoteaBody+"}\n");
            httpService.send(annoteaBody);
	    }

    	// responsible for post requests / service communication
        private function sendHTTPPost(annoteaBody:XML,servicePath:String,handler:Function):void {
			var httpService:HTTPService = createHTTPService(serviceUrl+servicePath);
            httpService.method = HTTP_POST;
            httpService.contentType = HTTPService.CONTENT_TYPE_XML;
            httpService.addEventListener(FaultEvent.FAULT, handler);
            httpService.addEventListener(ResultEvent.RESULT,handler);
            trace("Sent ["+httpService.method.toString() +"] request to:"+httpService.url.toString()+"\nbody {"+annoteaBody+"}\n");
            httpService.send(annoteaBody);
         }

         // responsible for post requests / service communication
         // is needed for the listAnnotations call as long as CONTENT_TYPE_XML is not supported
        private function sendHTTPGet(annoteaBody:XML,servicePath:String,handler:Function):void {
        	// we append a random number to avoid caching in IE		
			var httpService:HTTPService = createHTTPService(serviceUrl+servicePath+"&r="+Math.random());        
            httpService.method = HTTP_GET;
            httpService.addEventListener(FaultEvent.FAULT, handler);
            httpService.addEventListener(ResultEvent.RESULT,handler);
            trace("Sent ["+httpService.method.toString() +"] request to:"+httpService.url.toString()+"\nbody {"+annoteaBody+"}\n");           
            httpService.send(annoteaBody);
         }

        // creates a new Annotation by calling the service
		//
		// the result can be retrieved by passing a handler which receives the ResultEvent.RESULT
		public function createAnnotation(_videoAnnotation:VideoAnnotation,handler:Function):void {
    		trace("creatAnnotation called with: "+_videoAnnotation);
    		sendHTTPPost(RdfXmlAnnotationBuilder.annoToRdfXml(_videoAnnotation),ANNOTEA_PATH,handler);
		}

        public function updateAnnotation(_videoAnnotation:VideoAnnotation,handler:Function):void {
        	trace("updateAnnotation called with: "+_videoAnnotation);
         	sendHTTPPut(RdfXmlAnnotationBuilder.annoToRdfXml(_videoAnnotation),ANNOTEA_PATH+makeShortAnnotationId(_videoAnnotation.id),handler,true);
        }

        public function deleteAnnotation(_videoAnnotationId:String,handler:Function):void {
        	trace("deleteAnnotation called with: "+_videoAnnotationId);
         	sendHTTPDelete(null,ANNOTEA_PATH+makeShortAnnotationId(_videoAnnotationId),handler,true);
        }

        public function findAnnotationById(_videoAnnotationId:String,handler:Function):void {
        	trace("findAnnotationById called with: "+_videoAnnotationId);
         	sendHTTPGet(null,ANNOTEA_PATH+makeShortAnnotationId(_videoAnnotationId),handler);
        }

		public function findAnnotationBodyById(_videoAnnotationId:String,handler:Function):void {
        	trace("findAnnotationBodyById called with: "+_videoAnnotationId);
         	sendHTTPGet(null,ANNOTEA_PATH+ANNOTEA_BODY_PATH+makeShortAnnotationId(_videoAnnotationId),handler);
        }

		public function listAnnotations(objectId:String,rootID:String,handler:Function):void {
			trace("listAnnotations called with objectId["+objectId+"] rootID["+rootID+"]");
         	sendHTTPGet(null,ANNOTEA_ANNOTATES+objectId,handler);
		}
		
		public function makeShortAnnotationId(annotationID:String):String {
			var parts:Array=annotationID.split("/");
			if(parts.length>0) return parts[parts.length-1];
			return annotationID;
		}
	}
}