/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2019 Oliver Loeffler, Raumzeitfalle.net
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

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Platform;

class Invoke {

    private Invoke() {

    }

    static void later(Supplier<Void> action) {
        Platform.runLater(action::get);
    }

    static <T> void later(T value, Consumer<T> consumingAction) {
        Platform.runLater(() -> consumingAction.accept(value));
    }

    static void laterWithDelay(Duration duration, Runnable r) {
        Platform.runLater(() -> {
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            r.run();
        });
    }

    static void andWaitWithoutException(Runnable r) {
        try {
            andWait(r);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
    }

    static void andWait(Runnable r) throws InterruptedException, ExecutionException {
        FutureTask<?> task = new FutureTask<>(r, null);
        Platform.runLater(task);
        task.get();
    }

}
