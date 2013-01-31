package filters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class OSMObjects {
	// TODO: change to dependency injection
	private enum RelevantKeys {
		amenity, building, highway, Leisure, railway_station, railway_entrance
	}

	public static class TagContainer {
		private final String id;
		protected final Map<String, String> tags = new HashMap<>();

		public TagContainer(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public Map<String, String> getTags() {
			return tags;
		}

		@Override
		public boolean equals(Object that) {
			return (that != null && that instanceof TagContainer) ? this
					.equals((TagContainer) that) : false;
		}

		public boolean equals(TagContainer that) {
			return this.id.equals(that.id);
		}

		@Override
		public int hashCode() {
			return this.id.hashCode();
		}
	}

	public static class ComplexTagContainer extends TagContainer {

		private final Map<String, Integer> nodesIdxs = new HashMap<>();
		private List<TagContainer> nodes = new LinkedList<>();

		public ComplexTagContainer(String id) {
			super(id);
		}

		public void setNodes(List<TagContainer> nodes) {
			nodes.clear();
			nodesIdxs.clear();

			this.nodes = nodes;
			int i = 0;
			for (TagContainer node : nodes) {
				nodesIdxs.put(node.id, i++);
			}
		}

		public List<TagContainer> getNodes() {
			return this.nodes;
		}

		public Map<String, Integer> getNodexIdxs() {
			return this.nodesIdxs;
		}
	}

	public static class Way extends ComplexTagContainer {
		public Way(String id) {
			super(id);
		}
	}

	public static class Relation extends ComplexTagContainer {
		public Relation(String id) {
			super(id);
		}
	}
}