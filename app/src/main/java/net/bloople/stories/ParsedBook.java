package net.bloople.stories;

import org.commonmark.node.Heading;
import org.commonmark.node.Node;

import java.util.ArrayList;
import java.util.List;

class ParsedBook {
    private List<Node> nodes = new ArrayList<>();
    private List<Node> outlineNodes = new ArrayList<>();
    private List<Integer> outlineNodesMap = new ArrayList<>();

    ParsedBook() {
    }

    Node get(int index) {
        return nodes.get(index);
    }

    int size() {
        return nodes.size();
    }

    Node getOutlineNode(int index) {
        return outlineNodes.get(index);
    }

    int outlineNodesSize() {
        return outlineNodes.size();
    }

    int indexOfOutlineNodeIndex(int outlineIndex) {
        return outlineNodesMap.get(outlineIndex);
    }

    void addNodesAll(List<Node> newNodes) {
        int countBefore = nodes.size();

        nodes.addAll(newNodes);

        for(int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);

            Heading heading = NodesHelper.findFirstHeading(node);
            if(heading != null) {
                outlineNodes.add(heading);
                outlineNodesMap.add(countBefore + i);
            }
        }
    }
}
