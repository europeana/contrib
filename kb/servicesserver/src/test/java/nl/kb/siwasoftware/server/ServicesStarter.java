package nl.kb.siwasoftware.server;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * @author Gerald de Jong, Beautiful Code BV, <geralddejong@gmail.com>
 */

public class ServicesStarter {

    public static void main(String... args) throws Exception {
        Server server = new Server(8080);
        server.addHandler(new WebAppContext("src/main/webapp", "/api/siwa"));
        server.start();
    }
}
