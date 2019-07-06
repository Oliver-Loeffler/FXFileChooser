package net.raumzeitfalle.fx.filechooser;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class FileSystemDialogAdapter<DIALOG,CONTAINER,RESULT> {

	private RESULT result;

	private DIALOG dialog;
	
	private Consumer<CONTAINER> beforeOpen;
	
	private Consumer<CONTAINER> afterClosing;

	private BiFunction<DIALOG,CONTAINER,RESULT> defaultDialogFunction;
	
	private Function<RESULT,SwingDialogReturnValues> resultFunction;
	
	private Function<Throwable,SwingDialogReturnValues> errorFunction;

	public FileSystemDialogAdapter(DIALOG fxSystemDialog,
			BiFunction<DIALOG,CONTAINER,RESULT> dialogFunction) {
		
		this(fxSystemDialog, dialogFunction, 
				actualResult -> (null == actualResult ? SwingDialogReturnValues.CANCEL_OPTION : SwingDialogReturnValues.APPROVE_OPTION),
				exception -> SwingDialogReturnValues.ERROR_OPTION);
	}
	
	public FileSystemDialogAdapter(DIALOG fxSystemDialog,
			BiFunction<DIALOG,CONTAINER,RESULT> dialogFunction,
			Function<RESULT,SwingDialogReturnValues> resultFunction,
			Function<Throwable,SwingDialogReturnValues> errorFunction) {
		
		this.dialog = fxSystemDialog;
		this.defaultDialogFunction = dialogFunction;
		this.resultFunction = resultFunction;
		this.errorFunction = errorFunction;
		
		this.result = null;
		
		this.beforeOpen = null;
		this.afterClosing = null;
	}
	
	public FileSystemDialogAdapter<DIALOG,CONTAINER,RESULT> beforeOpenDialog(Consumer<CONTAINER> beforeAction) {
		this.beforeOpen = beforeAction;
		return this;
	}
	
	public FileSystemDialogAdapter<DIALOG,CONTAINER,RESULT> afterClosingDialog(Consumer<CONTAINER> afterClosing) {
		this.afterClosing = afterClosing;
		return this;
	}
	
	
	public int runDialog(CONTAINER window) {
		return runDialog(defaultDialogFunction, window);
	}


	public int runDialog(BiFunction<DIALOG,CONTAINER,RESULT> dialogFunction, CONTAINER window) {

		if (null != beforeOpen) {
			beforeOpen.accept(window);
		}

		try {
			Invoke.andWait(() -> {
				result = dialogFunction.apply(dialog, window);
				if (null != afterClosing) {
					afterClosing.accept(window);
				}
			});
		} catch (Exception e) {
			this.errorFunction
				.apply(e)
				.getValue();
		}		
		
		return this.resultFunction
				.apply(this.result)
				.getValue();
	}

	public RESULT getResult() {
		return result;
	}

}
