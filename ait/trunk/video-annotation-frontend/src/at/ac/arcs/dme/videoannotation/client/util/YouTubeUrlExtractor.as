package at.ac.arcs.dme.videoannotation.client.util
{
	import flash.events.EventDispatcher;
	
	import mx.rpc.events.ResultEvent;
	import mx.rpc.http.HTTPService;

	/**
	 * YouTubeUrlExtractor retieves a FLV url from a youtube target 
	 * 
	 * @author Christian Sadilek
	 **/
	[Bindable] public class YouTubeUrlExtractor extends EventDispatcher
	{
		private const GET_VIDEO_URL:String = "http://dme.arcs.ac.at/telplus/ytue";

		// url from youtube page
		private var _srcURL:String;
		// url to flv file
		private var _flvURL:String;

		private var _videoId:String;
		
		public function get flvURL():String {
			return this._flvURL;
		}

		public function set flvURL(value:String):void {
			this._flvURL = value;
		}

		public function YouTubeUrlExtractor()
		{		
		}

		public function convertToFLVUrl(youtubeURL:String):void {
			this._srcURL = youtubeURL;
			this.flvURL = null;
			youtubeURL = youtubeURL.replace("?","/");
			youtubeURL = youtubeURL.replace("=","/");
			startLoading(youtubeURL);
		}

		private function startLoading (urlString:String):void {			
			var service:HTTPService = new HTTPService();
     		     
     		service.url = GET_VIDEO_URL + "?url=" + urlString;
     		service.addEventListener(ResultEvent.RESULT, onLoadVideoInfoComplete);
     		service.send();     			
		}

		private function onLoadVideoInfoComplete (event:ResultEvent):void {
			this.flvURL = String(event.result);
		}
	}
}

