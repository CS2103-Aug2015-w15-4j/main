package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Log {
	public static final int TABNUM = GUI.LOG; // for the array check
	public static final String TABNAME = GUI.tabNames[TABNUM]; 
	public static final int WIDTH = GUI.TABPANE_WIDTH;
	public static final int PADDING = 8;
	
	public static final ScrollBarPolicy V_POLICY = ScrollBarPolicy.AS_NEEDED;
	public static final ScrollBarPolicy H_POLICY = ScrollBarPolicy.NEVER;
	public static final Pos ALIGNMENT = Pos.BOTTOM_LEFT;
	public static final String ID_VBOX = "logVbox";
	public static final String ID_SCROLL = "logScroll";
	
	protected VBox vbox;
	protected ScrollPane sp;
	protected TextFlow textbox; // the storage for the log
	
	public Log() {
		textbox = new TextFlow();
		vbox = new VBox();
		vbox.setMinWidth(WIDTH);
		vbox.setMaxWidth(WIDTH);
		vbox.setAlignment(ALIGNMENT);
		vbox.setId(ID_VBOX);
		vbox.getChildren().add(textbox);
		sp = new ScrollPane(vbox);
		sp.setPadding(new Insets(PADDING));
		sp.setFitToHeight(true);
		sp.setVbarPolicy(V_POLICY);
		sp.setHbarPolicy(H_POLICY);
		sp.setId(ID_SCROLL);
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Node getNode() { 
		return sp;
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
