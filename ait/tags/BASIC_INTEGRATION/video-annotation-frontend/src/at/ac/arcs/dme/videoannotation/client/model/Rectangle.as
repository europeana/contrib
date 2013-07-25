package at.ac.arcs.dme.videoannotation.client.model
{
     /**
     * Rectangular shape
     * 
     * @author Manuel Gay 
     **/
     public class Rectangle extends AnnotationShape
    {
        
        public var width:Number;
        public var height:Number;
        
        public function Rectangle()
        {
            super();
            this._type = RECTANGLE_SHAPE;
        }
        
        public override function toString():String {
            return super.toString() + " width: " + width + " height: " + height;
        }

    }
}