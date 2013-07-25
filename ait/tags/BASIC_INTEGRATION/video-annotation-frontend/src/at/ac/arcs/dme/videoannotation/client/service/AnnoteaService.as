package at.ac.arcs.dme.videoannotation.client.service
{
	import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;

	/**
	 * The AnnoteaService interface is the client side interface for the annotation middleware
     *
     * @author Stefan Pomajbik
     * @author Christian Sadilek
     **/
	public interface AnnoteaService
	{
		function set serviceUrl(serviceUrl:String):void;

		function createAnnotation(_videoAnnotation:VideoAnnotation,handler:Function):void;

        function updateAnnotation(_videoAnnotation:VideoAnnotation,handler:Function):void;

        function deleteAnnotation(_videoAnnotationId:String,handler:Function):void;

        function findAnnotationById(_videoAnnotationId:String,handler:Function):void;

		function findAnnotationBodyById(_videoAnnotationId:String,handler:Function):void;

		function listAnnotations(objectId:String,rootID:String,handler:Function):void;

	}
}