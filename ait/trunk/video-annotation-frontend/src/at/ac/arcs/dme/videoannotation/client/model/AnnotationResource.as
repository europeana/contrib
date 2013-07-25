package at.ac.arcs.dme.videoannotation.client.model
{
    /**
     * An external resource the annotation is related to
     **/
    [Bindable] public class AnnotationResource
    {

        private var _url:String;
        private var _language:String;
        private var _description:String;
        private var _name:String;

        public function AnnotationResource(name:String, url:String, language:String, description:String) {
            this._name = name;
            this._url = url;
            this._language = language;
            this._description = description;
        }
        
        public function get name():String {
            return this._name;
        }
        
        public function set name(n:String):void {
            this._name = n;
        }
        
        public function get url():String {
            return this._url;
        }
        
        public function set url(u:String):void {
            this._url = u;
        }
        
        public function get language():String {
            return this._language;
        }
        
        public function set language(l:String):void {
            this._language = l;
        }
        
        public function get description():String {
            return this._description;
        }
        
        public function set description(d:String):void {
            this.description = d;
        }

    }
}