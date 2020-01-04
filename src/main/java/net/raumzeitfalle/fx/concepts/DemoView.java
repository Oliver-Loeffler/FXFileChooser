/*-
 * #%L
 * FXFileChooser
 * %%
 * Copyright (C) 2017 - 2020 Oliver Loeffler, Raumzeitfalle.net
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
package net.raumzeitfalle.fx.concepts;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

public class DemoView extends AnchorPane {

    public DemoView() throws IOException {

        /*
         * TODO: #1
         *  Load FXML by convention, FXML should declare all GUI elements inside <fx:root/>.
         *
         */
        Class<?> thisClass = getClass();
        String fileName = thisClass.getSimpleName() + ".fxml";
        URL resource = thisClass.getResource(fileName);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setRoot(this);

        /*
         * TODO: #2 identify and load controller by convention
         *
         * loader.setController(...);
         * loader.setControllerFactory(...);
         */

        String data = "Hallo Welt!";

        Map<Class, Callable<?>> creators = new HashMap<>();
        creators.put(DemoController.class, new Callable<DemoController>() {

            @Override
            public DemoController call() throws Exception {
                return new DemoController(data);
            }

        });

        /*
        loader.setControllerFactory(new Callback<Class<?>, Object>() {

            @Override
            public Object call(Class<?> param) {
                System.out.println(param);
                Callable<?> callable = creators.get(param);
                if (callable == null) {
                    try {
                        // default handling: use no-arg constructor
                        return param.newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        throw new IllegalStateException(ex);
                    }
                } else {
                    try {
                        return callable.call();
                    } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        });
    */
        loader.setController(new DemoController(data));


        /*
         * TODO: #3 decide if and how necessary models can be loaded by convention, if needed.
         *           in some cases the controller may require a separate model
         */

        Parent view = loader.load();

        /*
         * TODO: #4 load CSS sheets also by convention
         *  - define a convention (incl. naming convention for e.g. skin names)
         *  - define a loading strategy
         *
         *  getStylesheets().add(url.toExternalForm());
         *  applyCss();
         *
         */
    }
}
