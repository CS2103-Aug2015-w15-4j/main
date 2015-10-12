package gui;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Sidebar {
	public final static String TAG = GUI.TAG_SIDEBAR;
	public final static String TAG_TEXTBOX = GUI.TAG_SIDEBAR_TEXTBOX;
	public final static String AVATAR_IMAGE = GUI.AVATAR_IMAGENAME;
	
	public final static int WIDTH = GUI.SIDEBAR_WIDTH;
	public final static int HEIGHT = GUI.WINDOW_HEIGHT;
	public final static int PADDING = 10;
	public final static Pos ORIENTATION = GUI.SIDEBAR_ORIENTATION;

	public int imageHeight = WIDTH;
	public int textboxHeight = HEIGHT - imageHeight - 20;
	
	protected TextFlow textbox; // list of messages from application
	protected VBox vbox;
	protected Image avatar;
	protected ImageView avatarView;

	public Sidebar() {
		textbox = new TextFlow();
		textbox.setId(TAG_TEXTBOX);
		textbox.setMaxWidth(WIDTH);
		textbox.setMinWidth(WIDTH);
		vbox = new VBox();
		vbox.setPadding(new Insets(PADDING));
		vbox.setSpacing(PADDING);
		vbox.setAlignment(ORIENTATION);
		vbox.getStyleClass().add(TAG);
		vbox.getChildren().add(textbox);
		VBox.setVgrow(textbox, Priority.ALWAYS);
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
	
	public void addToTextbox(String input) {
		GUI.addParagraphToTextFlow(textbox,new Text(input.trim()));
		maintainTextboxLimit();
	}
	
	public void maintainTextboxLimit() {
		int height = 0; // initialise a num to start the sum from
		int delete = -1; 
		for (int i=0;i<textbox.getChildren().size();i++) {
			height += textbox.getChildren().get(i).getBoundsInParent().getHeight();
			while (height > textboxHeight) {
				delete++;
				height -= textbox.getChildren().get(delete).getBoundsInParent().getHeight();
			}
		}
		
		if (delete>=0) {
			textbox.getChildren().remove(0, delete+1);
		}
	}
}
