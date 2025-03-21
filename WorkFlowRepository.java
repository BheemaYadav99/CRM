package com.jsp.smart_automation.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.smart_automation.entity.CrmWorkFlow;

import java.util.List;
import java.util.Optional;

public interface WorkFlowRepository extends JpaRepository<CrmWorkFlow, Long> {
    List<CrmWorkFlow> findByWfCode(String wfCode);
    List<CrmWorkFlow> findByWfName(String wfName);
    Optional<CrmWorkFlow> findFirstByWfName(String wfName);
	CrmWorkFlow findByWfId(String wfId);
	List<CrmWorkFlow> findByWfCodeOrWfName(String wfCode, String wfName);
}
