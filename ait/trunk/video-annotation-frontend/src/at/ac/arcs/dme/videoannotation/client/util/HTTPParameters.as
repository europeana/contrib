package at.ac.arcs.dme.videoannotation.client.util
{
    /**
    * Tuple of the HTTP Parameters this application takes
    * 
    * @author Manuel Gay
    **/
    public class HTTPParameters
    {
        
        private var _objectUrl:String;
    	private var _user:String;
    	private var _db:String;
    	private var _telplusId:String;

        
        public function HTTPParameters() {
        }
        
        public function get objectURL():String {
            return this._objectUrl;
        }
        
        public function get user():String {
            return this._user;
        }
        
        public function get db():String {
            return this._db;
        }
        
        public function get telplusId():String {
            return this._telplusId;
        }
        
        public function set objectURL(url:String):void {
            this._objectUrl = url;
        }
        
        public function set user(user:String):void {
            this._user = user;
        }
        
        public function set db(db:String):void {
            this._db = db;
        }
        
        public function set telplusId(telplusId:String):void {
            this._telplusId = telplusId;
        }

    }
}