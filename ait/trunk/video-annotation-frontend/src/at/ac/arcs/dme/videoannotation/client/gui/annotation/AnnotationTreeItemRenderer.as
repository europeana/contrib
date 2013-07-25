package at.ac.arcs.dme.videoannotation.client.gui.annotation
{
	import flash.display.DisplayObject;
	
	import mx.controls.Label;
	import mx.controls.treeClasses.*;
	import mx.controls.Image;
	import mx.events.FlexEvent;
	
	import at.ac.arcs.dme.videoannotation.client.model.VideoAnnotation;

    /** 
    * Renderer for the annotation tree
    * 
    * @author Christian Sadilek 
    * 
    */
	public class AnnotationTreeItemRenderer extends TreeItemRenderer {	
	 	private var annotationTreeNode:AnnotationTreeNode = new AnnotationTreeNode();	         

		override public function set data(value:Object):void {
			super.data = value;					
	        validateNow();
		}
	
        override protected function createChildren():void {       
        	 super.createChildren();
                          
             removeChild(DisplayObject(label));          
             addChild(annotationTreeNode);        	             	    
        }
        

	    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {			
			super.updateDisplayList(unscaledWidth, unscaledHeight);			
			annotationTreeNode.annotationData = VideoAnnotation(super.data);
			
			var startx:Number = data ? TreeListData(super.listData).indent : 0;
			
			if (disclosureIcon) {
				disclosureIcon.x = startx;
				disclosureIcon.setActualSize(disclosureIcon.width,disclosureIcon.height);
				disclosureIcon.visible = data ?TreeListData(super.listData).hasChildren : false;
				startx = disclosureIcon.x + disclosureIcon.width;
			}									
									
			annotationTreeNode.x = startx;
			annotationTreeNode.y = (unscaledHeight - annotationTreeNode.height);
			annotationTreeNode.setActualSize(unscaledWidth - startx, measuredHeight);	
			annotationTreeNode.styleName="annotationTree";			
			
			if (disclosureIcon)
				disclosureIcon.y = (unscaledHeight - annotationTreeNode.height);								
		}
		
		override protected function measure():void {			
			measuredHeight = annotationTreeNode.getExplicitOrMeasuredHeight();
		}
	}
}
