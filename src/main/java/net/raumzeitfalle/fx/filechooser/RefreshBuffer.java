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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Platform;
import javafx.collections.ObservableList;

final class RefreshBuffer {

    static RefreshBuffer get(FindFilesTask task, int expectedNumberOfElements, ObservableList<IndexedPath> target) {
        int bufferSize = determineBufferSize(expectedNumberOfElements);
        return new RefreshBuffer(task, bufferSize, target);
    }

    private final List<IndexedPath> cache;

    private final AtomicReference<List<IndexedPath>> atomicCache;

    private final ReentrantLock lock = new ReentrantLock();

    private final ObservableList<IndexedPath> target;

    private final int desiredCacheSize;

    private final FindFilesTask task;

    private RefreshBuffer(FindFilesTask task, int bufferSize, ObservableList<IndexedPath> target) {
        this.cache = new ArrayList<>(2 * bufferSize);
        this.target = target;
        this.desiredCacheSize = bufferSize;
        this.atomicCache = new AtomicReference<>(cache);
        this.task = task;
    }

    void update(Path file) {
        cache.add(IndexedPath.valueOf(file));
        if (!task.isCancelled() && currentCacheSize() > desiredCacheSize) {
            flush();
        }
    }

    private int currentCacheSize() {
        return cache.size();
    }

    void flush() {
        this.lock.lock();
        try {
            IndexedPath[] update = this.atomicCache.get().toArray(new IndexedPath[0]);
            Platform.runLater(() -> target.addAll(update));
            this.atomicCache.get().clear();
        } finally {
            this.lock.unlock();
        }
    }

    static int determineBufferSize(int items) {
        if (items > 500_000)
            return 1000;

        if (items > 100_000)
            return 500;

        if (items > 50_000)
            return 200;

        if (items > 15_000)
            return 100;

        if (items > 5_000)
            return 50;

        if (items > 1_000)
            return 20;

        return 10;
    }

}
