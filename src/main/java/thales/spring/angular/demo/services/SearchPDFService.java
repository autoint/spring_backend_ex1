package thales.spring.angular.demo.services;

import java.util.List;

import thales.spring.angular.demo.domain.FilePDFReport;

public interface SearchPDFService {

	List<FilePDFReport> getPDFsLike(String regex);
}
