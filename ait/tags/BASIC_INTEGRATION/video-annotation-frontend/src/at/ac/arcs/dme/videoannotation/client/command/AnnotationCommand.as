package at.ac.arcs.dme.videoannotation.client.command
{
    import at.ac.arcs.dme.videoannotation.client.controller.AnnotationController;
    import at.ac.arcs.dme.videoannotation.client.event.AnnotationEvent;
    import at.ac.arcs.dme.videoannotation.client.event.ApplicationEvent;
    import at.ac.arcs.dme.videoannotation.client.event.UserEvent;
    import at.ac.arcs.dme.videoannotation.client.gui.annotation.AnnotationForm;
    import at.ac.arcs.dme.videoannotation.client.gui.debug.DebugBox;
    import at.ac.arcs.dme.videoannotation.client.model.AnnotationResource;
    import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
    import at.ac.arcs.dme.videoannotation.client.model.VideoFragment;
    import at.ac.arcs.dme.videoannotation.client.service.AnnotationEnrichmentService;
    import at.ac.arcs.dme.videoannotation.client.service.AnnoteaService;
    import at.ac.arcs.dme.videoannotation.client.service.AnnoteaServiceHttpService;
    import at.ac.arcs.dme.videoannotation.client.service.RdfXmlAnnotationBuilder;
    import at.ac.arcs.dme.videoannotation.client.util.HTTPParameters;
    
    import flash.events.Event;
    import flash.external.ExternalInterface;
    
    import mx.containers.Panel;
    import mx.controls.Alert;
    import mx.core.Application;
    import mx.managers.PopUpManager;
    import mx.resources.ResourceManager;
    import mx.rpc.events.FaultEvent;
    import mx.rpc.events.ResultEvent;
    
    /**
     * This class takes care of the business layer, i.e. mostly of service invocation
     * 
     * @author Manuel Gay
     **/
    public class AnnotationCommand
    {
        
        private var application:Application;
        private var debugBox:DebugBox;
        private var urlParams:HTTPParameters;
        
        private var rdf:Namespace = new Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	    private var annoteaService:AnnoteaService = new AnnoteaServiceHttpService();
	    private var annotationEnrichmentService:AnnotationEnrichmentService = new AnnotationEnrichmentService();
	    
	    private var _currentAnnotation:VideoAnnotation;
        
        public function AnnotationCommand(application:Application, debugBox:DebugBox, urlParams:HTTPParameters):void {
            this.application = application;
            this.debugBox = debugBox;
            this.urlParams = urlParams;
            annoteaService.serviceUrl = ResourceManager.getInstance().getString("videoannotation","annotation.middleware.url");
            annotationEnrichmentService.serviceUrl = ResourceManager.getInstance().getString("videoannotation","annotation.enrichment.url");
        }
        
        
                    
        public function on_editAnnotation(event:UserEvent):void {
            trace('command: edit annotation');
            displayAnnotationForm();
            
            // make a copy of the annotation
            var annotationCopy:VideoAnnotation = event.annotation.shallowCopy();
            
            // we pass the copy to the form
            application.systemManager.dispatchEvent(new AnnotationEvent(AnnotationEvent.LOAD_ANNOTATION, annotationCopy));
            
            // pass the copy to the canvas
            application.systemManager.dispatchEvent(new AnnotationEvent(AnnotationEvent.SHAPE_EDIT, annotationCopy));
            
        }
        
        public function on_replyAnnotation(event:UserEvent):void {
            
            displayAnnotationForm();
            
            // initialize the reply
	        var fragment:VideoFragment = new VideoFragment(event.annotation.videoFragment.timeFrom, event.annotation.videoFragment.timeTo);
    		var reply:VideoAnnotation = new VideoAnnotation(fragment);
    			
        	reply.rootId = (event.annotation.rootId!=null) ? event.annotation.rootId : event.annotation.id;
      		reply.replyToId = event.annotation.id;
      		reply._Title = "Re:" + event.annotation.title;
      		
            // we pass the annotation to the form
            application.systemManager.dispatchEvent(new AnnotationEvent(AnnotationEvent.LOAD_ANNOTATION, reply));
      		
      		// pass it to the canvas
	        application.systemManager.dispatchEvent(new AnnotationEvent(AnnotationEvent.SHAPE_EDIT, reply));
      		
        }
        
        public function on_deleteAnnotation(event:UserEvent):void {
		    var vAnno:VideoAnnotation = event.annotation;
    	    annoteaService.deleteAnnotation(vAnno.id,callbackDeleteAnnotation);
	    }
	    
	    

        
        
        public function handleNewAnnotation(event:AnnotationEvent):void {

            // display the form
            displayAnnotationForm();
            
   	        // we create the annotation
            var fragment:VideoFragment = new VideoFragment(event.timeFrom, event.timeTo);
            var annotation:VideoAnnotation = new VideoAnnotation(fragment);

            // we pass the annotation to the form
            application.systemManager.dispatchEvent(new AnnotationEvent(AnnotationEvent.LOAD_ANNOTATION, annotation));
            
            // we pass the annotation to the annotation canvas
	        application.systemManager.dispatchEvent(new AnnotationEvent(AnnotationEvent.SHAPE_NEW, annotation));

        }
   	
       	public function handleSaveAnnotation(event:AnnotationEvent):void {
			var vAnno:VideoAnnotation = event.annotation;
			saveAnnotation(vAnno);
		}
		
		public function handleListAnnotation(event:ApplicationEvent):void {
		    annoteaService.listAnnotations(urlParams.objectURL,null,callbackListAnnotations);
		}
		
		public function handleGetAnnotationEnrichmentData(event:AnnotationEvent):void {
		    
		    // store the annotation in a variable so we know which annotation to update when the callback comes in
		    // this is somewhat of a hack but flex does not allow passing parameters for handlers in action listeners
		    this._currentAnnotation = event.annotation;

		    annotationEnrichmentService.getEnrichmentData(event.annotationDescription, callbackGetAnnotationEnrichmentData);
		}








       	private function saveAnnotation(vAnnotation:VideoAnnotation):void {
       	    
    	 	if (vAnnotation.id == VideoAnnotation.NEW_ANNOTATION_ID) {
    			vAnnotation.aboutId = urlParams.objectURL;
    			vAnnotation.creator = urlParams.user;
    			vAnnotation.telPlusId = urlParams.telplusId;
    		    annoteaService.createAnnotation(vAnnotation,callbackCreateAnnotation);
    		} else {
    			vAnnotation.updateDate = new Date();    			
    			annoteaService.updateAnnotation(vAnnotation,callbackUpdateAnnotation);
    		}
    	}
    	
    	
    	
    	private function callbackCreateAnnotation(event:Event):void {
    		if (event.type == ResultEvent.RESULT) {
             	var annoteaResultData:XMLList = new XMLList();
             	annoteaResultData = (event as ResultEvent).result.rdf::Description;
    	
             	debugBox.log("\ncreate annotation submitted successfully:"+
     	         	(event as ResultEvent).result.toString());         	   	    		
      		} else { 
      			// ugly workaround: FLEX in IE throws a FaultEvent when HTTP Status 201
      			// is returned. We ignore the error in case of IE. 
      			// see e.g. http://stackoverflow.com/questions/354936/flex-2032-stream-error-in-ie-only
      			if(!ExternalInterface.call(
      				"function(){return navigator.appName.indexOf(\"Microsoft\")!=-1}")){      			      		
      					Alert.show(ResourceManager.getInstance().getString('videoannotationIntl', 'ERROR_CONFLICT'));      
      				}
      		}
      		annoteaService.listAnnotations(urlParams.objectURL,null,callbackListAnnotations);
    	}
    
    	private function callbackUpdateAnnotation(event:Event):void {
    		if (event.type == ResultEvent.RESULT) {
    		    trace("\nupdate annotation submitted successfully:"
         			+(event as ResultEvent).result.toString());
         		debugBox.log("\nupdate annotation submitted successfully:"
         			+(event as ResultEvent).result.toString());
      		} else {
      			Alert.show(ResourceManager.getInstance().getString('videoannotationIntl', 'ERROR_CONFLICT'));
      			debugBox.log("\nupdate annotation:"+ event.toString());
      		}
         	annoteaService.listAnnotations(urlParams.objectURL,null,callbackListAnnotations);
    	}
    
    	private function callbackListAnnotations(event:Event):void {
    		//trace(event.toString());
    		if (event.type == ResultEvent.RESULT) {
    		    var presentationData:Array = new Array();   		
         		var annoteaResultData:XMLList = new XMLList();
         		debugBox.rawData((event as ResultEvent).result.toString());
         		if((event as ResultEvent).result!=null && (event as ResultEvent).result!="") {
         			annoteaResultData = (event as ResultEvent).result.rdf::Description;
         			presentationData=RdfXmlAnnotationBuilder.fromRdfXml(annoteaResultData);
         			presentationData.sortOn("updateDate", Array.NUMERIC | Array.CASEINSENSITIVE | Array.DESCENDING);
         		}
         		
         		var e:ApplicationEvent = new ApplicationEvent(ApplicationEvent.LOAD_DATA);
         		e.presentationData = presentationData;
         		application.systemManager.dispatchEvent(e);
         		
         		debugBox.log("\n list annotations successful:"+event.toString());								
     		} else {
     			debugBox.log("\nlist annotation:"+ (event as FaultEvent).toString());
     			Alert.show((event as FaultEvent).toString());
     		}
     	}
     	 
     	private function callbackDeleteAnnotation(event:Event):void {
     	   if (event.type == ResultEvent.RESULT) {
            	//this._annotation = null;
         		debugBox.log("\ndelete annotation successful:"+ (event as ResultEvent).toString());
            } else {
            	debugBox.log("\ndelete annotation:"+ (event as FaultEvent).toString());
         		Alert.show(ResourceManager.getInstance().getString('videoannotationIntl', 'ERROR_CONFLICT'));
            }
         	annoteaService.listAnnotations(urlParams.objectURL,null,callbackListAnnotations);
       	}
       	
       	private function callbackGetAnnotationEnrichmentData(event:Event):void {
       	    if (event.type == ResultEvent.RESULT) {
       	        
       	        // update the annotation
       	        var a:VideoAnnotation = _currentAnnotation;
       	        
       	        /*
       	        var entityList:XMLList = (event as ResultEvent).result as XMLList;
       	        for each(var entity:XML in entityList) {
       	            var link:String = entity.refrence.link;
       	            var abstract:String = entity.refrence.linkAbstract;
       	            var name:String = entity.entityName;
       	            
       	            var r:AnnotationResource = new AnnotationResource(link, null, abstract);
       	            a.addResource(r);
       	        }
       	        */
       	        var e:AnnotationEvent = new AnnotationEvent(AnnotationEvent.LOAD_ANNOTATION_ENRICHMENT_DATA, a);
       	        e.annotationEnrichmentData = (event as ResultEvent).result as XML;
       	        application.systemManager.dispatchEvent(e);
       	        
       	        _currentAnnotation = null;
       	        
       	    } else if(event.type == FaultEvent.FAULT) {
       	        trace("An error occured while retrieving the enrichment data: " + (event as FaultEvent).message);
       	    }
       	}
    
    /*
     	private function callbackFindAnnotationById(event:Event):void {
     		if (event.type == ResultEvent.RESULT) {
             	var annoteaResultData:XMLList = new XMLList();
             	annoteaResultData = (event as ResultEvent).result.rdf::Description;
             	this._annotation = RdfXmlAnnotationBuilder.fromRdfXml(annoteaResultData)[0];
    
             	debugBox.log("\nfind annotation by id successful:"+annoteaResultData.toString());
             	selectFragmentBar(_annotation);
        	} else {
     			debugBox.log("\nfind annotation by id fault:"+(event as FaultEvent).toString());
     		}
     	}
        
        
        */
        
        private var annotationForm:AnnotationForm = null;
            
        private function displayAnnotationForm():void {
            if(annotationForm!=null) PopUpManager.removePopUp(annotationForm);
	    	var form:Panel = PopUpManager.createPopUp(application, AnnotationForm, false) as Panel;
	    	annotationForm = AnnotationForm(form);
        	AnnotationController.getInstance().initAnnotationForm(annotationForm);
        }
            
    }
}