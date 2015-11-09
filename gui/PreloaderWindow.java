package gui;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

//@@author A0122534R
public class PreloaderWindow extends Preloader {
	protected final static int PRELOADER_SIZE = 150;
	private Stage preloaderStage;
	private Scene scene;

	@Override
	public void init() {
		ProgressIndicator preloader = new ProgressIndicator();
		preloader.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		preloader.setMinSize(PRELOADER_SIZE, PRELOADER_SIZE);
		scene = new Scene(preloader);
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
