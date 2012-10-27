package org.gephi.blueprints.plugin.config;

import com.jnj.fluxgraph.FluxGraph;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.rexster.Tokens;
import com.tinkerpop.rexster.config.GraphConfiguration;
import com.tinkerpop.rexster.config.GraphConfigurationException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.postgresql.Driver;

/**
 * Rexter configuration for FluxGraph. Accepts configuration in fluxgraph.xml as follows:
 * 
 * <code>
 * <graph>
 *   <graph-name>FluxGraph Temporal Database</graph-name>
 *   <graph-type>org.gephi.blueprints.plugin.config.FluxGraphGraphConfiguration</graph-type>
 *   <graph-location>datomic:free://localhost:4334/schema</graph-location>
 *   <properties />
 * </graph>
 * </code>
 * 
 * The configuration file should be placed in gephi-folder/ext/graph.
 * 
 * @author Timmy Storms (timmy.storms@gmail.com)
 * @author Davy Suvee (dsuvee@its.jnj.com)
 * @author Davy Suvee (info@datablend.be)
 */
public final class FluxGraphGraphConfiguration implements GraphConfiguration {

    /** {@inheritDoc} */
    @Override
    public Graph configureGraphInstance(final Configuration properties) throws GraphConfigurationException {
        final String location = properties.getString(Tokens.REXSTER_GRAPH_LOCATION);
        if (StringUtils.isBlank(location)) {
            throw new GraphConfigurationException("issing or empty configuration element: " 
                    + Tokens.REXSTER_GRAPH_LOCATION);
        }
        // Classloading issue: No suitable driver found
        Thread.currentThread().setContextClassLoader(Driver.class.getClassLoader());
        return new FluxGraph(location);
    }
    
}
