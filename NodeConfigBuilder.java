package com.jsp.smart_automation.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsp.smart_automation.config.NodeConfig;
import com.jsp.smart_automation.entity.NodeDetailsModel;

public class NodeConfigBuilder {

	public List<NodeConfig> getNodeConfig(List<NodeDetailsModel> nodeDetailsList) {
		Map<String, NodeConfig> nodeConfigs = new HashMap<>();
		for (NodeDetailsModel nodeDetails : nodeDetailsList) {
			if (!nodeConfigs.containsKey(nodeDetails.getNodeId())) {
				buildNodeConfig(nodeDetails, nodeDetailsList, nodeConfigs);
			} else {
				NodeConfig nodeConfig = nodeConfigs.get(nodeDetails.getNodeId());
				if (outgoingNodesExist(nodeDetails)) {
					addOutgoingNodes(nodeConfig, nodeDetails, nodeDetailsList, nodeConfigs);
				}
				if (incomingNodesExist(nodeDetails)) {
					addIncomingNodes(nodeConfig, nodeDetails, nodeDetailsList, nodeConfigs);
				}
			}
		}
		return new ArrayList<>(nodeConfigs.values());
	}

	private void buildNodeConfig(NodeDetailsModel nodeDetails, List<NodeDetailsModel> nodeDetailsList,
			Map<String, NodeConfig> nodeConfigs) {
		NodeConfig nodeConfig = new NodeConfig();
		nodeConfig.setNodeId(nodeDetails.getNodeId());
		nodeConfig.setNodeType(nodeDetails.getNodeType());
		nodeConfig.setNodeProperties(nodeDetails.getNodeProperties());

		if ("start".equals(nodeDetails.getNodeType())) {
			nodeConfig.setStartNode(true);
		}
		if ("end".equals(nodeDetails.getNodeType())) {
			nodeConfig.setEndNode(true);
		}

		nodeConfigs.put(nodeDetails.getNodeId(), nodeConfig);

		if (outgoingNodesExist(nodeDetails)) {
			addOutgoingNodes(nodeConfig, nodeDetails, nodeDetailsList, nodeConfigs);
		}
		if (incomingNodesExist(nodeDetails)) {
			addIncomingNodes(nodeConfig, nodeDetails, nodeDetailsList, nodeConfigs);
		}
	}

	private void addIncomingNodes(NodeConfig nodeConfig, NodeDetailsModel nodeDetails,
			List<NodeDetailsModel> nodeDetailsList, Map<String, NodeConfig> nodeConfigs) {
		List<String> incomingNodes = nodeDetails.getIncomingNodes();
		if (incomingNodes == null) {
			return;
		}
		for (String incomingNode : incomingNodes) {
			if (incomingNodeExistsWithNodeConfig(incomingNode, nodeConfig)) {
				continue;
			}
			if (!nodeConfigs.containsKey(incomingNode)) {
				NodeDetailsModel incomingNodeDetails = getNodeDetails(incomingNode, nodeDetailsList);
				if (incomingNodeDetails == null) {
					continue;
				}
				buildNodeConfig(incomingNodeDetails, nodeDetailsList, nodeConfigs);
			}
			nodeConfig.addIncomingNode(nodeConfigs.get(incomingNode));
		}
	}

	private boolean incomingNodeExistsWithNodeConfig(String incomingNode, NodeConfig nodeConfig) {
		if (nodeConfig.getIncomingNodes() != null) {
			for (NodeConfig incomingNodeConfig : nodeConfig.getIncomingNodes()) {
				if (incomingNodeConfig.getNodeId().equals(incomingNode)) {
					return true;
				}
			}
		}
		return false;
	}

	private NodeDetailsModel getNodeDetails(String nodeId, List<NodeDetailsModel> nodeDetailsList) {
		for (NodeDetailsModel nodeDetails : nodeDetailsList) {
			if (nodeDetails.getNodeId().equals(nodeId)) {
				return nodeDetails;
			}
		}
		return null;
	}

	private boolean incomingNodesExist(NodeDetailsModel nodeDetails) {
		return nodeDetails.getIncomingNodes() != null;
	}

	private boolean outgoingNodesExist(NodeDetailsModel nodeDetails) {
		return nodeDetails.getOutgoingNodes() != null;
	}

	private void addOutgoingNodes(NodeConfig nodeConfig, NodeDetailsModel nodeDetails,
			List<NodeDetailsModel> nodeDetailsList, Map<String, NodeConfig> nodeConfigs) {
		List<String> outgoingNodes = nodeDetails.getOutgoingNodes();
		if (outgoingNodes == null) {
			return;
		}
		for (String outgoingNode : outgoingNodes) {
			if (outgoingNodeExistsWithNodeConfig(outgoingNode, nodeConfig)) {
				continue;
			}
			if (!nodeConfigs.containsKey(outgoingNode)) {
				NodeDetailsModel outgoingNodeDetails = getNodeDetails(outgoingNode, nodeDetailsList);
				if (outgoingNodeDetails == null) {
					continue;
				}
				buildNodeConfig(outgoingNodeDetails, nodeDetailsList, nodeConfigs);
			}
			nodeConfig.addOutgoingNode(nodeConfigs.get(outgoingNode));
		}
	}

	private boolean outgoingNodeExistsWithNodeConfig(String outgoingNode, NodeConfig nodeConfig) {
		if (nodeConfig.getOutgoingNodes() != null) {
			for (NodeConfig outgoingNodeConfig : nodeConfig.getOutgoingNodes()) {
				if (outgoingNodeConfig.getNodeId().equals(outgoingNode)) {
					return true;
				}
			}
		}
		return false;
	}
}
