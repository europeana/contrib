package at.ac.arcs.dme.videoannotation.client.gui.annotation
{
    import at.ac.arcs.dme.videoannotation.client.model.AnnotationShape;
    import at.ac.arcs.dme.videoannotation.client.model.Ellipse;
    import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;
    
    import flash.events.MouseEvent;
    
    /**
     * A renderer for elliptical annotation shapes
     * 
     * @author Manuel Gay
     */
    public class EllipseAnnotationRenderer extends AbstractAnnotationRenderer
    {
        
        public function EllipseAnnotationRenderer() {
            super();
        }
        
        private static var MINIMUM_WIDTH:Number = 2;
        private static var MINIMUM_HEIGHT:Number = 2;
        
        override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
            var w:Number;
            var h:Number; 
            
            if(!isNaN(endX)){
            
                w = startX - endX;
                h = startY - endY;
                w = Math.abs(w);
                h = Math.abs(h);
    
                // we don't allow to draw in the other direction, or things will get too difficult        
                if(endX < startX && endY < startY){
                    w = MINIMUM_WIDTH;
                    h = MINIMUM_HEIGHT;
                }
                
                if(endX < startX && endY > startY)
                    w = MINIMUM_WIDTH;
                
                if(endX > startX && endY < startY)
                    h = MINIMUM_HEIGHT;
                
            } else {
                w = unscaledWidth;
                h = unscaledHeight;
            }
            
            lastWidth = w;
            lastHeight = h;
            endX = startX + w;
            endY = startY + h;

            

            graphics.clear();
            graphics.lineStyle(1, _color, 1);
            graphics.beginFill(_color, 0.5);
            graphics.drawEllipse(startX, startY, w, h);
            graphics.endFill();
        }
        
        
        override public function setShapeCoordinates(annotationShape:AnnotationShape):void {
            var r:Ellipse = annotationShape as Ellipse;
            
            this.startX = r.startX;
            this.startY = r.startY;
            this.endX = r.width + r.startX;
            this.endY = r.height + r.startY;
        }
        
        override public function getAnnotationShape():AnnotationShape {
            var e:Ellipse = new Ellipse();
            e.startX = startX;
            e.startY = startY;
            e.width = lastWidth;
            e.height = lastHeight;
            e.color = color;
            
            return e;
        }
        
    }
}