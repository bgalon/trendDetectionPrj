package filters;

import filters.OSMObjects.ComplexTagContainer;
import filters.OSMObjects.TagContainer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;
import java.util.Set;

class NodesFinder extends DefaultHandler {

	private final Map<String, Set<ComplexTagContainer>> nodes;

	public NodesFinder(Map<String, Set<ComplexTagContainer>> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException {
		if (localName.equals("node")) {
			String id = atts.getValue("id");
			if (nodes.containsKey(id)) {
				updatePolygons(atts, id);
			}
		}
	}

	private void updatePolygons(Attributes atts, String id) {
		for (ComplexTagContainer ctc : nodes.get(id)) {
            for(TagContainer toUpdate:ctc.getNodesById(id)){
			    toUpdate.getTags().put("lat", atts.getValue("lat"));
			    toUpdate.getTags().put("lon", atts.getValue("lon"));
            }
		}
	}
}
