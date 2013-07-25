package at.researchstudio.dme.imageannotation.client.image.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSUtils {
	
	public static JsArray<JavaScriptObject> toJSArray(JavaScriptObject[] array) {
		JsArray<JavaScriptObject> result = newJSArray(array.length);
		for (int i=0; i<array.length; i++) {
			result.set(i, array[i]);
		}
		return result;
	}
	
	public static JavaScriptObject[] toJavaArray(JsArray<JavaScriptObject> array) {
		JavaScriptObject[] result = new JavaScriptObject[array.length()];
		for (int i=0; i<result.length; i++) {
			result[i] = array.get(i);
		}
		return result;
	}
	
	public static native JsArray<JavaScriptObject> newJSArray(int length) /*-{
		return new Array(length);
	}-*/;

}
