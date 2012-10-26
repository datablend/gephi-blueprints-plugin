package org.gephi.blueprints.plugin.ui;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.gephi.blueprints.plugin.config.GraphConfigurationReader;
import org.gephi.blueprints.plugin.importer.GraphDbImporter;
import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.ImporterUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * The user interface that provides configuration for the {@link GraphDbImporter}.
 * 
 * @author Timmy Storms (timmy.storms@gmail.com)
 */
@ServiceProvider(service = ImporterUI.class)
public final class GraphDbImporterUI implements ImporterUI {
    
    /** {@inheritDoc} */
    @Override
    public JPanel getPanel() {
        final JPanel rootPanel = new JPanel(new BorderLayout());        
        final JComboBox choiseBox = new JComboBox(GraphConfigurationReader.getGraphNames());
        choiseBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent event) {
                final String itemName = choiseBox.getSelectedItem().toString();
                GraphDbImporter.getInstance().setGraph(GraphConfigurationReader.getGraph(itemName));
            }
        });
        rootPanel.add(choiseBox, BorderLayout.CENTER);
        return rootPanel;
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(GraphDbImporterUI.class, "GraphDbImporterUI_Name");
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isUIForImporter(final Importer importer) {
        return importer instanceof GraphDbImporter;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup(final Importer importer) {}
    
    /** {@inheritDoc} */
    @Override
    public void unsetup(final boolean update) {}

}
