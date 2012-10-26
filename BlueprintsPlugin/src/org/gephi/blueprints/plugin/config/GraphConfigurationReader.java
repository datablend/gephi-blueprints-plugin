package org.gephi.blueprints.plugin.config;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.rexster.Tokens;
import com.tinkerpop.rexster.config.GraphConfiguration;
import com.tinkerpop.rexster.config.Neo4jGraphConfiguration;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Reader for graph database configurations.
 * 
 * @see FluxGraphGraphConfiguration
 * @see Neo4jGraphConfiguration
 * @author Timmy Storms (timmy.storms@gmail.com)
 */
public final class GraphConfigurationReader {
    
    /** The configuration files directory path constant. */
    private static final String GRAPH_DIR_PATH = "/etc/graph";
    
    /** The available {@link Configuration}s, sorted by graph name. */
    private static final Map<String,Configuration> CONFIGURATIONS = new TreeMap<String, Configuration>();
    
    static {
        final String fullPath = System.getProperty("user.dir") + GRAPH_DIR_PATH;
        final File configurationDirectory = new File(fullPath);
        Validate.notNull(configurationDirectory, "Directory " + fullPath + " does not exist");
        Validate.isTrue(configurationDirectory.exists(), "Graph configuration directory does not exist. "
                + "Make sure that " + fullPath + " exists.");
        Validate.isTrue(configurationDirectory.listFiles().length != 0, "No graph configuration files found");
        for (final File configFile : configurationDirectory.listFiles()) {
            final Configuration configuration;
            try {
                configuration = new XMLConfiguration(configFile);
            } catch (final ConfigurationException e) {
                throw new GraphConfigurationException(e);
            }
            String graphName = configuration.getString(Tokens.REXSTER_GRAPH_NAME);     
            if (StringUtils.isBlank(graphName)) {
                graphName = "Unknown database - check configuration element " + Tokens.REXSTER_GRAPH_NAME;
            }
            CONFIGURATIONS.put(graphName, configuration);
        }
    }   
    
    private GraphConfigurationReader() {}
    
    /**
     * Returns an initialized {@link Graph} that corresponds to a given name.
     * @param name the graph database name
     * @return the initialized {@link Graph}
     */
    public static Graph getGraph(final String name) {
        Validate.isTrue(StringUtils.isNotBlank(name), "Graph name cannot be empty");
        final Configuration configuration = CONFIGURATIONS.get(name);
        final String graphType = configuration.getString(Tokens.REXSTER_GRAPH_TYPE);
        try {
            final Class<?> clazz = Class.forName(graphType);            
            final Constructor<?> constructor = clazz.getConstructor();
            return ((GraphConfiguration) constructor.newInstance()).configureGraphInstance(configuration);
        } catch (final Exception e) {
            throw new GraphConfigurationException(e);
        } 
    }
    
    /**
     * Returns all graph database names of all available configurations.
     * @return  an array containing all graph database names
     */
    public static String[] getGraphNames() {
        return CONFIGURATIONS.keySet().toArray(new String[CONFIGURATIONS.size()]);
    }
    
    /** Runtime exception that should be thrown when the graph configuration fails. */
    static class GraphConfigurationException extends RuntimeException {
        
        /** {@inheritDoc} */
        public GraphConfigurationException(final Throwable cause) {
            super(cause);
        }
        
    }
    
}
