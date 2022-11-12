/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2021 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
        this(() -> serviceUnderTest.getValue());
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
