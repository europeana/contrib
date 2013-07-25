package at.ac.arcs.dme.videoannotation.client.gui.annotation
{
    import at.ac.arcs.dme.videoannotation.client.model.AnnotationShape;
    
    import flash.events.MouseEvent;
    
    import mx.core.UIComponent;
    
    /**
     * An abstract annotation renderer that represents a tool to be used for graphical annotation (e.g. a rectangle, circle, ellipse, ...)
     * 
     * @author Manuel Gay
     */
    public class AbstractAnnotationRenderer extends UIComponent
    {
        
        public var startX:Number;
        public var startY:Number;
    
        public var endX:Number;
        public var endY:Number;
        
        public var lastWidth:Number;
        public var lastHeight:Number;
        
        protected var _color:uint = 0xFFF000;
        
        public function AbstractAnnotationRenderer() {
            
        }
        
        public function get color():uint {
            return this._color;
        }
        
        public function set color(color:uint):void {
            this._color = color;
        }

        public function initalize(event:MouseEvent, color:uint):void {
            this.startX = event.localX;
            this.startY = event.localY;
            
            this.endX = event.localX;
            this.endY = event.localY;
            
            this.color = color;
            
            this.invalidateDisplayList();
        }
       
        /** sets the shape coordinates for the shape to be rendered */
        public function setShapeCoordinates(annotationShape:AnnotationShape):void {
            // abstract - to be overriden by each subclass
        }
        
        /** sets the color of the shape **/
        public function setShapeColor(annotationShape:AnnotationShape):void {
            this._color = annotationShape.color;
        }
        
        /** gets an instance of the shape to be rendered */
        public function getAnnotationShape():AnnotationShape {
            // abstract - to be overriden by each subclass
            return null;
        }
        
        /** clears the shape **/
        public function clear():void {
            graphics.clear();
        }
    }
}