package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class Textbox {
	public final static String TAG = GUIController.TAG_BOTTOMBAR;
	public final static String TAG_TEXTBOX = GUIController.TAG_TEXTBOX;
	public static String AVATAR_IMAGE = GUIController.AVATAR_IMAGENAME;
	public final static String WELCOME_MESSAGE = "Welcome!";
	
	public final static int WIDTH = 100;//GUIController.MINIMUM_WINDOW_SIZE/GUIController.TEXTBOX_RATIO;
	public final static int CLOCK_WIDTH = 140;
	public final static int CLOCK_HEIGHT = 80;
	public final static int PADDING = 10;
	public final static Pos ORIENTATION = Pos.CENTER_RIGHT;
	
	protected TextFlow textflow; // list of messages from application
	protected Label label;
	protected HBox hbox;
	protected Image avatar;
	protected ImageView avatarView;
	protected Image quote = null;
	protected ImageView quoteView;
	
	protected Label clock; 
	LocalDateTime time;
	DateTimeFormatter formatter;

	public Textbox() {
		hbox = new HBox();
		
		clock = new Label();
		clock.getStyleClass().add(GUIController.STYLE_CURVED_LABEL);
		formatter = DateTimeFormatter.ofPattern("E\ndd/MM/yyyy\nHH:mm");
		updateTime();
		//clock.prefWidthProperty().bind(hbox.widthProperty());
		clock.setMinHeight(CLOCK_HEIGHT);
		//clock.setMaxWidth(CLOCK_WIDTH);
		clock.setMinWidth(CLOCK_WIDTH);
		clock.setTextAlignment(TextAlignment.CENTER);
		clock.setAlignment(Pos.CENTER);
		HBox.setHgrow(clock, Priority.SOMETIMES);

		avatarView = new ImageView();
		avatarView.setEffect(new DropShadow());
		hbox.getChildren().add(avatarView);
		loadAvatar(); // loads an image from file into avatarView
		label = new Label(WELCOME_MESSAGE);
		label.prefWidthProperty().bind(hbox.widthProperty());
		label.prefHeightProperty().bind(hbox.heightProperty());
		label.setTextFill(Color.WHITE);
		label.setEffect(new DropShadow());
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
	
	/**
	 * Loads avatar
	 * @return true if successful
	 */
	public boolean loadAvatar() {
		AVATAR_IMAGE = GUIController.AVATAR_IMAGENAME;
		InputStream stream;
		try {
			stream = new FileInputStream(new File(AVATAR_IMAGE));
			if (stream!=null) {
				Image image = new Image(stream,WIDTH, WIDTH, true,true);
				if (image!=null) {
					avatar = image;
					avatarView.setImage(avatar);
				}
				return true;
			}
		} catch (FileNotFoundException e) {
			// do nothing
		}
		return false;
	}
	
	public void updateTime() {
		time = LocalDateTime.now();
		/*
		clock.setText(
			time.getDayOfWeek() + "\n" +
			time.toLocalDate().toString() + "\n" + 
			time.getHour() + ":" + time.getMinute()
		);//*/
		clock.setText(time.format(formatter));
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
