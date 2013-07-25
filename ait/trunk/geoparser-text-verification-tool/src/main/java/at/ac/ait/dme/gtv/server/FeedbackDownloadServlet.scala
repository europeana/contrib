package at.ac.ait.dme.gtv.server

import xml.Elem
import at.ac.ait.dme.gtv.client.model.FeedbackEntry
import at.ac.ait.dme.gtv.client.model.FeedbackEntry.FeedbackType
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import java.net.URLDecoder

/**
 * Simple servlet to downlad feedback passed via a URL parameter as XML
 *
 * @author Manuel Bernhardt
 */

class FeedbackDownloadServlet extends HttpServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {

    resp.setCharacterEncoding("UTF-8")
    resp.setContentType("application/xml")

    // parse query parameters
    val feedback = req.getParameter("feedback")
    if(feedback.length < 1) {
      resp.getWriter.append(<feedback></feedback>.toString)
      resp.getWriter.flush
    } else {
      val feedbackList = for (el <- URLDecoder.decode(feedback).split(';')) yield new FeedbackEntry(
        FeedbackType.valueOf(el.split('@')(0)),
        el.split('@')(1),
        Integer.parseInt(el.split('@')(2)))

          val xml:Elem =
          <feedback>
            { for (f <- feedbackList) yield
            <location>
              <name>{f.getText}</name>
              <errorType>{f.getType.name}</errorType>
              <offset>{f.getOffset}</offset>
            </location>
            }
          </feedback>

      resp.getWriter.append(xml.toString)
      resp.getWriter.flush
    }
  }
}