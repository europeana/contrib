package gr.ntua.ivml.mint.test;

public class TestTrimTab {
	public static void main(String[] args) {
		String a = "Type A";
		String b = "Type\tA";
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(a.trim());
		System.out.println(b.trim());
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<map value=\"" + a + "\">" + b.trim() + "</map>");
		
		System.out.println(buffer.toString());
	}
}
