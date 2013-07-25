package at.ac.arcs.dme.videoannotation.client.model
{
    /**
     * Elliptical shape
     * 
     * @author Manuel Gay 
     **/
    public class Ellipse extends AnnotationShape
    {
        
        public var width:Number;
        public var height:Number;
        
        public function Ellipse()
        {
            super();
            this._type = ELLIPSE_SHAPE;
        }
        
        public function initialize(Cx:Number, Cy:Number, Rx:Number, Ry:Number):void {
            
            width = Rx * 2;
            height = Ry * 2;
            startX = Cx - Rx;
            startY = Cy - Ry;
        }
        
        public function get Cx():Number {
    		return startX + (width / 2);
    	}
    
    	public function get Cy():Number {
    		return startY + (height / 2) ;
    	}
    
    	public function get Rx():Number {
    		return (width / 2);
    	}
    
    	public function get Ry():Number {
    		return (height / 2);
    	}
    	
    	public override function toString():String {
    	    return "type: " + _type + " Cx: " + Cx + " Cy: " + Cy + " Rx: " + Rx + " Ry: " + Ry;
    	}

        
    }
}