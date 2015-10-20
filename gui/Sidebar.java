package gui;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class Sidebar {
	public final static String TAG = GUIController.TAG_SIDEBAR;
	public final static String TAG_TEXTBOX = GUIController.TAG_SIDEBAR_TEXTBOX;
	public final static String AVATAR_IMAGE = GUIController.AVATAR_IMAGENAME;
	public final static String QUOTE_IMAGE = "quote.png";
	public final static String WELCOME_MESSAGE = "Welcome!";
	
	public final static int WIDTH = GUIController.SIDEBAR_WIDTH;
	public final static int HEIGHT = GUIController.WINDOW_HEIGHT;
	public final static int PADDING = 10;
	public final static Pos ORIENTATION = GUIController.SIDEBAR_ORIENTATION;

	public int imageHeight = WIDTH;
	public int textboxHeight = HEIGHT - imageHeight - 20;
	
	
	protected TextFlow textflow; // list of messages from application
	protected Label label;
	protected VBox vbox;
	protected Image avatar;
	protected ImageView avatarView;
	protected Image quote;
	protected ImageView quoteView;

	public Sidebar() {
		label = new Label(WELCOME_MESSAGE);
		label.setId(TAG_TEXTBOX);
		quote = new Image(Sidebar.class.getResourceAsStream(QUOTE_IMAGE), 
				WIDTH-PADDING, textboxHeight, false, true);
		label.setTextAlignment(TextAlignment.CENTER);
		StackPane stack = new StackPane();
		if (quote!=null) {
			quoteView = new ImageView(quote);
			stack.getChildren().add(quoteView);
			StackPane.setAlignment(quoteView, ORIENTATION);
		}
		stack.getChildren().add(label);
		StackPane.setAlignment(label, ORIENTATION);
		vbox = new VBox();
		stack.prefWidthProperty().bind(vbox.widthProperty());
		//stack.maxWidthProperty().bind(vbox.widthProperty());
		vbox.setPadding(new Insets(PADDING));
		vbox.setSpacing(PADDING);
		vbox.setMaxWidth(WIDTH);
		vbox.setAlignment(ORIENTATION);
		vbox.getStyleClass().add(TAG);
		vbox.getChildren().add(stack);
		VBox.setVgrow(stack, Priority.ALWAYS);
		avatar = new Image(Sidebar.class.getResourceAsStream(AVATAR_IMAGE),
				WIDTH, imageHeight, true, true);
		if (avatar!=null) {
			imageHeight = (int) avatar.getHeight();
			avatarView = new ImageView(avatar);
			vbox.getChildren().add(avatarView);
		}
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Node getNode() {
		return vbox;
	}
	
	/**
	 * Binds object to the property given
	 * @return
	 */
	public void bindWidth(ReadOnlyDoubleProperty readOnlyDoubleProperty) {
		vbox.prefWidthProperty().bind(readOnlyDoubleProperty);
	}
	
	/**
	 * Binds object to the property given
	 * @return
	 */
	public void bindHeight(ReadOnlyDoubleProperty readOnlyDoubleProperty) {
		vbox.prefHeightProperty().bind(readOnlyDoubleProperty);
	}
	
	public void addToTextbox(String input) {
		//GUI.addParagraphToTextFlow(textflow,new Text(input.trim()));
		//maintainTextboxLimit();
		//keepOne();
		label.setText(input.trim());
	}
	
	public void keepOne() {
		if (textflow.getChildren().size()>1) {
			textflow.getChildren().remove(0);
		}
	}
	
	public void maintainTextboxLimit() {
		int height = 0; // initialise a num to start the sum from
		int delete = -1; 
		for (int i=0;i<textflow.getChildren().size();i++) {
			height += textflow.getChildren().get(i).getBoundsInParent().getHeight();
			while (height > textboxHeight) {
				delete++;
				height -= textflow.getChildren().get(delete).getBoundsInParent().getHeight();
			}
		}
		
		if (delete>=0) {
			textflow.getChildren().remove(0, delete+1);
		}
	}
}
