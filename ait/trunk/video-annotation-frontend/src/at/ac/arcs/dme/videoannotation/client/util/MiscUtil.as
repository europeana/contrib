package at.ac.arcs.dme.videoannotation.client.util
{
	import flash.external.ExternalInterface;
	
	import mx.collections.ArrayCollection;
	import mx.formatters.DateFormatter;

    /**
     * Utility class
     * 
     * @author Stefan Pomajbik
     **/
	public class MiscUtil {

		// takes a date represented in ISO 8601 format, does some convertations
		// and parses it by using the Date.parse() function to return an according
		// Date object.
	    private static function isoToDate(value:String):Date {
	    	var dateStr:String = value;
	        dateStr = dateStr.replace(/-/g, "/");
	        dateStr = dateStr.replace("T", " ");
	        dateStr = dateStr.replace("Z", "-0000");
	        dateStr = dateStr.replace("+", " UTC+");
	        dateStr = dateStr.replace("-", " UTC-");
	        var timeZone:String = dateStr.substr(dateStr.indexOf("UTC",0),dateStr.length);
	        timeZone = timeZone.replace(":", "");
	        dateStr = dateStr.substr(0,dateStr.indexOf("UTC",0))+timeZone;
	        return new Date(Date.parse(dateStr));
	    }

	    // converts the date of a Date object into an ISO 8601 formatted (UTC timezoned) string representation
	    private static function dateToIso(date:Date):String {
	       	var string:String = date.getFullYear()+"-";
	    	((date.getUTCMonth())+1<10) ? string +="0"+(date.getUTCMonth()+1) : string += (date.getUTCMonth()+1) ;
	    	string += "-";
	    	(date.getUTCDate()<10) ? string +="0"+date.getUTCDate() : string += date.getUTCDate() ;
	    	string += "T";
	    	(date.getUTCHours()<10) ? string +="0"+date.getUTCHours() : string += date.getUTCHours() ;
	    	string += ":";
	    	(date.getUTCMinutes()<10) ? string +="0"+date.getUTCMinutes() : string += date.getUTCMinutes() ;
	    	string += ":";
	    	(date.getUTCSeconds()<10) ? string +="0"+date.getUTCSeconds() : string += date.getUTCSeconds() ;
			string += "Z";

			return string;
		}

		Date.prototype.getFormatedString = function (format:String):String {
			var dFormatter:DateFormatter = new DateFormatter();
			dFormatter.formatString = format;
			return dFormatter.format(this);
		}

		public static function formatDate(date:Date,form:String):String {
			var dFormatter:DateFormatter = new DateFormatter();
			dFormatter.formatString = form;
			return dFormatter.format(date);
		}

		public static function getHtmlParameter(key:String):String {
	    	var value:String;
	    	var uparam:String = ExternalInterface.call("window.location.search.toString");
	    	if(uparam==null) return null;

		    var paramArray:ArrayCollection = new ArrayCollection(uparam.split('&'));
		    for(var x:int=0; x<paramArray.length; x++) {
		    	var split:Array = paramArray[x].split("=");
		    	var tempKey:String = split[0];
		    	if (x == 0) tempKey=tempKey.substr(1,tempKey.length-1);
		    	if (tempKey == key) return split[1];
		    }
		    return null;
		}
		
		public static function isAudio():Boolean {
			return getHtmlParameter("audio") != null && getHtmlParameter("audio")=="true" || 
				getHtmlParameter("objectURL").search(/.mp3\z/)>-1;
		}
    }
}
