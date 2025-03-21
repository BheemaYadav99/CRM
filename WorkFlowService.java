package com.jsp.smart_automation.service;

import java.util.List;
import java.util.Map;

import com.jsp.smart_automation.context.WorkflowTransactionContext;
import com.jsp.smart_automation.dto.CrmWorkflowDto;
import com.jsp.smart_automation.dto.WorkFlowStatusDTO;
import com.jsp.smart_automation.entity.CrmWorkFlow;

public interface WorkFlowService {
	public void insertWorkFlow(CrmWorkflowDto workflowDto);

	public List<CrmWorkFlow> findByWfCode(String wfCode);

	public List<CrmWorkFlow> findByWfName(String wfName);

	public void updateStatusFlag(WorkFlowStatusDTO workflowDto);

	public void manualPushTransactionDetails(Map<String, Object> manualPushDetailsMap);

	void execute(WorkflowTransactionContext context, CrmWorkFlow crmWorkFlow, String uniqueValue);

}
