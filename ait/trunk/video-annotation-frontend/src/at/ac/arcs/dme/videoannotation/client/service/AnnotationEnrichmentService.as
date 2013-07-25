package at.ac.arcs.dme.videoannotation.client.service
{
    import mx.rpc.events.FaultEvent;
    import mx.rpc.events.ResultEvent;
    import mx.rpc.http.HTTPService;
    
    public class AnnotationEnrichmentService
    {
        
        private const ENRICHMENT_PATH:String = "findEntities?annotation=";
        
        private var _serviceUrl:String;
        
        public function set serviceUrl(url:String):void {
            this._serviceUrl = url;
        }
        
        public function get serviceUrl():String {
            return this._serviceUrl;
        }
        
        public function AnnotationEnrichmentService() {
            
        }
        
        public function getEnrichmentData(text:String, handler:Function):void {
            var httpService:HTTPService = createHTTPService(_serviceUrl + ENRICHMENT_PATH + text);
            httpService.addEventListener(FaultEvent.FAULT, handler);  			  		
			httpService.addEventListener(ResultEvent.RESULT, handler);
			httpService.send(null);
        }
        
        private function createHTTPService(_requestUrl:String):HTTPService {
	    	var httpService:HTTPService = new HTTPService();
	    	httpService.url = _requestUrl;
	    	httpService.requestTimeout = 15;
	    	httpService.resultFormat = "e4x";
	    	httpService.useProxy = false;    	
	    	return httpService;
    	}
        

    }
}