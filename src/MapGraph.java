import autocomplete.Autocomplete;
import autocomplete.TreeSetAutocomplete;
import graphs.AStarGraph;
import graphs.Edge;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * {@link AStarGraph} of places as {@link Point} vertices and streets edges weighted by physical distance.
 *
 * @see AStarGraph
 * @see MapServer
 */
public class MapGraph implements AStarGraph<Point> {
    private final String osmPath;
    private final String placesPath;
    private final SpatialContext context;
    private final Map<Point, Set<Edge<Point>>> neighbors;
    private final Map<String, List<Point>> locations;
    private final Autocomplete autocomplete;
    private final Map<CharSequence, Integer> importance;

    /**
     * Constructs a new street map graph from the path to an OSM file and a places TSV.
     *
     * @param osmPath    The path to a gzipped OSM (XML) file.
     * @param placesPath The path to a gzipped TSV file representing places and importance.
     * @throws ParserConfigurationException if a parser cannot be created.
     * @throws SAXException                 for SAX errors.
     * @throws IOException                  if a file is not found or if the file is not gzipped.
     */
    public MapGraph(String osmPath, String placesPath, SpatialContext context)
            throws ParserConfigurationException, SAXException, IOException {
        this.osmPath = osmPath;
        this.placesPath = placesPath;
        this.context = context;

        // Parse the OpenStreetMap (OSM) data using the SAXParser XML tree walker.
        neighbors = new HashMap<>();
        Handler handler = new Handler(Set.of(
                "motorway",
                "trunk",
                "primary",
                "secondary",
                "tertiary",
                "unclassified",
                "residential",
                "living_street",
                "motorway_link",
                "trunk_link",
                "primary_link",
                "secondary_link",
                "tertiary_link"
        ));
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        saxParser.parse(new GZIPInputStream(fileStream(osmPath)), handler);

        // Add reachable locations to the Autocomplete engine.
        locations = handler.byName;
        autocomplete = new TreeSetAutocomplete();
        autocomplete.addAll(locations.keySet());

        // Parse the place-importance data.
        importance = new HashMap<>();
        try (Scanner input = new Scanner(new GZIPInputStream(fileStream(placesPath)))) {
            while (input.hasNextLine()) {
                Scanner line = new Scanner(input.nextLine()).useDelimiter("\t");
                importance.put(line.next(), line.nextInt());
            }
        }
    }

    /**
     * Returns an input stream from the contents of the file at the given path.
     *
     * @param path a file path.
     * @return an input stream with the contents of the specified file.
     * @throws FileNotFoundException if there is no file at the specified path.
     */
    private static InputStream fileStream(String path) throws FileNotFoundException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    /**
     * Returns the location closest to the given target location.
     *
     * @param target the target location.
     * @return the id of the location closest to the target.
     */
    public Point closest(Point target) {
        return Collections.min(neighbors.keySet(), Comparator.comparingDouble(
                location -> context.calcDistance(target, location)
        ));
    }

    /**
     * Return the names of all locations that prefix-match the query string.
     *
     * @param prefix prefix string that could be any case with or without punctuation.
     * @return a list of full names of locations matching the prefix.
     */
    public List<CharSequence> getLocationsByPrefix(String prefix) {
        List<CharSequence> result = autocomplete.allMatches(prefix);
        result.sort(Comparator.comparingInt(importance::get));
        return result;
    }

    /**
     * Return all locations that match a valid location name.
     *
     * @param locationName a full name of a valid location.
     * @return a list of locations whose name matches the location name.
     */
    public List<Point> getLocations(String locationName, Point center) {
        if (locationName == null || !locations.containsKey(locationName)) {
            return List.of();
        }
        List<Point> result = new ArrayList<>(locations.get(locationName));
        result.sort(Comparator.comparingDouble(location -> context.calcDistance(center, location)));
        return result;
    }

    @Override
    public List<Edge<Point>> neighbors(Point v) {
        return new ArrayList<>(neighbors.getOrDefault(v, Set.of()));
    }

    @Override
    public double estimatedDistance(Point start, Point end) {
        return context.calcDistance(start, end);
    }

    @Override
    public String toString() {
        return "MapGraph{" +
                "osmPath='" + osmPath + '\'' +
                ", placesPath='" + placesPath + '\'' +
                ", context='" + context + '\'' +
                '}';
    }

    /**
     * Adds an edge to this graph if it doesn't already exist, using distance as the weight.
     */
    private void addEdge(Point from, Point to) {
        if (!neighbors.containsKey(from)) {
            neighbors.put(from, new HashSet<>());
        }
        neighbors.get(from).add(new Edge<>(from, to, estimatedDistance(from, to)));
    }

    /**
     * Parses OSM XML files to construct a StreetMapGraph.
     */
    private class Handler extends DefaultHandler {
        private final Set<String> allowedHighwayTypes;
        private final Map<Long, Point> byId;
        private final Map<String, List<Point>> byName;
        private String state;
        private long id;
        private String name;
        private boolean validWay;
        private Point location;
        private Queue<Point> path;

        Handler(Set<String> allowedHighwayTypes) {
            this.allowedHighwayTypes = allowedHighwayTypes;
            this.byId = new HashMap<>();
            this.byName = new HashMap<>();
            reset();
        }

        /**
         * Reset the handler state before processing a new way or node.
         */
        private void reset() {
            state = "";
            id = Long.MIN_VALUE;
            name = "";
            validWay = false;
            location = null;
            path = new ArrayDeque<>();
        }

        /**
         * Called at the beginning of an element.
         *
         * @param uri        The Namespace URI, or the empty string if the element has no Namespace URI or
         *                   if Namespace processing is not being performed.
         * @param localName  The local name (without prefix), or the empty string if Namespace
         *                   processing is not being performed.
         * @param qName      The qualified name (with prefix), or the empty string if qualified names are
         *                   not available. This tells us which element we're looking at.
         * @param attributes The attributes attached to the element. If there are no attributes, it
         *                   shall be an empty Attributes object.
         * @see Attributes
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("node")) {
                state = "node";
                id = Long.parseLong(attributes.getValue("id"));
                location = context.getShapeFactory().pointLatLon(
                        Double.parseDouble(attributes.getValue("lat")),
                        Double.parseDouble(attributes.getValue("lon"))
                );
            } else if (qName.equals("way")) {
                state = "way";
            } else if (state.equals("way") && qName.equals("nd")) {
                long ref = Long.parseLong(attributes.getValue("ref"));
                path.add(byId.get(ref));
            } else if (state.equals("way") && qName.equals("tag")) {
                String k = attributes.getValue("k");
                String v = attributes.getValue("v");
                if (k.equals("highway")) {
                    validWay = allowedHighwayTypes.contains(v);
                }
            } else if (state.equals("node") && qName.equals("tag") && attributes.getValue("k").equals("name")) {
                name = attributes.getValue("v").strip()
                        .replace('“', '"')
                        .replace('”', '"')
                        .replace('‘', '\'')
                        .replace('’', '\'');
            }
        }

        /**
         * Called at the end of an element.
         *
         * @param uri       The Namespace URI, or the empty string if the element has no Namespace URI or
         *                  if Namespace processing is not being performed.
         * @param localName The local name (without prefix), or the empty string if Namespace
         *                  processing is not being performed.
         * @param qName     The qualified name (with prefix), or the empty string if qualified names are
         *                  not available.
         */
        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("way")) {
                if (validWay && !path.isEmpty()) {
                    Point from = path.remove();
                    while (!path.isEmpty()) {
                        Point to = path.remove();
                        addEdge(from, to);
                        addEdge(to, from);
                        from = to;
                    }
                }
                reset();
            } else if (qName.equals("node")) {
                byId.put(id, location);
                if (!name.isBlank()) {
                    byName.putIfAbsent(name, new ArrayList<>());
                    byName.get(name).add(location);
                }
                reset();
            }
        }
    }
}
