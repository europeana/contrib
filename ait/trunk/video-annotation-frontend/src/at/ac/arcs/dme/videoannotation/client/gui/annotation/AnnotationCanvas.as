package at.ac.arcs.dme.videoannotation.client.gui.annotation
{
    import at.ac.arcs.dme.videoannotation.client.event.AnnotationEvent;
    import at.ac.arcs.dme.videoannotation.client.event.ApplicationEvent;
    import at.ac.arcs.dme.videoannotation.client.event.UserEvent;
    import at.ac.arcs.dme.videoannotation.client.model.AnnotationShape;
    import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
    
    import com.ericfeminella.collections.HashMap;
    
    import flash.events.Event;
    import flash.events.MouseEvent;
    import flash.geom.Point;
    
    import mx.collections.ArrayCollection;
    import mx.containers.Canvas;
    import mx.events.VideoEvent;
    
    /**    
     * Special canvas on which shapes can be drawn, for annotation.
     * 
     * This class is a bit complicated, as it has to deal both with external events and with internal ones.
     * The principle of the canvas is that it can display a number of shapes simulteanously by using renderers
     * (see AbstractAnotationRenderer, and its subclasses RectangleAnnotationRenderer and EllipseAnnotationRenderer).
     * 
     * Renderers are simple display objects that render in different shapes/colours, and each renderer of a shape to be
     * displayed is added as a child to the canvas.
     * 
     * The canvas keeps track of the currently displayed annotation shapes through the _displayedAnnotationsIds array.
     * 
     * @author Manuel Gay
     */
    public class AnnotationCanvas extends Canvas
    {
        
        public static const NONE:String = "none";
        public static const RECTANGLE:String = "rectangle";
        public static const ELLIPSE:String = "ellipse";
        
        /** the annotations whose shapes are drawn by the canvas **/
        private var _renderers:HashMap = new HashMap();
        
        /** reference to the current annotation **/
        private var _selectedAnnotation:VideoAnnotation;
        
        /** list of IDs of the currently displayed annotations **/
        private var _displayedAnnotationsId:ArrayCollection = new ArrayCollection();
        
        /** toggle for automatic annotation rendering **/
        private var _requestedFrameDisplay:Boolean = false;
        
        /** instance of the renderer that is selected during edition **/
        private var _selectedRenderer:AbstractAnnotationRenderer;
        
        /** active annotation color selected in the form **/
        private var _activeColor:uint;
        
        /** current time index **/
        private var _currentPlayheadTime:Number = 0;
        
        /** presentation data, i.e. all annotations, needed to keep a track of what to display **/
    	private var _presentationData:Array;    	
        
        public function AnnotationCanvas() {
            
        	// we create a mapping between the selected annotation tool and the internal tool type
        	// actionScript lets us do this in an interesting fashion
        	AnnotationEvent[UserEvent.SELECT_NO_SHAPE_TOOL] = NONE;
        	AnnotationEvent[UserEvent.SELECT_RECTANGLE_SHAPE_TOOL] = RECTANGLE;
        	AnnotationEvent[UserEvent.SELECT_ELLIPSE_SHAPE_TOOL] = ELLIPSE;
        	
        	AnnotationShape[AnnotationShape.NO_SHAPE] = NONE;
        	AnnotationShape[AnnotationShape.RECTANGLE_SHAPE] = RECTANGLE;
        	AnnotationShape[AnnotationShape.ELLIPSE_SHAPE] = ELLIPSE;
       	
        }
        
        /** event handlers **/
        
        public function handleCanvasReadonlyEvent(event:AnnotationEvent):void {
            canvasReadonly();
        }
        
        public function handleCanvasClear(event:Event):void {
            clearCanvas();
        }
        
        public function handleNewShape(event:AnnotationEvent):void {
            this._selectedAnnotation = event.annotation;
            
            clearCanvas();

            initializeAnnotationRenderer(event.annotation);

            canvasEditable();
        }
        
        public function handleEditShape(event:AnnotationEvent):void {
            this._selectedAnnotation = event.annotation;

            // display the frame we edit
            this._requestedFrameDisplay = true;	
            this.systemManager.dispatchEvent(new AnnotationEvent(AnnotationEvent.DISPLAY_FRAME, null));

            // clear the canvas, i.e. remove all shapes including other shapes that are not from the annotation we edit    
            clearCanvas();

            initializeAnnotationRenderer(event.annotation);
            
            // draw the shape
            if(event.annotation.videoFragment != null && event.annotation.videoFragment.shape != null) {
    		    drawAnnotationShape(event.annotation);
    		}
 
            // make the canvas editable
            canvasEditable();
        }
        
        /**
         * handles the selection of a specific annotation tool in the interface (annotation form)
         * this will instantiate a new renderer of the selected type and associate it with the annotation
         **/
        public function on_annotationToolSelected(event:UserEvent):void {

            // if there is already a renderer associated to the annotation clear the shape made with the old renderer
            if(_renderers.containsKey(event.annotation.id)) {
                var r:AbstractAnnotationRenderer = _renderers.getValue(event.annotation.id);
                if(r != null) {
                    removeChild(r);
                }
                invalidateDisplayList();
            }
            
            initializeAnnotationRenderer(event.annotation);

            // if there is a concrete tool selected, make the canvas editable
            if(getShapeType(event.annotation) != NONE) {
                canvasEditable();
            }
            
            // when selecting a new tool, clear the shape we previously drew with the renderer
            if(getShapeType(event.annotation) != AnnotationEvent[event.type]) {
                clearCanvas();
            }
            
            // set the current annotation
            this._selectedAnnotation = event.annotation;
        }
        
        /** changes the color of the active renderer **/
        public function on_changeAnnotationToolColor(event:UserEvent):void {
            if(event.annotation.videoFragment.shape != null) {
                _activeColor = event.annotation.videoFragment.shape.color;
            }
            
            if(!_renderers.containsKey(event.annotation.id)) {
                return;
            }
            var r:AbstractAnnotationRenderer = _renderers.getValue(event.annotation.id);
            if(r == null) {
                initializeAnnotationRenderer(event.annotation);
                r = _renderers.getValue(event.annotation.id);
            }
            if(getShapeType(event.annotation) == NONE) {
                return;
            }
            
            if(event.annotation.videoFragment.shape != null) {
                r.color = event.annotation.videoFragment.shape.color;
            }
            r.invalidateDisplayList();
            invalidateDisplayList();
        }
        
        private var wasJustStopped:Boolean = false;

        /** whenver we move the playHead we want to display the annotation shapes of fragments at that point in time **/
        public function handlePlayHeadUpdate(event:VideoEvent):void {

            this._currentPlayheadTime = event.playheadTime;
            
            if(_requestedFrameDisplay) {
                _requestedFrameDisplay = false;
                return;
            }
            
            // this is a workaround for a strange behavior of the player: when the video is stopped, the player nonetheless
            // sends one more frame, which causes the shapes to be redrawn
            // so in case of a STOP we ignore the next frame
            if(wasJustStopped) {
                wasJustStopped = false;
            } else {
                drawAnnotationsAtTime(event.playheadTime);
            }
        }
        
        public function handleVideoStop(event:ApplicationEvent):void {
            clearCanvas();
            wasJustStopped = true;
            
        }
        
        /** when new presentation data (annotations) comes in, handle the update and smooth display of annotation shapes **/
        public function handleLoadData(event:ApplicationEvent):void {
            trace("***** LOADING NEW PRESENTATION DATA, time: " + this._currentPlayheadTime);
            this._presentationData = event.presentationData;
            
            // when we load the presentation data, it may happen that the IDs of the displayed shapes become invalid
            // hence we simply forget about all of them now, by clearing the canvas
            clearCanvas();
            
            // however, we want our annotations to be displayed again
            drawAnnotationsAtTime(this._currentPlayheadTime);
        }




        /*******************************************/
	    /**    Methods for canvas manipulation     */
	    /*******************************************/
	    
        private function canvasEditable():void {
            this.addEventListener(MouseEvent.MOUSE_DOWN, handleDownEvent);
            
            // enable drag & drop on the shape, but only if we already have a shape renderer (otherwise there's not much to drag around)
            if(_selectedAnnotation != null && _renderers.getValue(_selectedAnnotation.id) != null) {
                makeShapeDraggable(_renderers.getValue(_selectedAnnotation.id));
            }
            
            trace("canvas editable");
        }
        
        private function canvasReadonly():void {
            this.removeEventListener(MouseEvent.MOUSE_DOWN, handleDownEvent, false);
            
            if(_selectedAnnotation != null && _renderers.getValue(_selectedAnnotation.id) != null) {
                makeShapeUndraggable(_renderers.getValue(_selectedAnnotation.id));
            }
            
            trace("canvas read only");
        }
        
        /** draws an annotation shape and updates the list of displayed annotations **/
        private function drawAnnotationShape(annotation:VideoAnnotation):void {
            
            var annotationShape:AnnotationShape = annotation.videoFragment.shape;
            
            if(annotationShape.type == AnnotationShape.NO_SHAPE) {
                return;
            }

            trace("drawing shape " + annotation.id);
            var renderer:AbstractAnnotationRenderer = _renderers.getValue(annotation.id);
            
            if(renderer == null) {
                return;
            }

            // add renderer to the list of children display objects of the canvas
            addChild(renderer);

            // register the annotation as being displayed
            this._displayedAnnotationsId.addItem(annotation.id);
            
            // set the shape coordinates and update the display list
            // the latter will draw the shape by calling the updateDisplayList method of the renderer
            trace("** set shape coordinates for shape " + annotationShape.toString());
            renderer.setShapeCoordinates(annotationShape);
            renderer.setShapeColor(annotationShape);
            renderer.invalidateDisplayList();
            
            // set the current renderer, as we need this global state for the drag & drop event listeners
            this._selectedRenderer = renderer;
        }
        
        /** draws all the shapes of annotations for a given time index **/
        private function drawAnnotationsAtTime(time:Number):void {
            trace("drawing annotations for time index " + time);
            
            // find the annotations that have shapes to be displayed
            for each (var a:VideoAnnotation in _presentationData) {
                if(a.videoFragment.shape != null) {
                    if((a.videoFragment.timeFrom <= time && time <= a.videoFragment.timeTo) && !_displayedAnnotationsId.contains(a.id)) {
                        trace("** displayed annotations array " + _displayedAnnotationsId);
                        // if we did not yet display the shape, draw it
                        trace("displaying shape of annotation :" + a.id);
                        initializeAnnotationRenderer(a);
                        drawAnnotationShape(a);
                    } else if ((a.videoFragment.timeFrom < time && a.videoFragment.timeTo < time) && _displayedAnnotationsId.contains(a.id)) {
                        trace("removing shape of annotation :" + a.id);
                        // if we displayed the shape but now it shouldn't be shown anymore
                        removeShape(a);
                    }
                }
            }
        }
        
        /** removes a shape from the canvas **/
        public function removeShape(annotation:VideoAnnotation):void {
            if(_renderers.getValue(annotation.id) != null) {
                _renderers.getValue(annotation.id).clear();
            }
            if(_displayedAnnotationsId.contains(annotation.id)) {
                _displayedAnnotationsId.removeItemAt(_displayedAnnotationsId.getItemIndex(annotation.id));
            }
            invalidateDisplayList();
        }
        
        /** clears the whole canvas **/
        public function clearCanvas():void {
            trace("clearing canvas");
            removeAllChildren();
            this._displayedAnnotationsId.removeAll();
            invalidateDisplayList();
        }
        
        
        
        
        
        
        
        /********************************************************************/
	    /**    Internal events, that do the actual drawing and dragging     */
	    /********************************************************************/
	    
        private function handleDownEvent(event:MouseEvent):void {

            if(!isOnShape(event) && !shapeDragging) {
                // first we clear the existing annotation, then we can start drawing
                removeShape(_selectedAnnotation);
                startDragging(event, _selectedAnnotation.videoFragment.shape.color);
            }
        }        
        
        private function startDragging(event:MouseEvent, color:uint):void {
            
            if(getShapeType(_selectedAnnotation) != NONE && getShapeType(_selectedAnnotation)  != null) {
            
                activateAnnotationRenderer(event, color);
                
                systemManager.addEventListener(MouseEvent.MOUSE_MOVE, systemManager_mouseMoveHandler, true);
                systemManager.addEventListener(MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);
                addEventListener(MouseEvent.ROLL_OVER, systemManager_mouseMoveHandler, true);
                
            }
        }
        
        private function stopDragging():void {
            
            systemManager.removeEventListener(MouseEvent.MOUSE_MOVE, systemManager_mouseMoveHandler, true);
            systemManager.removeEventListener(MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);
            removeEventListener(MouseEvent.ROLL_OVER, systemManager_mouseMoveHandler, true);
            
            // now we save the shape to the annotation
            this._selectedAnnotation.videoFragment.shape = this._selectedRenderer.getAnnotationShape();
        }
        
        private function systemManager_mouseMoveHandler(event:MouseEvent):void {
            
            if(!shapeDragging) {
            
                var pt:Point = new Point(event.stageX,event.stageY);
                pt = globalToLocal(pt);
    
                this._selectedRenderer.endX = pt.x;
                this._selectedRenderer.endY = pt.y;
                
                if(pt.x > this.width) {
                    this._selectedRenderer.endX = this.width - 1;
                }
                if(pt.y > this.height) {
                    this._selectedRenderer.endY = this.height - 1;
                }
                
                this._selectedRenderer.invalidateDisplayList();
            }

        }
        
        private function systemManager_mouseUpHandler(event:MouseEvent):void {
            
            if(!shapeDragging) {
                stopDragging();
                
                // after creating the shape we directly make it draggable
                makeShapeDraggable(_renderers.getValue(_selectedAnnotation.id));
            }
        }
        
        private var startOffsetX:Number;
        private var startOffsetY:Number;
        private var endOffsetX:Number;
        private var endOffsetY:Number;
        private var shapeDragging:Boolean = false;
        
        
        private function makeShapeDraggable(renderer:AbstractAnnotationRenderer):void {
            // enable drag & drop on the shape
            renderer.addEventListener(MouseEvent.MOUSE_DOWN, startShapeDragging);
            renderer.addEventListener(MouseEvent.MOUSE_UP, stopShapeDragging);
        }
        
        private function makeShapeUndraggable(renderer:AbstractAnnotationRenderer):void {
            // disable drag & drop on the shape
            renderer.removeEventListener(MouseEvent.MOUSE_DOWN, startShapeDragging, false);
            renderer.removeEventListener(MouseEvent.MOUSE_UP, stopShapeDragging, false);
        }
        
        
        private function startShapeDragging(event:MouseEvent):void {
           
           if(isOnShape(event) || shapeDragging) {
               shapeDragging = true;
            
               var mouseLocal:Point = globalToLocal(new Point(event.stageX, event.stageY));
            
               // keep the offset between the mouse position and the shape position, both for the top-left and bottom-right
               startOffsetX = mouseLocal.x - this._selectedRenderer.startX;
               startOffsetY = mouseLocal.y - this._selectedRenderer.startY;
               endOffsetX = this._selectedRenderer.endX - mouseLocal.x;
               endOffsetY = this._selectedRenderer.endY - mouseLocal.y;
               
               stage.addEventListener(MouseEvent.MOUSE_MOVE, dragShape);
           }
        }
        
        private function stopShapeDragging(event:MouseEvent):void {
            stage.removeEventListener(MouseEvent.MOUSE_MOVE, dragShape);
            shapeDragging = false;
            
            // now we save the shape to the annotation with the new coordinates
            var newShape:AnnotationShape = this._selectedRenderer.getAnnotationShape();
            
            if(this._selectedAnnotation.videoFragment.shape != null) {
                newShape.color = this._selectedAnnotation.videoFragment.shape.color;
            }
            this._selectedAnnotation.videoFragment.shape = newShape;

        }
        
        private function dragShape(event:MouseEvent):void {
            
            var mouseLocal:Point = globalToLocal(new Point(event.stageX, event.stageY));
            
            // if we're not going to drag out of the canvas, we update the renderer's shape coordinates
            if(mouseLocal.x + endOffsetX < this.width && mouseLocal.x - startOffsetX > 0 && mouseLocal.y + endOffsetY < this.height && mouseLocal.y - startOffsetY > 0) {
                this._selectedRenderer.startX = mouseLocal.x - startOffsetX;
                this._selectedRenderer.startY = mouseLocal.y - startOffsetY;
                this._selectedRenderer.endX = this._selectedRenderer.startX + this._selectedRenderer.lastWidth;
                this._selectedRenderer.endY = this._selectedRenderer.startY + this._selectedRenderer.lastHeight;
            
                event.updateAfterEvent();
                this._selectedRenderer.invalidateDisplayList();
            }/* else {
                trace("we're too far out:");
                trace("mouseLocal.x " + mouseLocal.x);
                trace("mouseLocal.y " + mouseLocal.y);
                trace("startOffsetX " + startOffsetX);
                trace("startOffsetY " + startOffsetY);
                trace("endOffsetX " + endOffsetX);
                trace("endOffsetY " + endOffsetY);
                trace("shapeWidth " + selectedRenderer.lastWidth);
                trace("shapeHeight " + selectedRenderer.lastHeight);
                trace("height " + height);
                trace("width " + width);
                trace("mouseLocal.x + endOffsetX " + (mouseLocal.x + endOffsetX).toString());
                trace("mouseLocal.x - startOffsetX "+ (mouseLocal.x - startOffsetX).toString());
                trace("mouseLocal.y + endOffsetY " + (mouseLocal.y + endOffsetY).toString());
                trace("mouseLocal.y - startOffsetY " + (mouseLocal.y - startOffsetY).toString());
            } */
        }
        
        private function isOnShape(event:MouseEvent):Boolean {
            
           if(this._selectedAnnotation == null || _renderers.getValue(_selectedAnnotation.id) == null) {
               return false;
           }
            
           var selectedRenderer:AbstractAnnotationRenderer = _renderers.getValue(_selectedAnnotation.id);
            
           var mouse:Point = new Point(event.stageX, event.stageY);
           var mouseLocal:Point = globalToLocal(mouse);
           
           return mouseLocal.x > selectedRenderer.startX && mouseLocal.x < selectedRenderer.endX 
               && mouseLocal.y > selectedRenderer.startY && mouseLocal.y < selectedRenderer.endY;
        }
        
        private function getShapeType(annotation:VideoAnnotation):String {
            return AnnotationShape[annotation.videoFragment.shape.type];
        }
        
        private function initializeAnnotationRenderer(annotation:VideoAnnotation):void {
            _renderers.put(annotation.id, getAnnotationRendererInstance(getShapeType(annotation), _activeColor));
        }
        
        private function activateAnnotationRenderer(event:MouseEvent, color:uint):void {
            this._selectedRenderer = _renderers.getValue(_selectedAnnotation.id);
            this._selectedRenderer.initalize(event, color);
            addChildAt(this._selectedRenderer, 0);
        }
        
        private function getAnnotationRendererInstance(type:String, color:uint):AbstractAnnotationRenderer {
            
            var r:AbstractAnnotationRenderer = null;
            
            if(type == RECTANGLE) {
                 r = new RectangleAnnotationRenderer();
            }
            
            if(type == ELLIPSE) {
                r = new EllipseAnnotationRenderer();
            }
            
            if(r != null) {
                r.color = color;
            }
            
            return r;
        }
    }
}