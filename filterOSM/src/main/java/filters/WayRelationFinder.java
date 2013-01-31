package filters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import filters.OSMObjects.ComplexTagContainer;
import filters.OSMObjects.TagContainer;
import filters.OSMObjects.Way;

class WaysFinder extends DefaultHandler {

	private final ElementState state = new ElementState();
	private final Set<ComplexTagContainer> elements;
	private final Map<String, Set<ComplexTagContainer>> nodes;

	public WaysFinder(Set<ComplexTagContainer> elements,
			Map<String, Set<ComplexTagContainer>> nodes) {
		this.elements = elements;
		this.nodes = nodes;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException {
		try {
			OSMKeys.valueOf(localName.toUpperCase()).handleStartElement(state, atts);
		} catch (IllegalArgumentException e) {
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			OSMKeys.valueOf(localName.toUpperCase()).handleEndElement(state, nodes);
			if (state.curr != null)
				this.elements.add(state.curr);
		} catch (IllegalArgumentException e) {
		}
	}

	public enum OSMKeys {
		WAY {
			@Override
			public void handleStartElement(ElementState state, Attributes atts) {
				state.inWay = true;
				state.nodes.clear();
				state.curr = new Way(atts.getValue("id"));
			}

			@Override
			public void handleEndElement(ElementState state,
					Map<String, Set<ComplexTagContainer>> nodes) {
				state.inWay = false;
				nodes.putAll(state.nodes);
			}
		},
		ND {
			@Override
			public void handleStartElement(ElementState state, Attributes atts) {
				if (state.inWay) {
					String ref = atts.getValue("ref");
					if (ref != null) {
						state.curr.getNodes().add(new TagContainer(ref));
						if (!state.nodes.containsKey(ref))
							state.nodes.put(ref, new HashSet<ComplexTagContainer>());
						state.nodes.get(ref).add(state.curr);
					}
				}
			}
		},
		TAG {
			@Override
			public void handleStartElement(ElementState state, Attributes atts) {
				if (state.inWay) {
					String key = atts.getValue("k"), value = atts.getValue("v");
					if (key != null && value != null)
						state.curr.getTags().put(key, value);
				}
			}
		};

		public abstract void handleStartElement(ElementState state, Attributes atts);

		public void handleEndElement(ElementState state, Map<String, Set<ComplexTagContainer>> nodes) {
		}

	}

	private class ElementState {
		public boolean inWay = false, inRelation = false;
		public ComplexTagContainer curr = null;
		public final Map<String, Set<ComplexTagContainer>> nodes = new HashMap<>();
	}
}
