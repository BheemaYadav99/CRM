package com.jsp.smart_automation.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeConfig implements Cloneable {

	private Map<String, String> nodeProperties;
	private String nodeId;
	private String nodeType;
	private List<NodeConfig> outgoingNodes;
	private List<NodeConfig> incomingNodes;
	private NodeConfig sourceNodeConfig;
	private boolean isEndNode;
	private boolean isStartNode;
	private String Remarks;

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public Map<String, String> getNodeProperties() {
		return nodeProperties;
	}

	public void setNodeProperties(Map<String, String> nodeProperties) {
		this.nodeProperties = nodeProperties;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public List<NodeConfig> getOutgoingNodes() {
		return outgoingNodes;
	}

	public void addOutgoingNode(NodeConfig outgoingNode) {
		if (outgoingNodes == null)
			outgoingNodes = new ArrayList<>();
		outgoingNodes.add(outgoingNode);
	}

	public void setOutgoingNodes(List<NodeConfig> outgoingNodes) {
		this.outgoingNodes = outgoingNodes;
	}

	public List<NodeConfig> getIncomingNodes() {
		return incomingNodes;
	}

	public void setIncomingNodes(List<NodeConfig> incomingNodes) {
		this.incomingNodes = incomingNodes;
	}

	public void addIncomingNode(NodeConfig incomingNode) {
		if (incomingNodes == null)
			incomingNodes = new ArrayList<>();
		incomingNodes.add(incomingNode);
	}

	public NodeConfig getSourceNodeConfig() {
		return sourceNodeConfig;
	}

	public void setSourceNodeConfig(NodeConfig sourceNodeConfig) {
		this.sourceNodeConfig = sourceNodeConfig;
	}

	public boolean isEndNode() {
		return isEndNode;
	}

	public void setEndNode(boolean isEndNode) {
		this.isEndNode = isEndNode;
	}

	public boolean isStartNode() {
		return isStartNode;
	}

	public void setStartNode(boolean isStartNode) {
		this.isStartNode = isStartNode;
	}

	public String getPropertyAsString(String key) {
		return this.nodeProperties != null ? this.nodeProperties.get(key) : null;
	}

	// Cloning Method
	@Override
	public NodeConfig clone() {
		try {
			NodeConfig cloned = (NodeConfig) super.clone();

			// Deep cloning mutable fields
			if (this.nodeProperties != null) {
				cloned.nodeProperties = new HashMap<>(this.nodeProperties);
			}

			if (this.outgoingNodes != null) {
				cloned.outgoingNodes = new ArrayList<>();
				for (NodeConfig outgoingNode : this.outgoingNodes) {
					cloned.outgoingNodes.add(outgoingNode.clone());
				}
			}

			if (this.incomingNodes != null) {
				cloned.incomingNodes = new ArrayList<>();
				for (NodeConfig incomingNode : this.incomingNodes) {
					cloned.incomingNodes.add(incomingNode.clone());
				}
			}

			cloned.sourceNodeConfig = this.sourceNodeConfig != null ? this.sourceNodeConfig.clone() : null;

			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
