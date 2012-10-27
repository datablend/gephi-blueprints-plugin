package org.gephi.blueprints.plugin.importer;

import org.gephi.io.importer.spi.DatabaseImporter;
import org.gephi.io.importer.spi.DatabaseImporterBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Graph database importer builder.
 * 
 * @author Timmy Storms (timmy.storms@gmail.com)
 * @author Davy Suvee (dsuvee@its.jnj.com)
 * @author Davy Suvee (info@datablend.be)
 */
@ServiceProvider(service = DatabaseImporterBuilder.class)
public class GraphDbImporterBuilder implements DatabaseImporterBuilder {

    /** {@inheritDoc} */
    @Override
    public DatabaseImporter buildImporter() {
        return GraphDbImporter.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return NbBundle.getMessage(GraphDbImporterBuilder.class, "GraphDbImporterBuilder_Name");
    }
    
}
