package test;

import static org.junit.Assert.*;

import org.junit.Test;

import gui.ShortcutManager;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import gui.ShortcutManager.CommandType;

public class GUIShortcutTests {
	boolean isShiftDown = false; 
	boolean isControlDown = false; 
	boolean isAltDown = false; 
	KeyEvent keyEvent = null;
	@Test
	public void focusModeTest01() {
		// BACK_SLASH (\) only
		keyEvent = new KeyEvent(null, null, null, KeyCode.BACK_SLASH, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.FOCUS_MODE, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void focusModeTest02() {
		// Ctrl + BACK_SLASH (\) only
		isControlDown = true;
		keyEvent = new KeyEvent(null, null, null, KeyCode.BACK_SLASH, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.FOCUS_MODE, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void focusModeClearTest01() {
		// BACK_SPACE only
		keyEvent = new KeyEvent(null, null, null, KeyCode.BACK_SPACE, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.FOCUS_MODE_CLEAR, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void focusModeClearTest02() {
		// Shift + BACK_SLASH (\) only
		isShiftDown = true;
		keyEvent = new KeyEvent(null, null, null, KeyCode.BACK_SLASH, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.FOCUS_MODE_CLEAR, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void helpTest01() {
		// F1 only
		keyEvent = new KeyEvent(null, null, null, KeyCode.F1, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.HELP, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void searchTest01() {
		// Ctrl + F only
		isControlDown = true;
		keyEvent = new KeyEvent(null, null, null, KeyCode.F, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.SEARCH, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void switchTest01() {
		// Alt + T only
		isAltDown = true;
		keyEvent = new KeyEvent(null, null, null, KeyCode.T, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.SWITCH, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void inputFieldTest01() {
		// Ctrl + T only
		isControlDown = true;
		keyEvent = new KeyEvent(null, null, null, KeyCode.T, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.INPUT_FIELD, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void undoTest01() {
		// Ctrl + Z only
		isControlDown = true;
		keyEvent = new KeyEvent(null, null, null, KeyCode.Z, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.UNDO, ShortcutManager.processKeyEvent(keyEvent));
	}
	
	@Test
	public void unpinTest01() {
		// Ctrl + U only
		isControlDown = true;
		keyEvent = new KeyEvent(null, null, null, KeyCode.U, isShiftDown, isControlDown, isAltDown, false);
		assertEquals(CommandType.UNPIN, ShortcutManager.processKeyEvent(keyEvent));
	}
}
