package gr.ntua.ivml.awareness.play;

import play.api.mvc.Codec;
import play.core.j.JavaResults;
import play.mvc.*;


public class IEResult implements Result {
    final private play.api.mvc.Result wrappedResult;

    public IEResult(final int StatusCode, final String content, final String contentType) {
        if(content == null) throw new NullPointerException("null content");
        String nct=contentType;
        if(contentType==null){nct="aplication/json";}else{nct=contentType;}
        this.wrappedResult = JavaResults.Status(StatusCode).apply(
                // implement the play.api.mvc.Content interface
                new Content() {
                    @Override public String body() { return content; }
                    @Override public String contentType() { return contentType; }
                },
                JavaResults.writeContent(Codec.utf_8()),
                JavaResults.contentTypeOf(nct));
    }

	@Override
	public play.api.mvc.Result getWrappedResult() {
		return this.wrappedResult;
	}

    
}