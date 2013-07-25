package at.ac.arcs.dme.videoannotation.client.controller
{
    import at.ac.arcs.dme.videoannotation.client.command.AnnotationCommand;
    import at.ac.arcs.dme.videoannotation.client.event.AnnotationEvent;
    import at.ac.arcs.dme.videoannotation.client.event.ApplicationEvent;
    import at.ac.arcs.dme.videoannotation.client.event.RangeEvent;
    import at.ac.arcs.dme.videoannotation.client.event.UserEvent;
    import at.ac.arcs.dme.videoannotation.client.gui.annotation.AnnotationCanvas;
    import at.ac.arcs.dme.videoannotation.client.gui.annotation.AnnotationForm;
    import at.ac.arcs.dme.videoannotation.client.gui.debug.DebugBox;
    import at.ac.arcs.dme.videoannotation.client.gui.video.AbstractPlayer;
    import at.ac.arcs.dme.videoannotation.client.gui.video.FragmentBarList;
    import at.ac.arcs.dme.videoannotation.client.util.HTTPParameters;
    
    import mx.core.Application;
    import mx.core.UIComponent;
    
    /**
     * The AnnotationController binds the events triggered by users or by the application to the various graphical components.
     * Note that not all event listeners are defined here, additional listeners are defined in the videoannotation class too.
     * 
     * @author Manuel Gay
     */
    public class AnnotationController extends UIComponent
    {

        static private var __instance:AnnotationController = null;
        
        static public function getInstance():AnnotationController
        {
            if (__instance == null) {
                __instance = new AnnotationController();
            }
            return __instance;
        }
            
        private var command:AnnotationCommand;
        private var application:Application;
        private var debugBox:DebugBox;
        
        private function listen(key:String, listener:Function):void {
            this.application.systemManager.addEventListener(key, listener);
        }
        
        
        /**
         * Initializes the controller (thic class), which takes care of mapping events to actions.
         * The controller also directly listens to a number of events which are business events and are handled by the AnnotationCommand
         */
        public function initController(application:Application, debugBox:DebugBox, urlParams:HTTPParameters):void {
            this.application = application;
            this.debugBox = debugBox;
            
            this.command = new AnnotationCommand(application, debugBox, urlParams);
            
            // the controller centralises the core (business) events
            listen(UserEvent.CLICK_REPLY_ANNOTATION, command.on_replyAnnotation);
            listen(UserEvent.CLICK_EDIT_ANNOTATION, command.on_editAnnotation);
            listen(UserEvent.CLICK_DELETE_ANNOTATION, command.on_deleteAnnotation);
            listen(AnnotationEvent.NEW, command.handleNewAnnotation);
		    listen(AnnotationEvent.SAVE, command.handleSaveAnnotation);
		    listen(ApplicationEvent.LIST_ANNOTATION, command.handleListAnnotation);
		    listen(AnnotationEvent.GET_ANNOTATION_ENRICHEMENT_DATA, command.handleGetAnnotationEnrichmentData);

        }
            
            
        /**
         * Initializes the form which takes care of:
         * - changing the selected range value when the slider is moved (CHANGE_PLAYER_RANGE)
         * - loading an annotation (on new, edit, reply) and displaying itself accordingly
         **/
        public function initAnnotationForm(form:AnnotationForm):void {
            listen(RangeEvent.CHANGE_PLAYER_RANGE, form.handleUpdateRange);
            listen(AnnotationEvent.LOAD_ANNOTATION, form.handleLoadAnnotation);
            listen(AnnotationEvent.LOAD_ANNOTATION_ENRICHMENT_DATA, form.handleLoadAnnotationEnrichmentData);
        }
        
        /**
         * Initalizes the annotation canvas which takes care of:
         * - switching to the right tool for drawing shapes, depending on the user selection
         * - updating the color of the shape being created or modified
         * - displaying the shapes while playing the video or video fragments
         * - clearing itself when no shapes should be displayed (e.g. video playback stopped)
         * - updating itself when new annotation data is available, for correct rendering of the shapes
         * - switching to a "read only" mode when not in edit/create mode
         * - allowing shapes to be modified and moved around on the canvas when in edit/create mode 
         **/
        public function initAnnotationCanvas(canvas:AnnotationCanvas):void {
            
            // user events
            listen(UserEvent.SELECT_NO_SHAPE_TOOL, canvas.on_annotationToolSelected);
		    listen(UserEvent.SELECT_RECTANGLE_SHAPE_TOOL, canvas.on_annotationToolSelected);
		    listen(UserEvent.SELECT_ELLIPSE_SHAPE_TOOL, canvas.on_annotationToolSelected);
		    listen(UserEvent.SELECT_ANNOTATION_COLOR, canvas.on_changeAnnotationToolColor);
		    listen(UserEvent.CLICK_DELETE_ANNOTATION, canvas.handleCanvasClear);
		    
		    // interface events
		    listen(AnnotationEvent.SHAPE_READONLY, canvas.handleCanvasReadonlyEvent);
		    listen(ApplicationEvent.STOP, canvas.handleVideoStop);
		    listen(ApplicationEvent.START, canvas.handleCanvasClear);
		    listen(AnnotationEvent.SHAPE_NEW, canvas.handleNewShape);
		    listen(AnnotationEvent.SHAPE_EDIT, canvas.handleEditShape);
            
            // application events
            listen(ApplicationEvent.LOAD_DATA, canvas.handleLoadData);
        }
        
        /**
         * Initializees the player which takes care of:
         * - playing a fragment when the button is clicked
         * - highlighting the range slider when necessary
         * - changing the range slider start & end when necessary
         * - displaying a single frame on request (e.g. when editing a specific annotation)
         **/
        public function initPlayer(player:AbstractPlayer):void {
            listen(UserEvent.CLICK_PLAY_FRAGMENT, player.handlePlayFragment);
            listen(UserEvent.SELECT_ANNOTATION_TREE, player.handleUpdateRangeSelector);
		    listen(UserEvent.SELECT_ANNOTATION_FRAGMENTBAR, player.handleUpdateRangeSelector);
		    listen(UserEvent.CLICK_NEW_ANNOTATION, player.handleDisplayFrame);
		    listen(AnnotationEvent.DISPLAY_FRAME, player.handleDisplayFrame);
		    listen(RangeEvent.CHANGE_FORM_RANGE, player.handleUpdateRangeSelector);
		    listen(RangeEvent.CHANGE_PLAYER_RANGE, player.handleUpdateRangeSelector); // TODO really needed here?
        }
        
        /**
         * Initializes the fragment bar, which takes care of:
         * - highlighting itself when a fragment is selected
         **/
        public function initFragmentBarList(list:FragmentBarList):void {
            listen(UserEvent.SELECT_ANNOTATION_FRAGMENTBAR, list.on_selectFragmentTree);
        }
    }
}