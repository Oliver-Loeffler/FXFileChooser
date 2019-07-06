package net.raumzeitfalle.fx.filechooser;

public enum SwingDialogReturnValues {

    /**
     * Return value if cancel is chosen.
     */
    CANCEL_OPTION(1),

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    APPROVE_OPTION(0),

    /**
     * Return value if an error occurred.
     */
    ERROR_OPTION(-1);

	private final int returnValue;
	
	private SwingDialogReturnValues(int returnValue) {
		this.returnValue = returnValue;
	}
	
	public int getValue() {
		return this.returnValue;
	}
    
}
