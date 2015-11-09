package gui;

import java.net.URL;

import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;
import javafx.stage.Popup;

//@@A0122534R
public class HelpMenu extends Popup {
	protected final static String HELP_HTML = "resources/help.html";
	protected final static int POPUP_HEIGHT = 450;
	
	protected WebView webView;
	protected Popup popup;
	
	public HelpMenu() {
		initWebView();
		initPopup();
        popup.getContent().add(webView);
	}
	
	/**
	 * @return the master/parent node for this object
	 */
	public Popup getNode() {
		return popup;
	}
	
	/**
	 * Initialises the WebView loads HELP.HTML into it
	 * @param webView
	 */
	protected void initWebView() {
		webView = new WebView();
		URL help = HelpMenu.class.getResource(HELP_HTML);
		if (help!=null) {
			String url = help.toExternalForm();
			webView.getEngine().load(url);
		}
        webView.setFontSmoothingType(FontSmoothingType.LCD);
        webView.setCache(true); // cache it as a viewable
	}
	
	/**
	 * Initialises the Popup and its default options
	 * @param popup
	 */
	protected void initPopup() {
		popup = new Popup();
		popup.setAutoHide(true); // hides after focus is lost so that no need to close
		popup.centerOnScreen();

	}
	
}
