package com.jsp.smart_automation.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jsp.smart_automation.context.WorkflowTransactionContext;
import com.jsp.smart_automation.dto.CrmWorkflowDto;
import com.jsp.smart_automation.dto.WorkFlowStatusDTO;
import com.jsp.smart_automation.entity.CrmWorkFlow;
import com.jsp.smart_automation.entity.EntityModel;
import com.jsp.smart_automation.entity.NodeDetailsModel;
import com.jsp.smart_automation.entity.WorkflowTransactionLogModel;
import com.jsp.smart_automation.repository.NodeDetailsModelRepository;
import com.jsp.smart_automation.repository.WorkFlowRepository;
import com.jsp.smart_automation.repository.WorkflowTransactionLogRepository;
import com.jsp.smart_automation.service.PushTransactionTaskService;
import com.jsp.smart_automation.service.WorkFlowService;
import com.jsp.smart_automation.util.Handler;

@Service
public class WorkFlowServiceIMPL implements WorkFlowService {

	private static final Logger logger = LoggerFactory.getLogger(WorkFlowServiceIMPL.class);

	@Autowired
	private PushTransactionTaskService pushTransactionTaskService;

	@Autowired
	private WorkFlowRepository workFlowRepository;

	@Autowired
	private WorkflowTransactionLogRepository workflowTransactionLogRepository;

	@Autowired
	private NodeDetailsModelRepository nodedetailsModelRepository;

	@Autowired
	private Handler handler;

	private static final String STATUS_DRAFT = "Draft";
	private static final String INITIAL_VERSION = "0";

	@Override
	public void insertWorkFlow(CrmWorkflowDto workflowDto) {
		if (workflowDto == null) {
			throw new IllegalArgumentException("Workflow data must not be null.");
		}

		List<CrmWorkFlow> existingWorkFlows = workFlowRepository.findByWfCodeOrWfName(workflowDto.getWfCode(),
				workflowDto.getWfName());
		if (existingWorkFlows != null && !existingWorkFlows.isEmpty()) {
			throw new IllegalArgumentException("A workflow with the same wfCode or wfName already exists.");
		}

		CrmWorkFlow newWorkFlowEntity = new CrmWorkFlow();
		newWorkFlowEntity.setStatusFlag(STATUS_DRAFT);
		newWorkFlowEntity.setVersion(0);
		String initialWfId = workflowDto.getWfCode() + "_" + INITIAL_VERSION;
		newWorkFlowEntity.setWfId(initialWfId);
		newWorkFlowEntity.setWfCode(workflowDto.getWfCode());
		newWorkFlowEntity.setCreatedDate(new Date());
		newWorkFlowEntity.setWfName(workflowDto.getWfName());
		newWorkFlowEntity.setEntityCode(workflowDto.getEntityCode());
		newWorkFlowEntity.setSourceData(workflowDto.getSourceData());
		newWorkFlowEntity.setUnqueField(workflowDto.getUniqueField());

		workFlowRepository.save(newWorkFlowEntity);
	}

	@Override
	public List<CrmWorkFlow> findByWfCode(String wfCode) {
		return workFlowRepository.findByWfCode(wfCode);
	}

	@Override
	public List<CrmWorkFlow> findByWfName(String wfName) {
		return workFlowRepository.findByWfName(wfName);
	}

	@Transactional
	@Override
	public void updateStatusFlag(WorkFlowStatusDTO workflowDto) {
		List<CrmWorkFlow> existingWorkFlows = workFlowRepository.findByWfCode(workflowDto.getWfCode());
		if (existingWorkFlows != null && !existingWorkFlows.isEmpty()) {
			boolean isDraftFound = false;
			int highestVersion = existingWorkFlows.stream().mapToInt(CrmWorkFlow::getVersion).max().orElse(0);

			for (CrmWorkFlow workFlow : existingWorkFlows) {
				if (!STATUS_DRAFT.equalsIgnoreCase(workFlow.getStatusFlag())) {
					workFlow.setStatusFlag("Inactive");
					workFlowRepository.save(workFlow);
				} else {
					isDraftFound = true;
				}
			}

			CrmWorkFlow workflowToUpdate = workFlowRepository.findByWfId(workflowDto.getWfId());
			if (workflowToUpdate != null) {
				if (!STATUS_DRAFT.equalsIgnoreCase(workflowToUpdate.getStatusFlag())) {
					workflowToUpdate.setStatusFlag(workflowDto.getStatusFlag());
					workflowToUpdate.setModifiedDate(new Date());
					workFlowRepository.save(workflowToUpdate);
				} else {
					int newVersion = highestVersion + 1;
					String newWfCode = workflowToUpdate.getWfCode() + "_" + newVersion;

					CrmWorkFlow newWorkFlow = new CrmWorkFlow();
					newWorkFlow.setWfCode(workflowToUpdate.getWfCode());
					newWorkFlow.setStatusFlag("Active");
					newWorkFlow.setModifiedDate(new Date());
					newWorkFlow.setCreatedDate(new Date());
					newWorkFlow.setVersion(newVersion);
					newWorkFlow.setWfId(newWfCode);
					newWorkFlow.setWfName(workflowToUpdate.getWfName());
					newWorkFlow.setSourceData(workflowToUpdate.getSourceData());
					newWorkFlow.setEntityCode(workflowToUpdate.getEntityCode());
					newWorkFlow.setRemark(workflowToUpdate.getRemark());
					newWorkFlow.setCreatedBy(workflowToUpdate.getCreatedBy());
					newWorkFlow.setModifiedBy(workflowToUpdate.getModifiedBy());
					newWorkFlow.setUnqueField(workflowToUpdate.getUnqueField());
					workFlowRepository.save(newWorkFlow);

					try {
						List<NodeDetailsModel> nodeDetailsModels = handler
								.convertWfXmlToNodeDetailsModel(workflowToUpdate.getSourceData());
						for (NodeDetailsModel nodeDetails : nodeDetailsModels) {
							nodeDetails.setWfId(newWfCode);
							nodedetailsModelRepository.save(nodeDetails);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out.println("New workflow created with wfId: " + newWfCode);
				}
			}

			if (isDraftFound) {
				System.out.println("Draft records were processed; new records have been created.");
			}
		}
	}

	@Override
	public void manualPushTransactionDetails(Map<String, Object> manualPushDetailsMap) {
		logger.debug("Received manual push details map: {}", manualPushDetailsMap);

		@SuppressWarnings("unchecked")
		Map<String, Object> additionalProps = (Map<String, Object>) manualPushDetailsMap.get("additionalProp1");

		if (additionalProps == null) {
			logger.error("Error: additionalProp1 is null.");
			return;
		}

		String wfId = (String) additionalProps.get("wfId");
		String uniqueValue = (String) additionalProps.get("uniqueValue");

		logger.info("Processing manual push with wfId: {}, uniqueValue: {}", wfId, uniqueValue);

		if (wfId == null || uniqueValue == null) {
			logger.error("Error: wfId or uniqueValue is null.");
			return;
		}

		CrmWorkFlow workflowModel = getWorkflowByWfId(wfId);
		if (workflowModel == null) {
			logger.error("Workflow not found for wfId: {}", wfId);
			return;
		}

		EntityModel entityModel = getEntityByEntityCode(workflowModel.getEntityCode());
		if (entityModel == null) {
			logger.error("Entity not found for entityCode: {}", workflowModel.getEntityCode());
			return;
		}

		logger.info("Fetched WorkflowModel: {}, EntityModel: {}", workflowModel, entityModel);

		pushTransactionTaskService.processManualTransactionData(workflowModel, entityModel, uniqueValue);
	}

	private CrmWorkFlow getWorkflowByWfId(String wfId) {
		logger.debug("Fetching WorkflowModel for wfId: {}", wfId);
		return workFlowRepository.findByWfId(wfId);
	}

	private EntityModel getEntityByEntityCode(String entityCode) {
		logger.debug("Fetching EntityModel for entityCode: {}", entityCode);
		return pushTransactionTaskService.getEntityByEntityCode(entityCode);
	}

	@Transactional
	@Override
	public void execute(WorkflowTransactionContext context, CrmWorkFlow crmWorkFlow, String uniqueValue) {
		if (context == null || crmWorkFlow == null || uniqueValue == null) {
			throw new IllegalArgumentException(
					"Invalid input: context, CrmWorkFlow, and uniqueValue must not be null.");
		}

		String wfId = context.getCrmworkflow().getWfId();

		CrmWorkFlow workflowModel = getWorkflowByWfId(wfId);
		if (workflowModel == null) {
			logger.error("Workflow not found for wfId: {}", wfId);
			throw new IllegalArgumentException("Workflow not found for wfId: " + wfId);
		}

		EntityModel entityModel = getEntityByEntityCode(workflowModel.getEntityCode());
		if (entityModel == null) {
			logger.error("Entity not found for entityCode: {}", workflowModel.getEntityCode());
			throw new IllegalArgumentException("Entity not found for entityCode: " + workflowModel.getEntityCode());
		}

		WorkflowTransactionLogModel workflowTransactionLog = new WorkflowTransactionLogModel();
		workflowTransactionLog.setWfId(workflowModel.getWfId());
		workflowTransactionLog.setTransactionUniqueValue(uniqueValue);
		workflowTransactionLog.setStatusFlag("In Progress");
		workflowTransactionLog.setCreatedDate(new Date());
		workflowTransactionLog.setModifiedDate(new Date());

		workflowTransactionLogRepository.save(workflowTransactionLog);
	}

}
