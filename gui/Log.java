package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Log {
	public static final int PADDING = 8;
	
	public static final ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	public static final ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	public static final Pos ALIGNMENT = Pos.BOTTOM_LEFT;
	public static final String ID_VBOX = "logVbox";
	public static final String ID_SCROLL = "logScroll";
	
	protected VBox vbox;
	protected ScrollPane sp;
	protected TextFlow textbox; // the storage for the log
	protected VBox master;
	protected Button name;
	
	public Log() {
		textbox = new TextFlow();
		vbox = new VBox();
		vbox.setAlignment(ALIGNMENT);
		vbox.setId(ID_VBOX);
		vbox.getChildren().add(textbox);
		sp = new ScrollPane(vbox);
		vbox.prefWidthProperty().bind(sp.widthProperty());
		vbox.prefHeightProperty().bind(sp.heightProperty());
		
		sp.setFitToHeight(true);
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		sp.setId(ID_SCROLL);
		
		master = new VBox();
		master.setPadding(new Insets(PADDING));
		name = new Button();
		name.setFocusTraversable(false);
		name.setAlignment(Pos.CENTER);
		master.getChildren().add(sp);
		sp.prefWidthProperty().bind(master.widthProperty());
		name.prefWidthProperty().bind(master.widthProperty());
		VBox.setVgrow(sp, Priority.ALWAYS);
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
	 * Set name of the log tab
	 */
	public void setName(String _name) {
		name.setText(_name);
		if (master.getChildren().size()==1) {
			master.getChildren().add(0,name);
		}
	}
	
	public void refresh() {
		sp.snapshot(new SnapshotParameters(), new WritableImage(1, 1));
		sp.setVvalue(sp.getVmax());
	}
	
	public void addToTextbox(String input) {
		textbox.getChildren().add(new Text(">"+input.trim()+"\n"));
		refresh();
	}
}
