/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.client;

import org.gwt.mosaic.ui.client.DecoratedTabLayoutPanel;
import org.gwt.mosaic.ui.client.TabLayoutPanel;
import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.client.image.ImageComposite;
import at.ait.dme.yuma.client.image.StandardImageComposite;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationComposite;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationSearchResultComposite;
import at.ait.dme.yuma.client.image.annotation.StandardImageAnnotationForm;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionEvent;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionHandler;
import at.ait.dme.yuma.client.image.shape.ShapeTypeRegistry;
import at.ait.dme.yuma.client.map.TiledImageComposite;
import at.ait.dme.yuma.client.map.annotation.ControlPointComposite;
import at.ait.dme.yuma.client.map.annotation.ControlPointForm;
import at.ait.dme.yuma.client.map.annotation.TagEnabledMapAnnotationForm;
import at.ait.dme.yuma.client.map.explore.ExplorationComposite;
import at.ait.dme.yuma.client.server.AuthenticationService;
import at.ait.dme.yuma.client.server.AuthenticationServiceAsync;
import at.ait.dme.yuma.client.server.WebsiteCaptureService;
import at.ait.dme.yuma.client.server.WebsiteCaptureServiceAsync;
import at.ait.dme.yuma.client.server.exception.TilesetGenerationException;
import at.ait.dme.yuma.client.server.exception.TilesetNotFoundException;
import at.ait.dme.yuma.client.user.User;
import at.ait.dme.yuma.client.util.LoadMask;
import at.ait.dme.yuma.client.util.MinMaxWindowPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the entry point to the application.
 *  
 * TODO "goto fragment" function in case the fragment lies outside the
 * visible rect due to dragging. 
 * 
 * TODO support rating
 * 
 * TODO paging for the search results table and annotation tree
 *  
 * @author Christian Sadilek
 */
public class Application implements EntryPoint {
	private static final String LEMO_COOKIE_NAME = "lemo_user";
	private static User authenticatedUser = null;
	private static AnnotationConstants annotationConstants = null;
	
	private ImageComposite imageComposite = null;
	
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
	 * load the module and initialize the application
	 */
	public void onModuleLoad() {
		String imageUrl = getRequestParameterValue("objectURL");
		String htmlUrl = getRequestParameterValue("htmlURL");
		
		if(htmlUrl==null) {
			initApplication(imageUrl);
		} else {
			final LoadMask loadMask = new LoadMask("Loading...");
			loadMask.show();
		
			WebsiteCaptureServiceAsync captureService = (WebsiteCaptureServiceAsync) GWT
					.create(WebsiteCaptureService.class);
			captureService.captureSite(htmlUrl, new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
					Window.alert(errorMessages.imageNotFound());
				}

				public void onSuccess(String imageUrl) {
					loadMask.hide();
					initApplication(imageUrl);
				}
			});
		}
	}
		
	
	private void initApplication(String imageUrl) {
		showHeader();		
		
		showImage(imageUrl); 
		
		// the image has to be completely loaded before we can show the annotations
		// otherwise possible fragments can not be displayed properly
		imageComposite.addLoadHandler(new LoadHandler() {
			public void onLoad(LoadEvent events) {
				// first we authenticate the user by either using the provided
				// user name or the secure authentication token.	
				String userName = getRequestParameterValue("user");
				if(userName!=null&&!userName.trim().isEmpty()) {
					// TODO deactivate this if you ever go into production					
					setAuthenticatedUser(new User(userName));
					showAnnotations();
					return;
				}
				
				String authToken = getRequestParameterValue("authToken");
				String appSign = getRequestParameterValue("appSign");
				
				AuthenticationServiceAsync authService = (AuthenticationServiceAsync) GWT
						.create(AuthenticationService.class);
				authService.authenticate(authToken, appSign, new AsyncCallback<User>() {
					public void onFailure(Throwable caught) {
						ErrorMessages errorMessages=(ErrorMessages)GWT.create(ErrorMessages.class);
						Window.alert(errorMessages.failedToAuthenticate());
						// create non-privileged user to use read-only mode.
						setAuthenticatedUser(new User());
						showAnnotations();
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
	 * show the image composite
	 * 
	 * @param imageUrl
	 * @throws TilesetGenerationException 
	 * @throws TilesetNotFoundException 
	 */
	private void showImage(String imageUrl) {
		if (Application.isInTileMode()) {									
			imageComposite = new TiledImageComposite(imageUrl);
			RootPanel.get().add(imageComposite, 0, 0);
		} else {
			imageComposite = new StandardImageComposite(imageUrl);
			RootPanel.get().add(imageComposite, 10, 80);
		}
	}		

	/**
	 * show the annotation composite
	 */
	private void showAnnotations() {
		// Create a floating window
		final WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(500, 50, 430, 600);
		window.show();
		window.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// we do not allow to close this window
				window.show();
			}
		});
		
		// Parent tab panel 
		TabLayoutPanel tabPanel = new DecoratedTabLayoutPanel();
		tabPanel.setPadding(0);
		showAnnotationsTab(tabPanel);
		if(Application.isInTileMode()) {
			showGeoReferencingTab(tabPanel);
			showExplorationTab(tabPanel);
			tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
				
				TiledImageComposite tic = (TiledImageComposite) imageComposite;
				@Override
				public void onSelection(SelectionEvent<Integer> event) {
					if (event.getSelectedItem().intValue() == 0) {
						// Annotations tab
						tic.showAnnotationLayer();
					} else if (event.getSelectedItem().intValue() == 1) {
						// Georeferencing tab
						tic.showControlPointLayer();
					} else if (event.getSelectedItem().intValue() == 2) {
						// Exploration tab
					}
				}
			});
		}
		window.setWidget(tabPanel);
	}
	
	/**
	 * show annotations tab
	 * 
	 * @param tabPanel
	 */
	private void showAnnotationsTab(TabLayoutPanel tabPanel) {
		ImageAnnotationComposite annComposite;
	
		if (Application.isInTileMode()) {
			annComposite = new ImageAnnotationComposite(
					imageComposite,
					new TagEnabledMapAnnotationForm(((TiledImageComposite)imageComposite).
							getAnnotationLayer()),
					ShapeTypeRegistry.allTypes());
		} else {
			annComposite = new ImageAnnotationComposite(imageComposite, 
					new StandardImageAnnotationForm(), 
					ShapeTypeRegistry.allTypes());			
		}

		annComposite.addImageAnnotationSelectionHandler(new ImageAnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(ImageAnnotationSelectionEvent event) {
				imageComposite.selectFragment(event.getAnnotation(), event.isSelected());
			}
		});
		tabPanel.add(annComposite, getConstants().tabAnnotations());
	}
	
	/**
	 * show georeferencing tab
	 * 
	 * @param tabPanel
	 */
	private void showGeoReferencingTab(TabLayoutPanel tabPanel) {
		ImageAnnotationComposite geoRefComposite = new ControlPointComposite(
				(TiledImageComposite)imageComposite, 
				new ControlPointForm(((TiledImageComposite)imageComposite).getControlPointLayer()), 
				ShapeTypeRegistry.geoTypes());
		
		geoRefComposite.addImageAnnotationSelectionHandler(new ImageAnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(ImageAnnotationSelectionEvent event) {
				imageComposite.selectFragment(event.getAnnotation(), event.isSelected());
			}
		});
				
		tabPanel.add(geoRefComposite, getConstants().tabGeoReferencing());
	}
	
	/**
	 * show exploration tab
	 * 
	 * @param tabPanel
	 */
	private void showExplorationTab(TabLayoutPanel tabPanel) {
		ExplorationComposite expComposite = new ExplorationComposite((TiledImageComposite)imageComposite);
		tabPanel.add(expComposite, getConstants().tabExploration());
	}

	/**
	 * show the header (search field and logo)
	 */
	private void showHeader() {
		if (Application.isInTileMode()) return; 
			
		final HorizontalPanel hPanel = new HorizontalPanel();
		
		// removed the search since it's not needed by europeana
		// show the search field		
		/*final TextBox searchTerm = new TextBox();
		searchTerm.setStyleName("imageAnnotation-search-term");
		searchTerm.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER)
					startSearch(searchTerm.getText());
			}
		});
		hPanel.add(searchTerm);
		
		// show the search button
		Button search = new Button(getConstants().annotationSearch());
		search.setStyleName("imageAnnotation-search");
		search.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				startSearch(searchTerm.getText());
			}	
		});
		hPanel.add(search);*/

		// show the logo
		String dbName = getDatabaseName();
		if(dbName==null) dbName = "ait";
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
		WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(10, 10, 1000, 400);
		window.setCaption("Search");
		window.setHideContentOnMove(false);
		window.setWidget(new ImageAnnotationSearchResultComposite(searchTerm));
		window.show();
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
		return getUser().equalsIgnoreCase(user) || authenticatedUser.isAdmin();
	}
	
	/**
	 * authenticate admin user as user provided
	 * 
	 * @param user
	 */
	public static void authenticateAs(String user) {
		if(authenticatedUser.isAdmin()) {
			Cookies.setCookie(LEMO_COOKIE_NAME, user, null, null, "/", false);							
		}
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
		if(objectUrl==null)
			objectUrl=getRequestParameterValue("htmlURL");
		
		return objectUrl;
	}
	
	/**
	 * return the flagged id used in admin mode
	 * 
	 * @return annotation id
	 */
	public static String getFlaggedId() {
		return getRequestParameterValue("flaggedId");
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
	 * check for tile mode
	 * 
	 * @return true if in tile mode, otherwise false
	 */
	public static boolean isInTileMode() {
		return (getRequestParameterValue("tileView") != null);
		
	}
	/**
	 * returns the internationalized constants of this app
	 * 
	 * @return constants
	 */
	public static AnnotationConstants getConstants() {
		if(annotationConstants==null)
			annotationConstants=(AnnotationConstants)GWT.create(AnnotationConstants.class);
		return annotationConstants;
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
