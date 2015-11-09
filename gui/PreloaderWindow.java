package gui;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//@@author A0122534R
public class PreloaderWindow extends Preloader {
	protected final static int PRELOADER_SIZE = 150;
	protected final static String AVATAR_NAME = "resources/lexi.png";
	protected final static int AVATAR_SIZE = 80;
	private Stage preloaderStage;
	private Scene scene;

	@Override
	public void init() {
		ProgressIndicator preloader = new ProgressIndicator();
		preloader.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		preloader.setMinSize(PRELOADER_SIZE, PRELOADER_SIZE);
		try {
			Image avatar = new Image(PreloaderWindow.class.getResourceAsStream(AVATAR_NAME), AVATAR_SIZE, AVATAR_SIZE, true, true);
			ImageView avatarFrame = new ImageView(avatar);
			
			StackPane sp = new StackPane();
			sp.getChildren().add(preloader);
			sp.getChildren().add(avatarFrame);
			scene = new Scene(sp);
		} catch (Exception e) {
			scene = new Scene(preloader);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.preloaderStage = primaryStage;
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.show();
	}

	@Override
	public void handleStateChangeNotification(
			StateChangeNotification stateChangeNotification) {
		if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
			preloaderStage.hide();
		}
	}
}
