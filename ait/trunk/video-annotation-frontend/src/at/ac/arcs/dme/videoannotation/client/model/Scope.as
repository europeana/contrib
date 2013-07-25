package at.ac.arcs.dme.videoannotation.client.model
{
       /**
        * Scope of the annotation
        * 
        * @author Christian Sadilek 
        **/
	   [Bindable] public class Scope {
		private var _name:String = null;
		
		public function Scope(scopeName:String) {
			_name = scopeName;
		}
		public function get name():String {
			return _name;
		}
		public static function valueOf(name:String):Scope {
			switch(name) {
				case "public": return PUBLIC;
				case "private": return PRIVATE;				
			}
			return PUBLIC;	
		}
		
		public static const PUBLIC:Scope = new Scope("public");
		public static const PRIVATE:Scope = new Scope("private");				
	}
}