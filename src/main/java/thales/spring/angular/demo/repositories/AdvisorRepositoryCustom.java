package thales.spring.angular.demo.repositories;

import java.util.List;

import thales.spring.angular.demo.domain.Advisor;

public interface AdvisorRepositoryCustom {

	public List<Advisor> getAdvisorByClientId(String idAdvisor,String idClient);
	
	public List<Advisor> getAdvisorById(String idAdvisor);

}
