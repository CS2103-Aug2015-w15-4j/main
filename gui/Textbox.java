package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class Textbox {
	public final static String TAG = GUIController.TAG_SIDEBAR;
	public final static String TAG_TEXTBOX = GUIController.TAG_SIDEBAR_TEXTBOX;
	public final static String AVATAR_IMAGE = GUIController.AVATAR_IMAGENAME;
	public final static String WELCOME_MESSAGE = "Welcome!";
	
	public final static int WIDTH = 100;//GUIController.MINIMUM_WINDOW_SIZE/GUIController.TEXTBOX_RATIO;
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
	
	protected Label clock = new Label(); 
	LocalDateTime time;
	DateTimeFormatter formatter;

	public Textbox() {
		hbox = new HBox();
		
		clock = new Label();
		updateTime();
		clock.prefWidthProperty().bind(hbox.widthProperty());
		clock.setMaxWidth(350);
		clock.setTextAlignment(TextAlignment.CENTER);
		clock.setAlignment(Pos.CENTER);
		HBox.setHgrow(clock, Priority.NEVER);
		
		//*
		avatar = new Image(Textbox.class.getResourceAsStream(AVATAR_IMAGE),WIDTH, WIDTH, true,true);
		if (avatar!=null) {
			avatarView = new ImageView(avatar);
			avatarView.setEffect(new DropShadow());
			hbox.getChildren().add(avatarView);
		}//*/
		label = new Label(WELCOME_MESSAGE);
		label.prefWidthProperty().bind(hbox.widthProperty());
		label.prefHeightProperty().bind(hbox.heightProperty());
		label.setId(TAG_TEXTBOX);
		label.setTextAlignment(TextAlignment.LEFT);
		//label.setStyle("-fx-border-color: #4E443C;");
		label.setPadding(new Insets(PADDING));
		HBox.setHgrow(label, Priority.ALWAYS);
		
		hbox.setPadding(new Insets(PADDING));
		hbox.setSpacing(PADDING);
		hbox.setAlignment(ORIENTATION);
		hbox.getStyleClass().add(TAG);
		hbox.getChildren().add(label);
		hbox.getChildren().add(clock);
		
		// time
        final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {  
        	@Override  
        	public void handle(ActionEvent event) {
        		updateTime();
        	}
        }));  
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
	}
	
	public void updateTime() {
		time = LocalDateTime.now();
		clock.setText(
			time.getDayOfWeek() + "\n" +
			time.toLocalDate().toString() + "\n" + 
			time.getHour() + ":" + time.getMinute()
		);
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
