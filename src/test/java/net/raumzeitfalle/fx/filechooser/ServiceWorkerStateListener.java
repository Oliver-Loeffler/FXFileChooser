package net.raumzeitfalle.fx.filechooser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;

class ServiceWorkerStateListener<R> implements ChangeListener<Worker.State> {
	
	private final CompletableFuture<ServiceExecResult<R>> serviceTestResult = new CompletableFuture<>();
	
	private final Supplier<R> valueSource;
	
	public static <R> ServiceWorkerStateListener<R> with(Service<R> serviceUnderTest) {
		
		ServiceWorkerStateListener<R> listener = new ServiceWorkerStateListener<>(serviceUnderTest);
		serviceUnderTest.stateProperty().addListener(listener);
		
		return listener;
	}
	
	public ServiceWorkerStateListener(Service<R> serviceUnderTest) {
		this(()->serviceUnderTest.getValue());
	}
	
	public ServiceWorkerStateListener(Supplier<R> valueSource) {
		this.valueSource = valueSource;
	}
	
	@Override
	public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
		if (newValue == Worker.State.SUCCEEDED) {
			serviceTestResult.complete(new ServiceExecResult<R>(newValue, valueSource.get()));
		}
		
		if (newValue == Worker.State.CANCELLED) {
			serviceTestResult.complete(new ServiceExecResult<R>(newValue, valueSource.get()));
		}
		
		if (newValue == Worker.State.FAILED) {
			serviceTestResult.complete(new ServiceExecResult<R>(newValue, valueSource.get()));
		}
		
	}
	
	public CompletableFuture<ServiceExecResult<R>> getServiceResult() {
		return serviceTestResult;
	}
	
	static class ServiceExecResult<R> {
		
		private final Worker.State serviceState;
		
		private final R result;

		public ServiceExecResult(Worker.State serviceState, R result) {
			this.serviceState = serviceState;
			this.result = result;
		}
		
		public Worker.State getServiceState() {
			return serviceState;
		}

		public R getResult() {
			return result;
		}
	}
}
