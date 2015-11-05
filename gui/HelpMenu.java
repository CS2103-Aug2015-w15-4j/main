package gui;

import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;
import javafx.stage.Popup;

public class HelpMenu {
	protected final static String HELP_HTML = "resources/help.html";
	
	protected int POPUP_HEIGHT = 450;
	
	protected WebView webView;
	protected Popup popup;
	
	public HelpMenu() {
		// load the help into webview
		webView = new WebView();
		String url = HelpMenu.class.getResource(HELP_HTML).toExternalForm();
        webView.getEngine().load(url);
        webView.setFontSmoothingType(FontSmoothingType.LCD);
        webView.setCache(true); // cache it as a viewable
        // create the popup
		popup = new Popup();
		popup.setAutoHide(true); // hides after focus is lost so that no need to close
		popup.getContent().add(webView);
		popup.centerOnScreen();
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Popup getNode() {
		return popup;
	}
}
