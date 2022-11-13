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
package net.raumzeitfalle.fx.filechooser.demos;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.raumzeitfalle.fx.filechooser.Skin;
import net.raumzeitfalle.fx.filechooser.SwingFileChooser;

public class DemoSwing {
    public static void main(String... args) {

        JFrame frame = new JFrame("Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton showDialogButton = new JButton("show SwingFileChooser");
        frame.getContentPane().add(showDialogButton);

        // SwingFileChooser.setUseJavaFxDirectoryChooser(true);
        SwingFileChooser fileChooser = SwingFileChooser.create(Skin.DARK);
        showDialogButton.addActionListener(l -> {
            int option = fileChooser.showOpenDialog(frame);
            if (option == SwingFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(frame, fileChooser.getSelectedFile().toString());
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
