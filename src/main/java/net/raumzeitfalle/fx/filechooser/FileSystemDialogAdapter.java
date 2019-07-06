package net.raumzeitfalle.fx.filechooser;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 
 * @author Oliver
 *
 * @param <D> The type of JavaFX dialog used, e.g. FileChooser or DirectoryChooser.
 * @param <W> The actual parent window type.
 * @param <R> The result type of a dialog interaction, in case of mentioned examples File.
 * 
 */
public class FileSystemDialogAdapter<D,W,R> {

	private R result;

	private D dialog;
	
	private Consumer<W> beforeOpen;
	
	private Consumer<W> afterClosing;

	private BiFunction<D,W,R> defaultDialogFunction;
	
	private Function<R,SwingDialogReturnValues> resultFunction;
	
	private Function<Throwable,SwingDialogReturnValues> errorFunction;

	public FileSystemDialogAdapter(D fxSystemDialog,
			BiFunction<D,W,R> dialogFunction) {
		
		this(fxSystemDialog, dialogFunction, 
				actualResult -> (null == actualResult ? SwingDialogReturnValues.CANCEL_OPTION : SwingDialogReturnValues.APPROVE_OPTION),
				exception -> SwingDialogReturnValues.ERROR_OPTION);
	}
	
	public FileSystemDialogAdapter(D fxSystemDialog,
			BiFunction<D,W,R> dialogFunction,
			Function<R,SwingDialogReturnValues> resultFunction,
			Function<Throwable,SwingDialogReturnValues> errorFunction) {
		
		this.dialog = fxSystemDialog;
		this.defaultDialogFunction = dialogFunction;
		this.resultFunction = resultFunction;
		this.errorFunction = errorFunction;
		
		this.result = null;
		
		this.beforeOpen = null;
		this.afterClosing = null;
	}
	
	public FileSystemDialogAdapter<D,W,R> beforeOpenDialog(Consumer<W> beforeAction) {
		this.beforeOpen = beforeAction;
		return this;
	}
	
	public FileSystemDialogAdapter<D,W,R> afterClosingDialog(Consumer<W> afterClosing) {
		this.afterClosing = afterClosing;
		return this;
	}
	
	
	public int runDialog(W window) {
		return runDialog(defaultDialogFunction, window);
	}


	public int runDialog(BiFunction<D,W,R> dialogFunction, W window) {

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

	public R getResult() {
		return result;
	}

}
