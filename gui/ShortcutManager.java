package gui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

//@@author A0122534R
public class ShortcutManager {
	public static enum CommandType { 
		FOCUS_MODE, 
		FOCUS_MODE_CLEAR, 
		HELP,
		SEARCH,  
		SWITCH,
		INPUT_FIELD, // gives control to the input Field
		UNDO, 
		UNPIN,
		NONE
	};
	
	public static CommandType processKeyEvent(KeyEvent keyEvent) {
		if (isFocusMode(keyEvent)) {
			return CommandType.FOCUS_MODE;
		} else if (isFocusModeClear(keyEvent)) {
			return CommandType.FOCUS_MODE_CLEAR;
		} else if (isHelp(keyEvent)) {
			return CommandType.HELP;
		} else if (isSearch(keyEvent)) {
			return CommandType.SEARCH;
		} else if (isSwitch(keyEvent)) {
			return CommandType.SWITCH;
		} else if (isInputField(keyEvent)) {
			return CommandType.INPUT_FIELD;
		} else if (isUndo(keyEvent)) {
			return CommandType.UNDO;
		} else if (isUnpin(keyEvent)) {
			return CommandType.UNPIN;
		}
		
		return CommandType.NONE;
	}
	
	public static boolean isFocusMode(KeyEvent keyEvent) {
		return (keyEvent.getCode()==KeyCode.BACK_SLASH);
	}
	
	public static boolean isFocusModeClear(KeyEvent keyEvent) {
		return ((keyEvent.getCode()==KeyCode.BACK_SLASH&&keyEvent.isShiftDown())||
		keyEvent.getCode()==KeyCode.BACK_SPACE);
	}

	public static boolean isHelp(KeyEvent keyEvent) {
		return (keyEvent.getCode()==KeyCode.F1);
	}
	
	public static boolean isSearch(KeyEvent keyEvent) {
		 return (keyEvent.getCode()==KeyCode.F&&
					(keyEvent.isControlDown()));
	}

	public static boolean isSwitch(KeyEvent keyEvent) {
		return ((keyEvent.getCode()==KeyCode.T)&&keyEvent.isAltDown());
	}
	
	public static boolean isInputField(KeyEvent keyEvent) {
		return ((keyEvent.getCode()==KeyCode.T)&&keyEvent.isControlDown());
	}
	
	public static boolean isUndo(KeyEvent keyEvent) {
		return (keyEvent.getCode()==KeyCode.Z&&
				(keyEvent.isControlDown()||keyEvent.isAltDown()));
	}
	
	public static boolean isUnpin(KeyEvent keyEvent) {
		return (keyEvent.getCode()==KeyCode.U&&
				(keyEvent.isControlDown()));
	}
}
