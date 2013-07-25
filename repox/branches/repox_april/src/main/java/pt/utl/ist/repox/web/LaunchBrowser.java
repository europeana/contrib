package pt.utl.ist.repox.web;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LaunchBrowser {
	public static void main(String[] args) {
		try {
			Desktop.getDesktop().browse(new URI("http://localhost:8080/repox"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
