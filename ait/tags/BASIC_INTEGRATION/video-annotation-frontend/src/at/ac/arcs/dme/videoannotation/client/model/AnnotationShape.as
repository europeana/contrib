package at.ac.arcs.dme.videoannotation.client.model
{
    /**
     * An annotation shape, used to mark a video fragment graphically
     * 
     * @author Manuel Gay
     **/
    [Bindable] public class AnnotationShape
    {
        
        public static const NO_SHAPE:String = "noShape";
        public static const RECTANGLE_SHAPE:String = "rectangleShape";
        public static const ELLIPSE_SHAPE:String = "ellipsisShape";
        
        public var startX:Number;
        public var startY:Number;
        
        protected var _type:String;
        private var _color:uint;
        
        public function AnnotationShape()
        {
            this._type = NO_SHAPE;
        }
        
        public function set type(type:String):void {
            this._type = type;
        }
        
        public function get type():String {
            return this._type;
        }
        
        public function set color(color:uint):void {
            this._color = color;
        }
        
        public function get color():uint {
            return this._color;
        }
        
        public function get isNoShape():Boolean {
            return this._type == NO_SHAPE;
        }
        
        public function get isRectangleShape():Boolean {
            return this._type == RECTANGLE_SHAPE;
        }

        public function get isEllipseShape():Boolean {
            return this._type == ELLIPSE_SHAPE;
        }
        
        public function set isNoShape(t:Boolean):void {}
        public function set isRectangleShape(t:Boolean):void {}
        public function set isEllipseShape(t:Boolean):void {}
        
        public function toString():String { return "type:" + _type + " startX: " + startX + " startY: " + startY + " color: " + color}

    }
}