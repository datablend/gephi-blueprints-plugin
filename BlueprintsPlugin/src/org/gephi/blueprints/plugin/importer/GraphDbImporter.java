package org.gephi.blueprints.plugin.importer;

import org.gephi.blueprints.plugin.util.AttributeTypeMapper;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.Database;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.Issue;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.DatabaseImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.NbBundle;

/**
 * {@link DatabaseImporter} for graph databases (supported by the Blueprints API).
 * 
 * @author Timmy Storms (timmy.storms@gmail.com)
 * @author Davy Suvee (dsuvee@its.jnj.com)
 * @author Davy Suvee (info@datablend.be)
 */
public final class GraphDbImporter implements DatabaseImporter, LongTask {
    
    /** The singleton instance of this class. */
    private static final GraphDbImporter INSTANCE = new GraphDbImporter();
    
    /** The {@link ProgressTicket}. */
    private ProgressTicket progress;
    
    /** The {@link Report}. */
    private Report report;    
    
    /** The {@link ContainerLoader}. */
    private ContainerLoader container;
    
    /** The {@link AttributeModel}. */
    private AttributeModel attributeModel;
    
    /** Indicates whether the import should be cancelled. */
    private boolean cancel;
    
    /** The {@link Graph}. */
    private Graph graph;
    
    private GraphDbImporter() {}
    
    /**
     * Returns the singleton instance of this class.
     * @return the singleton instance
     */
    public static GraphDbImporter getInstance() {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public boolean execute(final ContainerLoader container) {
        this.container = container;
        this.attributeModel = container.getAttributeModel();
        report = new Report();
        progress.start();
        importData();
        progress.finish();      
        return !cancel;
    }    
    
    /**
     * Sets the {@link Graph} that will be used to import all nodes and edges.
     * @param graph the {@link Graph}
     */
    public void setGraph(final Graph graph) {
        this.graph = graph;
    }
    
    /**
     * Returns the {@link Graph}.
     * @return  the {@link Graph}
     */
    public Graph getGraph() {
        return this.graph;
    }
    
    /** {@inheritDoc} */
    @Override
    public ContainerLoader getContainer() {
        return container;
    }

    /** {@inheritDoc} */
    @Override
    public Report getReport() {
        return report;
    }

    /** {@inheritDoc} */
    @Override
    public boolean cancel() {
        cancel = true;
        return cancel;
    }

    /** {@inheritDoc} */
    @Override
    public void setProgressTicket(final ProgressTicket progress) {
        this.progress = progress;
    }
    
    /** Imports all nodes and edges. */
    private void importData() {
        try {
            final Iterable<Vertex> vertices = graph.getVertices();
            drawNodes(vertices);
            drawEdges(vertices);    
        } catch (final Exception e) {
            throw new ImportException(e);
        } finally {
            if (graph != null) {
                graph.shutdown();
            }
        }
    }
    
    /**
     * Draws all the available Gephi nodes. Node attributes are added as well.
     * @param vertices the vertices that are to be drawn
     */
    private void drawNodes(final Iterable<Vertex> vertices) {
        for (final Vertex vertex : vertices) {            
            if (cancel) {
                return;
            }
            final Object vertexId = vertex.getId();
            if (vertexId == null) {
                final String message = NbBundle.getMessage(GraphDbImporter.class, "Issue_NoIdentifier");
                final Issue issue = new Issue(message, Issue.Level.WARNING);
                report.logIssue(issue);
                continue;
            }
            final NodeDraft gephiNode = container.factory().newNodeDraft();
            gephiNode.setId(vertexId.toString());            
            container.addNode(gephiNode);
            report.log(NbBundle.getMessage(GraphDbImporter.class, "Report_NodeAdded", 
                    vertexId.toString()));            
            for (final String propertyKey : vertex.getPropertyKeys()) {
                final Object propertyValue = vertex.getProperty(propertyKey);
                AttributeColumn attributeColumn;
                if (attributeModel.getNodeTable().hasColumn(propertyKey)) {
                    attributeColumn = attributeModel.getNodeTable().getColumn(propertyKey);
                } else {
                    final AttributeType attributeType = AttributeTypeMapper.map(propertyValue);
                    attributeColumn = attributeModel.getNodeTable().addColumn(propertyKey, attributeType);
                }
                gephiNode.addAttributeValue(attributeColumn, propertyValue);
                report.log(NbBundle.getMessage(GraphDbImporter.class, 
                        "Report_AttributeAdded", propertyKey, propertyValue));
            }
        }
    }
    
    /**
     * Draws all the available Gephi edges. Egde attributes are added as well.
     * @param vertices the vertices for which edges are to be drawn
     */
    private void drawEdges(final Iterable<Vertex> vertices) {
        for (final Vertex vertex : vertices) {
            if (cancel) {
                return;
            }
            final Iterable<Edge> edges = vertex.getEdges(Direction.IN);
            for (final Edge edge : edges) {
                final Vertex start = edge.getVertex(Direction.IN);
                final Vertex end = edge.getVertex(Direction.OUT);
                if (start.getId() == null || end.getId() == null) {
                    final String message = NbBundle.getMessage(GraphDbImporter.class, "Issue_MissingNode");
                    final Issue issue = new Issue(message, Issue.Level.WARNING);
                    report.logIssue(issue);
                    continue;
                }
                final NodeDraft startNode = container.getNode(start.getId().toString());
                final NodeDraft endNode = container.getNode(end.getId().toString());
                final EdgeDraft gephiEdge = container.factory().newEdgeDraft();
                gephiEdge.setSource(startNode);
                gephiEdge.setTarget(endNode);
                container.addEdge(gephiEdge);
                for (final String propertyKey : edge.getPropertyKeys()) {
                    final Object propertyValue = edge.getProperty(propertyKey);
                    AttributeColumn attributeColumn;
                    if (attributeModel.getEdgeTable().hasColumn(propertyKey)) {
                        attributeColumn = attributeModel.getEdgeTable().getColumn(propertyKey);
                    } else {
                        final AttributeType attributeType = AttributeTypeMapper.map(propertyValue);
                        attributeColumn = attributeModel.getEdgeTable().addColumn(propertyKey, attributeType);
                    }
                    gephiEdge.addAttributeValue(attributeColumn, propertyValue);
                    report.log(NbBundle.getMessage(GraphDbImporter.class, 
                            "Report_RelAttributeAdded", propertyKey, propertyValue));
                }
            }        
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDatabase(final Database dtbs) {}

    /** {@inheritDoc} */
    @Override
    public Database getDatabase() {
        return null;
    }
    
    /** Should be thrown when the import of graph database data fails. */
    class ImportException extends RuntimeException {
        
        /** {@inheritDoc} */
        public ImportException(final Throwable cause) {
            super(cause);
        }
        
    }
    
}
