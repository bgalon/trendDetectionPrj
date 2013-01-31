package filters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import config.Configurtaion;

public class BoudaryExtractor {
	Set<Boundary> $ = new HashSet<Boundary>();

	/**
	 * TODO: document
	 * 
	 * @param level
	 * @param destFile
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	Set<Boundary> extractBoundary(int level, File destFile) throws SAXException, IOException {
		if (destFile == null || !destFile.exists())
			throw new IllegalArgumentException();

		$.clear();

		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(new BoundaryFinder(level));
		reader.parse(new InputSource(new FileInputStream(destFile)));

		return $;
	}

	class BoundaryFinder implements ContentHandler {
		private static final String ADMIN_LEVEL = "admin_level";
		private static final String KEY = "k";
		private static final String VALUE = "v";
		private static final String ID = "id";
		private static final String RELATION = "relation";
		private static final String WAY = "way";
		private static final String TAG = "tag";
		private final int level;
		boolean inWay = false, inRelation = false;
		private final StringBuilder content = new StringBuilder();
		Boundary curr = null;

		public BoundaryFinder(int level) {
			this.level = level;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts)
				throws SAXException {
			switch (localName) {
			case WAY:
				inWay = true;
				curr = new Way(atts.getValue(ID), level);
				break;
			case RELATION:
				inRelation = true;
				curr = new Way(atts.getValue(ID), level);
				break;
			case TAG:
				if (inWay || inRelation) {
					String key = atts.getValue(KEY), value = atts.getValue(VALUE);
					if (key != null && value != null) {
						curr.getTags().put(key, value);
						if (key.equals(ADMIN_LEVEL) && Integer.valueOf(value) != level) {
							curr = null;
							inWay = inRelation = false;
						}
					}
				}
				break;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (inWay && localName.equals(WAY)) {
				inWay = false;
				addToBoundaryList();
			} else if (inRelation && localName.equals(RELATION)) {
				inRelation = false;
				addToBoundaryList();
			}
		}

		private void addToBoundaryList() {
			if (curr != null) {
				if (curr.getTags().containsKey(ADMIN_LEVEL))
					$.add(curr);
				curr = null;
			}
		}

		@Override
		public void endDocument() throws SAXException {
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			content.append(ch, start, length);
		}

		@Override
		public void setDocumentLocator(Locator locator) {
		}

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		}

		@Override
		public void processingInstruction(String target, String data) throws SAXException {
		}

		@Override
		public void skippedEntity(String name) throws SAXException {
		}
	}

//	public class TagContainer {
//		public String getId() {
//			return id;
//		}
//
//		public Map<String, String> getTags() {
//			return tags;
//		}
//
//		private final String id;
//		protected final Map<String, String> tags = new HashMap<>();
//
//		public TagContainer(String id) {
//			this.id = id;
//		}
//	}

//	public class Boundary extends TagContainer {
//		private final int level;
//
//		public Boundary(String id, int level) {
//			super(id);
//			this.level = level;
//		}
//
//		public int getLevel() {
//			return level;
//		}
//	}
//
//	public class Way extends Boundary {
//
//		public Way(String id, int level) {
//			super(id, level);
//		}
//	}
//
//	public class Relation extends Boundary {
//		public Relation(String id, int level) {
//			super(id, level);
//		}
	}

	public static void main(String[] args) throws SAXException, IOException {
		System.out.println(new BoudaryExtractor().extractBoundary(8,
				new File(Configurtaion.OSM_FILE)).size());
	}
}
