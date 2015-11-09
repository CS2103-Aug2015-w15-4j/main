package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

//@author A0122534R
public class Log {
	protected final static int MAXSIZE = 100; 
	// maximum size of the Log
	
	protected final static int PADDING = 8;
	
	protected final static ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	protected final static ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	protected final static Pos ALIGNMENT = Pos.BOTTOM_LEFT;
	protected final static String ID_VBOX = "logVbox";
	protected final static String ID_SCROLL = "logScroll";
	
	protected VBox vbox;
	protected ScrollPane sp;
	protected TextFlow textFlow; // the storage for the log
	protected VBox master;
	protected Label name;
	
	public Log() {
		master = new VBox();
		textFlow = new TextFlow();
		
		master.setPadding(new Insets(PADDING));
		master.getChildren().add(initContainer());
		initHeader();
	}
	
	/**
	 * Creates a log with a name
	 * @param str Name of the tab
	 */
	public Log(String _name) {
		this();
		setName(_name);
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public VBox getNode() { 
		return master;
	}
	
	/**
	 * Initialises the vbox and sp container for the Log
	 * @param vbox
	 * @param sp
	 * @return the initalised parent node of this container
	 */
	protected ScrollPane initContainer() {
		vbox = new VBox();
		vbox.setAlignment(ALIGNMENT);
		vbox.setId(ID_VBOX);
		vbox.getChildren().add(textFlow);
		sp = new ScrollPane(vbox);
		vbox.prefWidthProperty().bind(sp.widthProperty());
		vbox.prefHeightProperty().bind(sp.heightProperty());
		
		sp.setFitToHeight(true);
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		sp.setId(ID_SCROLL);
		sp.prefWidthProperty().bind(getNode().widthProperty());
		VBox.setVgrow(sp, Priority.ALWAYS);
		return sp;
	}
	
	/**
	 * Initialises the header for the Log
	 * @param name
	 */
	protected void initHeader() {
		name = new Label();
		name.getStyleClass().add(GuiController.CSS_STYLE_CURVED_LABEL);
		name.setAlignment(Pos.CENTER);
		name.prefWidthProperty().bind(getNode().widthProperty());
	}
	
	/**
	 * Set name of the log tab
	 */
	public void setName(String _name) {
		name.setText(_name);
		if (master.getChildren().size()==1) {
			master.getChildren().add(0,name);
		}
	}
	
	/**
	 * Resets the scrollpane to the bottom
	 */
	public void refresh() {
		sp.snapshot(new SnapshotParameters(), new WritableImage(1, 1));
		sp.setVvalue(sp.getVmax()); // scroll to the bottom after refresh
	}
	
	/**
	 * Adds a string to the log
	 * @param input String to be added
	 */
	public void addToLog(String input) {
		textFlow.getChildren().add(new Text(">"+input.trim()+"\n"));
		// if size exceed, delete first item
		if (textFlow.getChildren().size()>MAXSIZE) {
			textFlow.getChildren().remove(0);
		}
		refresh();
	}
	
	/**
	 * Gets the stored log data in the form of an iterator
	 */
	public TextFlow getLog() {
		return textFlow;
	}
	
}
