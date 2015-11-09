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
import javafx.util.Duration;

//@@author A0122534R
public class ConsolePanel {
	protected final static String CSSTAG = GuiController.CSS_TAG_BOTTOMBAR;
	protected final static String CSSTAG_TEXTBOX = GuiController.CSS_TAG_TEXTBOX;
	protected final static String WELCOME_MESSAGE = "Welcome!";
	
	protected final static int IMAGE_SIZE = 100;
	protected final static int CLOCK_WIDTH = 140;
	protected final static int CLOCK_HEIGHT = 100;
	protected final static int PADDING = 10;
	protected final static Pos ORIENTATION = Pos.CENTER_RIGHT;
	
	protected final static int POS_AVATAR = 0;
	protected final static int POS_TEXTBOX = 1;
	protected final static int POS_CLOCK = 2;
	protected final static double AUDIO_VOLUME = 0.5; // not yet customisable
	
	protected Label console; // contains the output from the application
	protected HBox hbox;
	
	// Avatar
	protected Image avatar;
	protected ImageView avatarView;
	protected Button frame; // frame for avatar
	
	// Audio
	protected Random randomGen;
	protected ArrayList<AudioClip> audioClips;
	protected AudioClip currentlyPlaying = null;
	protected static String[] audioClipNames = {
			"resources/pika_happy.mp3",
			"resources/pika_piiikachu.mp3",
			"resources/pikaaaa.mp3",
			"resources/pikachu_normal.mp3"
		};
	
	// Real time clock
	protected Button clock; // button so that it can be clicked for a more indepth date view later
	protected LocalDateTime time;
	protected DateTimeFormatter formatter;

	public ConsolePanel() {
		initHBox();
		initClock();
		initMessageBox();
		initAvatar(); // loads avatar into hbox by default
		initAudioClips();

		hbox.getChildren().add(console);
		hbox.getChildren().add(clock);
		
		addHandlers();
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public HBox getNode() {
		return hbox;
	}
	
	/**
	 * Initialises the master node
	 * @param hbox
	 */
	protected void initHBox() {
		hbox = new HBox();
		hbox.setPadding(new Insets(PADDING));
		hbox.setSpacing(PADDING);
		hbox.setAlignment(ORIENTATION);
		hbox.getStyleClass().add(CSSTAG);
	}
	
	/**
	 * Initialises the Clock
	 * @param clock
	 * @param formatter
	 */
	protected void initClock() {
		clock = new Button();
		clock.getStyleClass().add(GuiController.CSS_STYLE_CURVED_LABEL);
		formatter = DateTimeFormatter.ofPattern("E\ndd/MM/yyyy\nHH:mm");
		updateTime();
		clock.setMinHeight(CLOCK_HEIGHT);
		clock.setMinWidth(CLOCK_WIDTH);
		clock.setTextAlignment(TextAlignment.CENTER);
		clock.setAlignment(Pos.CENTER);
		HBox.setHgrow(clock, Priority.SOMETIMES);
	}
	
	/**
	 * Creates the message box for the console output
	 * @param console
	 */
	protected void initMessageBox() {
		console = new Label(WELCOME_MESSAGE);
		console.setWrapText(true);
		console.prefWidthProperty().bind(getNode().widthProperty());
		console.prefHeightProperty().bind(getNode().heightProperty());
		console.setTextFill(Color.WHITE);
		console.setEffect(new DropShadow());
		console.setTextAlignment(TextAlignment.LEFT);
		console.setPadding(new Insets(PADDING));
		HBox.setHgrow(console, Priority.ALWAYS);
	}
	
	/**
	 * Initialises the avatar image
	 * @param avatar
	 * @param avatarView
	 * @param frame
	 */
	protected void initAvatar() {
		avatarView = new ImageView();
		avatarView.setEffect(new DropShadow());
		frame = new Button();
		frame.setGraphic(avatarView);
		loadAvatar(); // loads an image from file into avatarView
	}
	/**
	 * Initialises the audio clips and its random generator
	 * @param randomGen
	 * @param audioClips
	 */
	protected void initAudioClips() {
		// create the audio clips and random generator
		randomGen = new Random();
		randomGen.setSeed(time.getNano()); // set random seed
		loadAudioClips();
	}
	
	/**
	 * Listeners and handlers
	 */
	protected void addHandlers() {
		// constantly update the clock
        final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {  
        	@Override
        	public void handle(ActionEvent event) {
        		updateTime();
        	}
        }));  
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
        // clicking on avatar results in audio
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
		InputStream stream;
		try {
			stream = new FileInputStream(new File(GuiController.AVATAR_IMAGENAME));
			if (stream!=null) {
				Image image = new Image(stream,IMAGE_SIZE, IMAGE_SIZE, true,true);
				if (image!=null) {
					avatar = image;
					avatarView.setImage(avatar);
					if (!hbox.getChildren().contains(frame)) { // if it was removed, add it back
						hbox.getChildren().add(0, frame); // at first position 
					}
				}
				return true;
			}
		} catch (Exception e) { 
			// any kind of failure, FileNotFoundException or NullPointerException, do this
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
	public void loadAudioClips() {
		audioClips = new ArrayList<AudioClip>();
		File file;
		for (int i=0;i<audioClipNames.length;i++) {
			try {
				file = new File(audioClipNames[i]);
				if (file.exists()) {
					audioClips.add(new AudioClip(file.toURI().toString()));
				} else {
					throw new FileNotFoundException("AudioClip not found at: " + file.toURI().toString()); 
				}
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
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
	 * Adds the message to the console output
	 * @param message
	 */
	public void addToConsole(String message) {
		console.setText(message.trim());
	}
}
