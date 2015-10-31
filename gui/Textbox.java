package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
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
	public final static int CLOCK_HEIGHT = 100;
	public final static int PADDING = 10;
	public final static Pos ORIENTATION = Pos.CENTER_RIGHT;
	
	public final static int POS_AVATAR = 0;
	public final static int POS_TEXTBOX = 1;
	public final static int POS_CLOCK = 2;

	public static double AUDIO_VOLUME = 0.1;
	
	protected TextFlow textflow; // list of messages from application
	protected Label label;
	protected HBox hbox;
	
	// Avatar
	protected Image avatar;
	protected ImageView avatarView;
	protected Button frame; // frame for avatar
	
	// Audio
	protected Random randomGen;
	protected ArrayList<AudioClip> audioClips;
	protected AudioClip currentlyPlaying = null;
	public final String[] audioClipNames = {
			"gui/resources/pika_happy.mp3",
			"gui/resources/pika_piiikachu.mp3",
			"gui/resources/pikaaaa.mp3",
			"gui/resources/pikachu_normal.mp3"
		};
	
	// Real time clock
	protected Button clock; // button so that it can be clicked for a more indepth date view later
	LocalDateTime time;
	DateTimeFormatter formatter;

	public Textbox() {
		hbox = new HBox();
		
		// create the clock
		clock = new Button();
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
		
		// create the avatar
		avatarView = new ImageView();
		avatarView.setEffect(new DropShadow());
		frame = new Button();
		frame.setGraphic(avatarView);
		hbox.getChildren().add(frame);
		loadAvatar(); // loads an image from file into avatarView
		
		// now create the audio clips and random generator
		randomGen = new Random();
		randomGen.setSeed(time.getNano()); // set random seed
		loadAudioClips();
		
		// create the message box
		label = new Label(WELCOME_MESSAGE);
		label.setWrapText(true);
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
        
        // clicking on avatar
        frame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!audioClips.isEmpty()) { // if there is audio, play one
					if (currentlyPlaying==null||!currentlyPlaying.isPlaying()) { 
						// if it is empty or is not currently playing
						AudioClip randomClip;
						do {
							randomClip = audioClips.get(randomGen.nextInt(audioClips.size()));
						}while(audioClips.size()>1&& // if only one item, just keep it
								currentlyPlaying!=null&& // if it is null, don't bother with more loops
								currentlyPlaying.equals(randomClip) // if it is same, keep looping
								); // while they are the same, keep looping
						currentlyPlaying = randomClip;
						currentlyPlaying.setVolume(AUDIO_VOLUME);
						currentlyPlaying.play();
					}
				}
			}
	    });
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
					if (!hbox.getChildren().contains(frame)) { // if it was removed, add it back
						hbox.getChildren().add(0, frame); // at first position 
					}
				}
				return true;
			}
		} catch (FileNotFoundException e) {
			// do nothing
			if (avatar==null) {
				hbox.getChildren().remove(frame);
			}
		}
		return false;
	}
	
	/**
	 * Loads all audio clips in voiceClips
	 * @return true if successful
	 */
	public boolean loadAudioClips() {
		audioClips = new ArrayList<AudioClip>();
		File file;
		try {
			for (int i=0;i<audioClipNames.length;i++) {
				file = new File(audioClipNames[i]);
				if (file.exists()) {
					audioClips.add(new AudioClip(file.toURI().toString()));
				} else {
					System.err.println("AudioClip not found at: " + file.toURI().toString());
				}
			}
			return true;
		} catch (Exception e) {
			// do nothing
			System.out.println("Unable to load AudioClips");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Gets current time and displays it on Clock label
	 */
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
