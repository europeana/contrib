package at.ait.dme.yuma.suite.apps.map.client.annotation;

import java.util.Collection;

import org.gwt.mosaic.ui.client.MessageBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.MediaViewer;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation.MediaType;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationService;
import at.ait.dme.yuma.suite.apps.core.shared.server.annotation.AnnotationServiceAsync;
import at.ait.dme.yuma.suite.apps.image.core.client.treeview.ImageAnnotationEditForm;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageFragment;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.GeoPoint;
import at.ait.dme.yuma.suite.apps.map.client.TileBasedImageViewer;
import at.ait.dme.yuma.suite.apps.map.client.treeview.MapAnnotationTreeNode;

public class MapAnnotationPanel extends AnnotationPanel {
	
	public MapAnnotationPanel(MediaViewer mediaViewer) {
		super(mediaViewer,
				new ImageAnnotationEditForm(
					MediaType.MAP, ((TileBasedImageViewer) mediaViewer).getTagCloud()),
				new MapAnnotationTreeNode());	
	}
	
	public MapAnnotationPanel(MediaViewer mediaViewer, 
			AnnotationEditForm editForm, AnnotationTreeNode treeNode) {

		super(mediaViewer, editForm, treeNode);
	}
	
	@Override
	protected void loadAnnotations() {
		AnnotationServiceAsync imageAnnotationService = (AnnotationServiceAsync) GWT
				.create(AnnotationService.class);

		imageAnnotationService.listAnnotations(YUMACoreProperties.getObjectURI(),
			new AsyncCallback<Collection<Annotation>>() {
				public void onFailure(Throwable t) {
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					MessageBox.error(errorMessages.error(), errorMessages.failedToReadAnnotations() + " (" + t.getMessage() + ")");
				}

				public void onSuccess(Collection<Annotation> foundAnnotations) {
					annotationTree.removeItems();
					
					for (Annotation a : foundAnnotations) {
						if (!isControlPoint(a)) {
							annotationTree.addAnnotation(a);
						}
					}
					
					treePanel.add(annotationTree);				
					disableLoadingImage();
					layout();
				}
			});	
	}
	
	protected boolean isControlPoint(Annotation a) {
		if (!a.hasFragment())
			return false;
		
		if (!(a.getFragment() instanceof ImageFragment))
			return false;
		
		ImageFragment f = (ImageFragment) a.getFragment();
		if (f.getShape() instanceof GeoPoint)
			return true;
		
		return false;
	}

}
