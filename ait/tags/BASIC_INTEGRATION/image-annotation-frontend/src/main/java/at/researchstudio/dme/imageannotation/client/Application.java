package at.researchstudio.dme.imageannotation.client;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationSearchResultComposite;
import at.researchstudio.dme.imageannotation.client.image.ImageComposite;
import at.researchstudio.dme.imageannotation.client.image.StandardImageComposite;
import at.researchstudio.dme.imageannotation.client.image.TiledImageComposite;
import at.researchstudio.dme.imageannotation.client.server.AuthenticationService;
import at.researchstudio.dme.imageannotation.client.server.AuthenticationServiceAsync;
import at.researchstudio.dme.imageannotation.client.user.User;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * TODO "goto fragment" function in case the fragment lies outside the
 * visible rect due to dragging or a too large image. 
 * 
 * TODO support rating and/or alerting of offensive content
 * 
 * TODO paging for the search results table and annotation tree
 *  
 */
public class Application implements EntryPoint {
	private static final String LEMO_COOKIE_NAME = "lemo_user";
	private static User authenticatedUser = null;

	private ImageComposite imageComposite = null;
	private ImageAnnotationComposite annotationComposite = null;
	
	public Application() {}

	/**
	 * only used by unit tests to create an image composite w/o an annotation composite
	 * 
	 * @param imageUrl
	 */
	public Application(String imageUrl) {
		showImage(imageUrl);		
	}
	
	/**
	 * load the module
	 */
	public void onModuleLoad() {
		String imageUrl = getRequestParameterValue("objectURL");
		showHeader();

		showImage(imageUrl); 
		
		// the image has to be fully loaded before we can show the annotations
		// otherwise possible fragments can not be properly displayed
		imageComposite.addLoadHandler(new LoadHandler() {		
			public void onLoad(LoadEvent event) {
				// first we authenticate the user either using the provided
				// user name or the secure authentication token.				
				String userName = getRequestParameterValue("user");
				if(userName!=null&&!userName.isEmpty()) {
					setAuthenticatedUser(new User(userName));
					showAnnotations();
					return;
				}
				
				String authToken = getRequestParameterValue("authToken");
				String appSign = getRequestParameterValue("appSign");
				if(authToken == null || appSign == null) {
					// create a non-privileged user
					setAuthenticatedUser(new User());
					showAnnotations();
					return;
				}
				
				AuthenticationServiceAsync authService = (AuthenticationServiceAsync) GWT
						.create(AuthenticationService.class);

				authService.authenticate(authToken, appSign, new AsyncCallback<User>() {
					public void onFailure(Throwable caught) {
						ErrorMessages errorMessages=(ErrorMessages)GWT.create(ErrorMessages.class);
						Window.alert(errorMessages.failedToAuthenticate());
					}
					public void onSuccess(User user) {
						setAuthenticatedUser(user);						 
						showAnnotations();
					}
				});					
			}			
		});		
	}

	/**
	 * show the tiled image composite
	 * 
	 * @param imageUrl
	 */
	private void showImage(String imageUrl) {
		if(getRequestParameterValue("tileView")!=null) {
			imageComposite = new TiledImageComposite(imageUrl);
		} else {
			imageComposite = new StandardImageComposite(imageUrl);
		}
		RootPanel.get().add(imageComposite, 10, 80);
	}		

	/**
	 * show the annotation composite
	 */
	private void showAnnotations() {
		annotationComposite = new ImageAnnotationComposite(imageComposite);
		RootPanel.get().add(annotationComposite, 500, 100);
		if(imageComposite!=null)
			imageComposite.setImageFragmentSelectionListener(annotationComposite);
	}

	/**
	 * show the header (search field and logo)
	 */
	private void showHeader() {
		AnnotationConstants annotationConstants=
			(AnnotationConstants)GWT.create(AnnotationConstants.class);	
		
		final HorizontalPanel hPanel = new HorizontalPanel();
		
		// show the search field		
		final TextBox searchTerm = new TextBox();
		searchTerm.setStyleName("imageAnnotation-search-term");
		searchTerm.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER)
					startSearch(searchTerm.getText());
			}
		});
		hPanel.add(searchTerm);
		
		// show the search button
		Button search = new Button(annotationConstants.annotationSearch());
		search.setStyleName("imageAnnotation-search");
		search.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				startSearch(searchTerm.getText());
			}	
		});
		hPanel.add(search);

		// show the logo
		String dbName = getDatabaseName();
		if(dbName==null) dbName = "econnect";
		String logoPath = "images/"+dbName+".gif";
		final Image logo = new Image(logoPath);
		// workaround for IE caching issue see 
		// http://groups.google.com/group/Google-Web-Toolkit/browse_thread/thread/11851753ba99454d/6079e2e2b9aea4bf
		DOM.setElementAttribute(logo.getElement(), "src", logoPath);
		
		// see also http://code.google.com/p/google-web-toolkit/issues/detail?id=2149
		if(logo.getWidth()>0) {
			RootPanel.get().add(hPanel,logo.getWidth()+20,50);			
		} else {			
			logo.addLoadHandler(new LoadHandler() {
				public void onLoad(LoadEvent event) {
					RootPanel.get().add(hPanel,logo.getWidth()+20,50);					
				}				
			});
		}
		RootPanel.get().add(logo,10,10);		
	}
	
	/**
	 * display the search result composite in a popup panel
	 * 
	 * @param searchTerm
	 */
	private void startSearch(String searchTerm) {
		PopupPanel popup = new PopupPanel();
		popup.add(new ImageAnnotationSearchResultComposite(searchTerm));
		popup.setPopupPosition(10, 10);
		popup.show();
		popup.setStyleName("imageAnnotation-popup-search");
	}
	
	/**
	 * returns the image composite
	 * 
	 * @return image composite
	 */
	public ImageComposite getImageComposite() {
		return imageComposite;
	}

	/**
	 * returns a request parameter. the initial request parameters provided to the 
	 * entry point are stored as javascript variables and are therefore accessible 
	 * through the dictionary. see also annotate.jsp.
	 * 
	 * @param parameterName
	 * @return parameter value
	 */
	private static String getRequestParameterValue(String parameterName) {
		String parameterValue;
		try {
			Dictionary parameters = Dictionary.getDictionary("parameters");
			parameterValue = parameters.get(parameterName);
			if (parameterValue.equals("null"))
				parameterValue = null;
		} catch (Exception e) {
			return null;
		}
		return parameterValue;
	}
	
	/**
	 * returns the provided name of the user 
	 * 
	 * @return user name
	 */
	public static String getUser() {
		String userName = authenticatedUser.getName();
		if(!userName.equalsIgnoreCase(Cookies.getCookie(LEMO_COOKIE_NAME))) {
			Cookies.setCookie(LEMO_COOKIE_NAME, userName, null, null, "/", false);
		}
		return userName;
	}
	
	/**
	 * set the authenticated user and create a cookie
	 * 
	 * @param user
	 */
	public static void setAuthenticatedUser(User user) {
		Cookies.setCookie(LEMO_COOKIE_NAME, user.getName(), null, null, "/", false);				
		authenticatedUser = user;
	}
	/**
	 * check if the given user is the currently authenticated user
	 * 
	 * @param user
	 * @return true if user is authenticated, otherwise false
	 */
	public static boolean isAuthenticatedUser(String user) {
		if(user==null || getUser()==null) return false;			
		return getUser().equalsIgnoreCase(user);
	}
	
	/**
	 * return the url to the image
	 * 
	 * @return image url
	 */
	public static String getImageUrl() {
		String objectUrl=getRequestParameterValue("objectURL");
		if(objectUrl==null)
			objectUrl=getRequestParameterValue("imageURL");
		return objectUrl;
	}
	
	/**
	 * returns the optional provided id of an external object that
	 * the annotated image is linked to.
	 * 
	 * @return id of the external object
	 */
	public static String getExternalObjectId() {
		return getRequestParameterValue("id");
	}
	
	/**
	 * returns the provided name of the database to use
	 * 
	 * @return database name
	 */
	public static String getDatabaseName() {
		return getRequestParameterValue("db");	
	}

	/**
	 * returns the base URL
	 * 
	 * @return base URL
	 */
	public static String getBaseUrl() {
		return getRequestParameterValue("baseURL");	
	}
	
	/**
	 * returns the bounding box 
	 * 
	 * @return base URL
	 */
	public static String getBbox() {
		return getRequestParameterValue("bbox");	
	}
	
	/**
	 * reload the application
	 */
	public static native void reload() /*-{
     	$wnd.location.reload();
  	}-*/;
	
	/**
	 * ideally you should never need this
	 * 
	 * @return user agent string
	 */
	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;
}
