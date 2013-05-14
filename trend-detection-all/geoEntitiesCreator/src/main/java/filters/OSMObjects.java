package filters;

import java.util.*;

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

        // For saving order between nodes (boundary Creation)
		private final Map<String, Set<Integer>> nodesIdxs = new HashMap<>();
		private List<TagContainer> nodes = new ArrayList<>();

		public ComplexTagContainer(String id) {
			super(id);
		}

        public void addNode(TagContainer node){
            nodes.add(node);
            if (!nodesIdxs.containsKey(node.getId())){
                nodesIdxs.put(node.getId(), new HashSet<Integer>());
            }
            nodesIdxs.get(node.getId()).add(nodes.size() - 1);
        }

        public Collection<TagContainer> getNodesById(String id){
            List<TagContainer> result = new ArrayList<>();
            for(Integer i:nodesIdxs.get(id)){
                result.add(nodes.get(i));
            }
            return result;
        }

		public List<TagContainer> getNodes() {
			return this.nodes;
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