package at.ac.arcs.dme.videoannotation.client.service
{
	import at.ac.arcs.dme.videoannotation.client.model.AnnotationResource;
	import at.ac.arcs.dme.videoannotation.client.model.AnnotationShape;
	import at.ac.arcs.dme.videoannotation.client.model.Ellipse;
	import at.ac.arcs.dme.videoannotation.client.model.Rectangle;
	import at.ac.arcs.dme.videoannotation.client.model.Scope;
	import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
	import at.ac.arcs.dme.videoannotation.client.model.VideoFragment;
	import at.ac.arcs.dme.videoannotation.client.util.MiscUtil;
	
	import com.adobe.utils.DateUtil;
	
	import mx.core.Application;

	/**
	 * RdfXmlAnnotationBuilder de/serializer for annotations
     *
     * @author Stefan Pomajbik
     * @author Christian Sadilek
     * @author Manuel Gay
     **/
	public class RdfXmlAnnotationBuilder
	{
        public static const RDF_NS:Namespace = new Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        public static const DC_NS:Namespace  = new Namespace("dc", "http://purl.org/dc/elements/1.1/");
        public static const RDFS_NS:Namespace = new Namespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        public static const HTTP_NS:Namespace = new Namespace("h", "http://www.w3.org/1999/xx/http#");
        public static const THREAD_NS:Namespace = new Namespace("tr", "http://www.w3.org/2001/03/thread#");
        public static const REPLYTYPE_NS:Namespace = new Namespace("rt", "http://www.w3.org/2001/12/replyType#");
		public static const ANNOTATION_NS:Namespace = new Namespace("a", "http://lemo.mminf.univie.ac.at/annotation-core#");
		public static const ANNOTATION_VIDEO_NS:Namespace = new Namespace("video", "http://lemo.mminf.univie.ac.at/annotation-video#");
		public static const ANNOTEA_ANNOTATION_NS:Namespace = new Namespace("ann", "http://www.w3.org/2000/10/annotation-ns#");
		public static const ANNOTATION_SCOPE_NS:Namespace = new Namespace("scope", "http://lemo.mminf.univie.ac.at/ann-tel#");				
		public static const ANNOTATION_REL_NS:Namespace = new Namespace("rel", "http://lemo.mminf.univie.ac.at/ann-relationship#");				
		public static const ANNOTATIONTYPE_NS:Namespace = new Namespace("at", "http://www.w3.org/2000/10/annotationType#");

		public static const ANNOTATION:String = ANNOTATION_NS+"Annotation";
		public static const ANNOTATION_ANNOTATES:String = ANNOTATION_NS+"annotates";
		public static const ANNOTATION_CREATED:String = ANNOTATION_NS+"created";
		public static const ANNOTATION_BODY:String = ANNOTATION_NS+"body";
		public static const ANNOTATION_FRAGMENT:String = ANNOTATION_NS+"fragment";
		public static const ANNOTATION_LINKED_TO:String = ANNOTATION_REL_NS+"isLinkedTo";
		public static const ANNOTATION_SCOPE:String = ANNOTATION_SCOPE_NS+"scope";		

		public static const ANNOTATION_TYPE_COMMENT:String = ANNOTATIONTYPE_NS+"Comment";

		public static const REPLY_TYPE_AGREE:String = REPLYTYPE_NS+"Agree";

		public static const THREAD_REPLY:String = THREAD_NS+"Reply";
		public static const THREAD_IN_REPLY_TO:String = THREAD_NS+"inReplyTo";
		public static const THREAD_ROOT:String = THREAD_NS+"root";

		public static const HTTP_BODY:String = HTTP_NS+"Body";
		public static const HTTP_CONTENT_TYPE:String = HTTP_NS+"ContentType";
		public static const HTTP_CONTENT_LENGTH:String = HTTP_NS+"ContentLength";

		public static const DUBLIN_CORE_DATE:String = DC_NS+"date";
		public static const DUBLIN_CORE_CREATOR:String = DC_NS+"creator";
		public static const DUBLIN_CORE_FORMAT:String = DC_NS+"format";		
		public static const DUBLIN_CORE_TITLE:String = DC_NS+"title";
		public static const DUBLIN_CORE_LANGUAGE:String = DC_NS+"language";
		public static const DUBLIN_CORE_DESCRIPTION:String = DC_NS+"description";
		
		

		public function RdfXmlAnnotationBuilder(){}

		public static function fromRdfXml( _rdfxml:XMLList):Array {
			trace("fromRdfXml called with:\n"+ _rdfxml.toXMLString());
			return buildAnnotationList(_rdfxml);
		}

		// takes an XMLList and convertes each item to an object of type VideoAnnotation
		// returns an Array of VideoAnnotations
		private static function buildAnnotationList(list:XMLList):Array {
            var rootAnnotations:Array = new Array();
            var allReplies:Object = new Object();
          
            var item:XML;
            for each(item in list) {
				// each item represents an annotation; so we create a new one
            	var videoAnnotation:VideoAnnotation = new VideoAnnotation(null);
				// we parse the id values
 	  	        videoAnnotation.id = item.@RDF_NS::about;
            	videoAnnotation.aboutId = item.ANNOTATION_NS::annotates.@RDF_NS::resource;
            	if (item.THREAD_NS::inReplyTo.@RDF_NS::resource != undefined) {
            		videoAnnotation.replyToId = item.THREAD_NS::inReplyTo.@RDF_NS::resource;
            	}
            	if (item.THREAD_NS::root.@RDF_NS::resource != undefined) {
            		videoAnnotation.rootId = item.THREAD_NS::root.@RDF_NS::resource;
            	}
            	if (item.ANNOTATION_REL_NS::isLinkedTo != undefined) {
            		videoAnnotation.telPlusId = item.ANNOTATION_REL_NS::isLinkedTo;
            	}
            	
            	// parse the mpeg21 string
            	if(item.ANNOTATION_NS::fragment != undefined) {
            	    parseMpeg21URI(item.ANNOTATION_NS::fragment, videoAnnotation);
            	}

                // parse related resources
            	if(item.ANNOTATION_REL_NS::isLinkedToResources != undefined) {
            	    var linkedToResources:XMLList = item.ANNOTATION_REL_NS::isLinkedToResources;
            	    for each(var resourceLink:XML in linkedToResources.children()) {
            	        var name:String = resourceLink.DC_NS::type;
            	        var url:String = resourceLink.RDFS_NS::seeAlso.@RDF_NS::resource;
            	        var lang:String = resourceLink.DC_NS::language;
            	        var desc:String = resourceLink.DC_NS::description;
            	        
            	        var res:AnnotationResource = new AnnotationResource(name, url, lang, desc);
            	        videoAnnotation.addResource(res);
            	    }
            	}

				// here we parse the rest of the annotations data
            	videoAnnotation.creator = item.DC_NS::creator;
            	videoAnnotation.creationDate = DateUtil.parseW3CDTF(item.ANNOTATION_NS::created);
            	videoAnnotation.updateDate = DateUtil.parseW3CDTF(item.DC_NS::date);
            	videoAnnotation.title = item.DC_NS::title;
            	videoAnnotation.text = item..HTTP_NS::Body..body;
            	
            	if(videoAnnotation.text == null) {
            	    videoAnnotation.text = "";
            	}
            	
            	if (item.ANNOTATION_SCOPE_NS::scope != undefined) {
            		var user:String = Application.application.parameters.user;
    				if (user == null) user = MiscUtil.getHtmlParameter("user");
            		videoAnnotation.scope = Scope.valueOf(item.ANNOTATION_SCOPE_NS::scope);  
            		if(videoAnnotation.scope == Scope.PRIVATE && user!=videoAnnotation.creator) 
            			continue;          		
            	}
            	
            	// collect replies
				if(videoAnnotation.replyToId!=null) {
					var replies:Array = allReplies[videoAnnotation.replyToId];
					if(replies==null) replies = new Array();
					replies.push(videoAnnotation);
					allReplies[videoAnnotation.replyToId]=replies;
				} else {	
					rootAnnotations.push(videoAnnotation);
				}
            }
             
            addAnnotationReplies(rootAnnotations, allReplies);                                    
            return rootAnnotations;
        }

		private static function addAnnotationReplies(annotations:Array, allReplies:Object):void {
			for each(var annotation:VideoAnnotation in annotations) {
            	var replies:Array = allReplies[annotation.id];
            	if(replies!=null) {
            		annotation.children = replies;            		
            		addAnnotationReplies(replies, allReplies);
            	}
            }				
		}			
		
		// takes an array of VideoAnnotations and serializes them into a single XML document
        public static function annoToRdfXml( _videoAnnotation:VideoAnnotation):XML {
        	trace("toRdfmXml called with:\n"+ _videoAnnotation);
        	var annos:Array = new Array();
        	annos.push(_videoAnnotation);
        	return serializeToRdfXml(annos);
        }

		// takes an array of VideoAnnotations and serializes them into a single XML document
        public static function annosToRdfXml( _videoAnnotation:Array):XML {
        	trace("toRdfmXml called with:\n"+ _videoAnnotation);
        	return serializeToRdfXml(_videoAnnotation);
        }

        private static function getVideoFragmentTimeFrom(videoFragment:VideoFragment):String {
        	if (videoFragment != null) return videoFragment.timeFrom.toString(); else return null;
        }

        private static function getVideoFragmentTimeTo(videoFragment:VideoFragment):String {
        	if (videoFragment != null) return videoFragment.timeTo.toString(); else return null;
        }

        private static function getVideoFragmentShape(videoFragment:VideoFragment):AnnotationShape {
        	if (videoFragment != null) return videoFragment.shape; else return null;
        }
        
        private static function getVideoFragmentShapeColor(videoFragment:VideoFragment):uint {
            if(videoFragment != null && videoFragment.shape != null) return videoFragment.shape.color; else return 0;
        }
		
        private static function serializeToRdfXml( _videoAnnotation:Array):XML{
			// make a root node and add the needed namespace declarations
			var xml:XML = <rdf:RDF xmlns:rdf={RDF_NS}></rdf:RDF>
			xml.addNamespace(ANNOTATION_NS);
			xml.addNamespace(ANNOTEA_ANNOTATION_NS);	
			xml.addNamespace(ANNOTATION_REL_NS);	
			xml.addNamespace(ANNOTATION_SCOPE_NS);				
			xml.addNamespace(ANNOTATION_VIDEO_NS);		
			xml.addNamespace(DC_NS);
			xml.addNamespace(HTTP_NS);
			xml.addNamespace(THREAD_NS);
			// we iterate over all annotations within the array and build a single xml document
			for each(var vAnnotation:VideoAnnotation in _videoAnnotation) {
			    
			    var resources:XML = <rel:isLinkedToResources xmlns:rdf={RDF_NS} xmlns:rel={ANNOTATION_REL_NS} rdf:parseType="Literal"></rel:isLinkedToResources>;
			    for each(var annotationResource:AnnotationResource in vAnnotation.resources.getValues()) {
			        var resourceLink:XML = <resource-link></resource-link>;
			        var type:XML = <dc:type xmlns:dc={DC_NS}>{annotationResource.name}</dc:type>;
			        var seeAlso:XML = <rdfs:seeAlso xmlns:rdf={RDF_NS} xmlns:rdfs={RDFS_NS} rdf:resource={annotationResource.url} />;
				    var lang:XML = <dc:language xmlns:dc={DC_NS}>{annotationResource.language}</dc:language>;
                    var description:XML = <dc:description xmlns:dc={DC_NS}>{annotationResource.description}</dc:description>;
                    resourceLink.appendChild(type);
                    resourceLink.appendChild(seeAlso);
                    resourceLink.appendChild(lang);
                    resourceLink.appendChild(description);
                    
                    resources.appendChild(resourceLink);
			    }
	    
				// we have to define the namespace again so we can use them to build our xml
				// TODO also fragments should be optional									
				var annotation:XML= 
					<rdf:Description rdf:about={vAnnotation.id==VideoAnnotation.NEW_ANNOTATION_ID?null:vAnnotation.id}
					  xmlns:rdf={RDF_NS}
					  xmlns:rdfs={RDFS_NS}
					  xmlns:tr={THREAD_NS} 
                      xmlns:a={ANNOTATION_NS}
                      xmlns:ann={ANNOTEA_ANNOTATION_NS}
                      xmlns:rel={ANNOTATION_REL_NS} 
                      xmlns:scope={ANNOTATION_SCOPE_NS}
                      xmlns:video={ANNOTATION_VIDEO_NS}
                      xmlns:dc={DC_NS}
                      xmlns:h={HTTP_NS}>
																
						<a:fragment>{buildMpeg21URI(vAnnotation)}</a:fragment>
						<video:video-fragment rdf:parseType="Literal">
							<videoFragment>
								<timeFrom>{getVideoFragmentTimeFrom(vAnnotation.videoFragment)}</timeFrom>
								<timeTo>{getVideoFragmentTimeTo(vAnnotation.videoFragment)}</timeTo>
								<shape>{getVideoFragmentShape(vAnnotation.videoFragment)}</shape>
							</videoFragment>
						</video:video-fragment>
						<a:annotates rdf:resource={vAnnotation.aboutId} />
						<rel:isLinkedTo>{vAnnotation.telPlusId}</rel:isLinkedTo>
						<a:author>{vAnnotation.creator}</a:author>
						<dc:creator>{vAnnotation.creator}</dc:creator>
						<a:created>{DateUtil.toW3CDTF(vAnnotation.creationDate,true)}</a:created>
						<a:modified>{DateUtil.toW3CDTF(vAnnotation.updateDate,true)}</a:modified>										
						<dc:date>{DateUtil.toW3CDTF(vAnnotation.updateDate,true)}</dc:date>
						<dc:title>{vAnnotation.title==null?"":vAnnotation.title}</dc:title>
						<dc:format>video/x-flv</dc:format>
						<ann:body>
							<rdf:Description>
								<h:Body rdf:parseType="Literal">
									<html>
										<head>
											<title>{vAnnotation.title==null?"":vAnnotation.title}</title>
										</head>
										<body>
											{vAnnotation.text==null?"":vAnnotation.text}
										</body>
									</html>
								</h:Body>
							</rdf:Description>
						</ann:body>
						<a:label>{vAnnotation.text}</a:label>
						<scope:scope>{vAnnotation.scope.name}</scope:scope>
					</rdf:Description>

				if(vAnnotation.replyToId==null) {								
					var annotationType:XML = <rdf:type xmlns:rdf={RDF_NS} rdf:resource={ANNOTATION} />;
					var annotationTypeComment:XML = <rdf:type xmlns:rdf={RDF_NS} rdf:resource={ANNOTATION_TYPE_COMMENT} />;					 						
					annotation.appendChild(annotationType);
					annotation.appendChild(annotationTypeComment);					
				} else {
					var replyType:XML = <rdf:type xmlns:rdf={RDF_NS} rdf:resource={THREAD_REPLY} />;
					var agreeType:XML = <rdf:type xmlns:rdf={RDF_NS} rdf:resource={REPLY_TYPE_AGREE} />;
					var root:XML = <tr:root xmlns:rdf={RDF_NS} xmlns:tr={THREAD_NS} rdf:resource={vAnnotation.rootId} />;
					var inReplyto:XML = <tr:inReplyTo xmlns:rdf={RDF_NS} xmlns:tr={THREAD_NS} rdf:resource={vAnnotation.replyToId} />;						
					annotation.appendChild(replyType);
					annotation.appendChild(agreeType);
					annotation.appendChild(root);
					annotation.appendChild(inReplyto);					
				}
				
				if(resources.length() > 0) {
				    annotation.appendChild(resources);
				}
		
			    if (vAnnotation.id==VideoAnnotation.NEW_ANNOTATION_ID || vAnnotation.id==null)
			    	delete annotation.@RDF_NS::about;
			    	
			    if (vAnnotation.telPlusId==null)
			    	delete annotation.ANNOTATION_REL_NS::isLinkedTo;
			    	
			    if (vAnnotation.videoFragment==null) {
			    	delete annotation.ANNOTATION_NS::fragment;
			    	delete annotation.ANNOTATION_VIDEO_NS::["video-fragment"];
			    } else {
					if (vAnnotation.videoFragment.shape == null) 
						delete annotation.ANNOTATION_VIDEO_NS::["video-fragment"].videoFragment.shape;
				}
				
				xml.appendChild(annotation);
			}
			return xml;
        }
        
        private static function buildMpeg21URI(vAnnotation:VideoAnnotation):String {
            var mpeg21URI:String = vAnnotation.aboutId + "#mp(~time('npt','" + getVideoFragmentTimeFrom(vAnnotation.videoFragment) + "','" +
            getVideoFragmentTimeTo(vAnnotation.videoFragment) + "')";
            
            if(vAnnotation.videoFragment.shape != null && !vAnnotation.videoFragment.shape.isNoShape) {
                
                mpeg21URI += "/~region(";
                
                if(vAnnotation.videoFragment.shape is Rectangle) {
                    
                    var rect:Rectangle = vAnnotation.videoFragment.shape as Rectangle;
                    
                    mpeg21URI += "rect(";
                    mpeg21URI += rect.startX + "," + rect.startY + "," + (rect.startX + rect.width) + "," + (rect.startY + rect.height);
                    mpeg21URI += ")"
                } else if(vAnnotation.videoFragment.shape is Ellipse) {
                    
                    var ellipse:Ellipse = vAnnotation.videoFragment.shape as Ellipse;
                    
                    trace("ellipse: " + ellipse.toString());
                    mpeg21URI+= "ellipse(";
                    mpeg21URI+= ellipse.Cx;
                    mpeg21URI+= ",";
                    mpeg21URI+= ellipse.Cy;
                    mpeg21URI+= ",";
                    trace("Cx " + ellipse.Cx);
                    trace("rx " + ellipse.Rx);
                    trace("Ry " + ellipse.Ry);
                    var CxRx:Number = ellipse.Cx + ellipse.Rx;
                    trace("CxRx " + CxRx);
			        mpeg21URI+= CxRx;
			        mpeg21URI+= ",";
			        mpeg21URI+= ellipse.Ry;
			        mpeg21URI+= ",";
			        mpeg21URI+= CxRx;
			        mpeg21URI+= ",";
			        var CxRy:Number = ellipse.Cy + ellipse.Ry;
			        trace("CxRy " + CxRy);
			        mpeg21URI+= CxRy;
                    mpeg21URI+= ")";
                } else {
                    throw new Error("Trying to build unknown shape type in mpeg21URI encoder");
                }
                
                mpeg21URI += ")";

                mpeg21URI += "/~color(" + vAnnotation.videoFragment.shape.color + ")";
            
            }
            
            mpeg21URI += ")";
            
            return mpeg21URI;
        }
        
    	private static function parseMpeg21URI(mpeg21URI:String, vAnnotation:VideoAnnotation):void {

    	    // we want to parse seomthing of the sort
    	    // http://url/to/resource#mp(~time('npt','1234'5678')/~region(rect(123,456,789,1234)/~color(0xFFF000)))
    	    // we apply the robustness principle: http://en.wikipedia.org/wiki/Robustness_principle


            // during the parsing we store everything and build our VideoFragment only at the end	    
    	    var vFragment:VideoFragment;
    	    var tFrom:int = 0;
    	    var tTo:int = 0;
    	    var shape:AnnotationShape = new AnnotationShape();


    	    var URI:String = mpeg21URI.split('#')[0];
    	    var mpeg21String:String = mpeg21URI.split('#')[1];
    	    
    	    if(mpeg21String.indexOf("mp(") != 0) {
    	        // we only handle media pointers
    	        return;
    	    } else {
    	        mpeg21String = mpeg21String.substring(3, mpeg21String.length - 1);
    	    }
    	    
    	    var mpeg21Nodes:Array = mpeg21String.split('/');
    	    
    	    for each(var node:String in mpeg21Nodes) {
    	        
    	        if(node.indexOf('~') != 0) {
    	            // we don't process this node, however we see if other nodes can be processed
    	            continue;
    	        } else {
    	            node = node.substring(1);
    	        }
    	        
    	        if(node.indexOf("time(") == 0) {
    	            
    	            // fetch args
    	            var temporalArgs:Array = node.substring(5, node.length-1).split(',');
    	            
    	            // we support only the 'npt' time-index for now
    	            if(argValue(temporalArgs[0]) != 'npt') {
    	                continue;
    	            }
    	            
    	            tFrom = parseInt(argValue(temporalArgs[1]));
    	            tTo = parseInt(argValue(temporalArgs[2]));
    	            
    	            
    	        } else if(node.indexOf("region(")== 0) {
    	            
    	            var region:String = node.substring(7, node.length -1);
    	            
	                if(region.indexOf("rect(") == 0) {
    	                var rect:Rectangle = new Rectangle();
    	                var coord:Array = region.substring(5, region.length -1).split(',');
    	                rect.startX = parseFloat(argValue(coord[0]));
    	                rect.startY = parseFloat(argValue(coord[1]));
    	                rect.width = parseFloat(argValue(coord[2])) - rect.startX;
    	                rect.height = parseFloat(argValue(coord[3])) - rect.startY;
    	                
    	                shape = rect;
        	         } else if(region.indexOf("ellipse(") == 0) {
    	                var ellipse:Ellipse = new Ellipse();
    	                var coordEllipse:Array = region.substring(8, region.length -1).split(',');

                        // not sure which coordinate system this is, but we can use it	                
    	                var Cx:Number = parseFloat(argValue(coordEllipse[0]));
    	                var Cy:Number = parseFloat(argValue(coordEllipse[1]));
    	                var CxRx:Number = parseFloat(argValue(coordEllipse[2]));
    	                var Ry:Number = parseFloat(argValue(coordEllipse[3]));
    	                var CxRx2:Number = parseFloat(argValue(coordEllipse[4]));
//    	                var CyRy:int = parseInt(argValue(coordEllipse[5]));
    	                
    	                ellipse.initialize(Cx, Cy, CxRx - Cx, Ry);
    	                trace("ellipse after parsing: " + ellipse.toString());
    	                
    	                shape = ellipse;
    	             }
    	            
    	        } else if(node.indexOf("color(") == 0) {
    	            if(shape != null) {
    	                var color:String = node.substring(6, node.length - 1);
    	                shape.color = parseInt(argValue(color));
    	            }
    	        }
    	        
    	        
    	        
    	    }

            // finally we build the videoFragment
            vFragment = new VideoFragment(tFrom, tTo);
            vFragment.shape = shape;
            
            // if we don't have a shape we make a dummy "no shape" to avoid having to make null checks everywhere
            if(vFragment.shape == null) {
                vFragment.shape = new AnnotationShape();
            }
            
            vAnnotation.videoFragment = vFragment;
              
    	    
    	}
    	
    	private static function argValue(arg:String):String {
    	    if(arg.indexOf("'") == 0) {
    	        arg = arg.substring(1);
    	    }
    	    if(arg.indexOf("'") == arg.length -1) {
    	        arg = arg.substring(0, arg.length - 1);
    	    }
    	    return arg;
    	}
        
	}
	
}
