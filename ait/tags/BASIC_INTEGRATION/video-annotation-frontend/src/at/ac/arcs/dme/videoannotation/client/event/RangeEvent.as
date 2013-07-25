package at.ac.arcs.dme.videoannotation.client.event
{
	import flash.events.Event;

	/** The RangeEvent class represents the event object passed to the event listener for changes of the RangeSelector
	 *  (this type can be explicitly used instead of the SliderEvent)
     *
     * @author Stefan Pomajbik
     * @author Manuel Gay 
     **/
	public class RangeEvent extends Event
	{
		public static const CHANGE_PLAYER_RANGE:String = "changePlayerRange";
		public static const CHANGE_FORM_RANGE:String = "chageFormRange";
		
		
		private var _range:Array = null;

		public function RangeEvent(type:String, range:Array, bubbles:Boolean=false, cancelable:Boolean=false) {
			super(type, bubbles, cancelable);
			this._range = range;
		}

		public function get range():Array {
			return this._range;
		}

	}
}