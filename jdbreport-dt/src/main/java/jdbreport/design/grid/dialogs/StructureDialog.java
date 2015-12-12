/*
 * Created on 28.11.2015
 *
 * Copyright (C) 2015 Andrey Kholmanskih
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jdbreport.design.grid.dialogs;

import jdbreport.design.grid.TemplateGrid;
import jdbreport.util.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * @version 3.1 28.11.2015
 * @author Andrey Kholmanskih
 */
public class StructureDialog extends JDialog {

     private StructurePanel structurePanel;

    public StructureDialog(Window owner, TemplateGrid grid) {
        super(owner, DEFAULT_MODALITY_TYPE);
        init(grid);
    }

    private void init(TemplateGrid grid) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(Messages.getString("StructureDialog.title"));
        structurePanel = new StructurePanel(grid);
        initControls();
        Utils.screenCenter(this);
    }

    private void initControls() {
        setSize(300, 500);
        JPanel panel = new JPanel(new BorderLayout());
        getContentPane().add(panel);
        panel.add(structurePanel, BorderLayout.CENTER);
    }

}
