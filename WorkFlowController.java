package com.jsp.smart_automation.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jsp.smart_automation.dto.CrmWorkflowDto;
import com.jsp.smart_automation.dto.WorkFlowStatusDTO;
import com.jsp.smart_automation.entity.CrmWorkFlow;
import com.jsp.smart_automation.service.WorkFlowService;

@RestController
@RequestMapping("/workflow")
public class WorkFlowController {

	private static final Logger logger = LoggerFactory.getLogger(WorkFlowController.class);

	@Autowired
	private WorkFlowService workFlowService;

	@PostMapping("/insertworkflow")
	public ResponseEntity<String> createWorkflow(@RequestBody CrmWorkflowDto workflowDto) {
		try {
			workFlowService.insertWorkFlow(workflowDto);
			return ResponseEntity.ok("Workflow created successfully.");
		} catch (Exception e) {
			logger.error("Error creating workflow: {}", e.getMessage(), e);
			return ResponseEntity.badRequest().body("Error creating workflow: " + e.getMessage());
		}
	}

	@GetMapping("/findByWfCode")
	public ResponseEntity<List<CrmWorkFlow>> findByWfCode(@RequestParam String wfCode) {
		try {
			List<CrmWorkFlow> workflows = workFlowService.findByWfCode(wfCode);
			return ResponseEntity.ok(workflows);
		} catch (Exception e) {
			logger.error("Error fetching workflows by code: {}", e.getMessage(), e);
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/findByWfName")
	public ResponseEntity<List<CrmWorkFlow>> findByWfName(@RequestParam String wfName) {
		try {
			List<CrmWorkFlow> workflows = workFlowService.findByWfName(wfName);
			return ResponseEntity.ok(workflows);
		} catch (Exception e) {
			logger.error("Error fetching workflows by name: {}", e.getMessage(), e);
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping("/updateStatus")
	public ResponseEntity<String> updateStatus(@RequestBody WorkFlowStatusDTO workFlowStatusDTO) {
		try {
			workFlowService.updateStatusFlag(workFlowStatusDTO);
			return ResponseEntity.ok("Workflow status updated successfully.");
		} catch (Exception e) {
			logger.error("Error updating workflow status: {}", e.getMessage(), e);
			return ResponseEntity.badRequest().body("Error updating workflow status: " + e.getMessage());
		}
	}

	@PostMapping("/manualPush")
	public ResponseEntity<String> manualPushTransactionDetails(@RequestBody Map<String, Object> manualPushDetailsMap) {
		try {
			workFlowService.manualPushTransactionDetails(manualPushDetailsMap);
			return ResponseEntity.ok("Manual push transaction details processed successfully.");
		} catch (Exception e) {
			logger.error("Error processing manual push transaction details: {}", e.getMessage(), e);
			return ResponseEntity.badRequest()
					.body("Error processing manual push transaction details: " + e.getMessage());
		}
	}

}
