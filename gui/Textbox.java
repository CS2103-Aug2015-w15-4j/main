package gui;

import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class Textbox {
	public final static String TAG = GUIController.TAG_SIDEBAR;
	public final static String TAG_TEXTBOX = GUIController.TAG_SIDEBAR_TEXTBOX;
	public final static String AVATAR_IMAGE = GUIController.AVATAR_IMAGENAME;
	public final static String QUOTE_IMAGE = "quote.png";
	public final static String WELCOME_MESSAGE = "Welcome!";
	
	public final static int PADDING = 10;
	public final static Pos ORIENTATION = Pos.CENTER_RIGHT;

	//public int imageHeight = WIDTH;
	//public int textboxHeight = HEIGHT - imageHeight - 20;
	
	protected TextFlow textflow; // list of messages from application
	protected Label label;
	protected HBox hbox;
	protected Image avatar;
	protected ImageView avatarView;
	protected Image quote = null;
	protected ImageView quoteView;

	public Textbox() {
		hbox = new HBox();
		label = new Label(WELCOME_MESSAGE);
		label.prefWidthProperty().bind(hbox.widthProperty());
		label.prefHeightProperty().bind(hbox.heightProperty());
		label.setId(TAG_TEXTBOX);
		//quote = new Image(Sidebar.class.getResourceAsStream(QUOTE_IMAGE), 
		//		WIDTH-PADDING, textboxHeight, false, true);
		label.setTextAlignment(TextAlignment.LEFT);
		StackPane stack = new StackPane();
		if (quote!=null) {
			quoteView = new ImageView(quote);
			stack.getChildren().add(quoteView);
			StackPane.setAlignment(quoteView, ORIENTATION);
		}
		stack.getChildren().add(label);
		StackPane.setAlignment(label, ORIENTATION);
		hbox.setPadding(new Insets(PADDING));
		hbox.setSpacing(PADDING);
		hbox.setAlignment(ORIENTATION);
		hbox.getStyleClass().add(TAG);
		hbox.getChildren().add(stack);
		HBox.setHgrow(stack, Priority.ALWAYS);
		/*
		avatar = new Image(Sidebar.class.getResourceAsStream(AVATAR_IMAGE),
				WIDTH, imageHeight, true, true);
		if (avatar!=null) {
			imageHeight = (int) avatar.getHeight();
			avatarView = new ImageView(avatar);
			hbox.getChildren().add(avatarView);
		}//*/
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public HBox getNode() {
		return hbox;
	}
	
	public void addToTextbox(String input) {
		label.setText(input.trim());
	}
}
