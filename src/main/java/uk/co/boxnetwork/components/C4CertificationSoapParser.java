package uk.co.boxnetwork.components;

import java.io.CharArrayReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.boxnetwork.data.C4Metadata;
import uk.co.boxnetwork.model.CertificationCategory;
import uk.co.boxnetwork.model.CertificationTime;
import uk.co.boxnetwork.model.CertificationType;
import uk.co.boxnetwork.model.ComplianceInformation;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ProgrammeCertification;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.util.GenericUtilities;

@Component
public class C4CertificationSoapParser {
	private static final Logger logger=LoggerFactory.getLogger(C4CertificationSoapParser.class);
	private String compliancepath[]={"GetComplianceInformationForAssetResponse","GetComplianceInformationForAssetResult"};
	
	@Autowired
	private XMLDocumentParser xmlparser;
		
	public Set<ComplianceInformation> parse(String complianceDocument) throws DocumentException{
		SAXReader reader = new SAXReader();		
			Document document=reader.read(new CharArrayReader((complianceDocument.toCharArray())));
			return parse(document);
	}
	
	public Set<ComplianceInformation> parse(Document document){
		Element comtainerElement=xmlparser.getElementByPaths(document,compliancepath);
		
		Set<ComplianceInformation> compliances=new HashSet<ComplianceInformation>();
		
		
		for ( Iterator<Element> i = comtainerElement.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("ComplianceInformation")){
				ComplianceInformation compliance=processComplianceInformation(elem);
				compliances.add(compliance);
		    }
	     }
		return compliances;
	}	
	public ComplianceInformation processComplianceInformation(Element complianceElement){
		if(complianceElement==null){
			return null;			
		}		
		ComplianceInformation complianceInformation=new ComplianceInformation();
		
		complianceInformation.setIssueNumber(xmlparser.getElementContentByPaths(complianceElement, "IssueNumber"));
		complianceInformation.setMaterialId(xmlparser.getElementContentByPaths(complianceElement, "MaterialId"));
		complianceInformation.setProgrammeVersionCertification(xmlparser.getElementContentByPaths(complianceElement, "ProgrammeVersionCertification"));
		complianceInformation.setTransmissionDate(xmlparser.getElementContentByPaths(complianceElement, "TransmissionDate"));
		complianceInformation.setId(xmlparser.getElementContentByPaths(complianceElement, "id"));
		complianceInformation.setAssetId(xmlparser.getElementContentByPaths(complianceElement, "AssetId"));
		complianceInformation.setViewerUserKey(xmlparser.getElementContentByPaths(complianceElement, "ViewerUserKey"));
		complianceInformation.setCertificationFinished(xmlparser.getElementContentByPaths(complianceElement, "CertificationFinished"));
		complianceInformation.setEditedOn(xmlparser.getElementContentByPaths(complianceElement, "EditedOn"));
		Element programmeCertifications=xmlparser.getElementByPaths(complianceElement, "ProgrammeCertifications");
		if(programmeCertifications!=null){			
			for ( Iterator<Element> i = programmeCertifications.elementIterator(); i.hasNext(); ) {
				Element elem = (Element) i.next();
				if(elem.getName().equals("Certification")){
					ProgrammeCertification certfication=processCertification(elem);
					if(certfication!=null){
						complianceInformation.addProgrammeCertification(certfication);
					}
			    }
		     }
		}	
		return complianceInformation;
	}
	private ProgrammeCertification processCertification(Element certficationElement){		
		if(certficationElement==null){
			return null;			
		}
		ProgrammeCertification certication=new ProgrammeCertification();
		certication.setCertificationDate(xmlparser.getElementContentByPaths(certficationElement, "CertificationDate"));
		certication.setCertificationEndDate(xmlparser.getElementContentByPaths(certficationElement, "CertificationEndDate"));
		certication.setComplianceObservationId(xmlparser.getElementContentByPaths(certficationElement, "ComplianceObservationId"));
		certication.setCreatedBy(xmlparser.getElementContentByPaths(certficationElement, "CreatedBy"));
		certication.setCreatedOn(xmlparser.getElementContentByPaths(certficationElement, "CreatedOn"));
		certication.setId(xmlparser.getElementContentByPaths(certficationElement, "Id"));
		certication.setModifiedBy(xmlparser.getElementContentByPaths(certficationElement, "ModifiedBy"));
		certication.setModifiedOn(xmlparser.getElementContentByPaths(certficationElement, "ModifiedOn"));
		for ( Iterator<Element> i = certficationElement.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("CertificationType")){
				certication.setCertificationType(processCertificationType(elem));				
		    }
	     }
		return certication;
	}
	private CertificationType processCertificationType(Element certificationTypeElemeent){
		if(certificationTypeElemeent==null){
			return null;
		}
		CertificationType certificationType=new CertificationType();
		certificationType.setCliKey(xmlparser.getElementContentByPaths(certificationTypeElemeent, "CliKey"));
		certificationType.setCode(xmlparser.getElementContentByPaths(certificationTypeElemeent, "Code"));
		certificationType.setDescription(xmlparser.getElementContentByPaths(certificationTypeElemeent, "Description"));
		certificationType.setId(xmlparser.getElementContentByPaths(certificationTypeElemeent, "Id"));
		for ( Iterator<Element> i = certificationTypeElemeent.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("CertificationCategory")){
				certificationType.setCertificationCategory(processCertificationCategory(elem));				
		    }
			else if(elem.getName().equals("CertificationTimes")){
				for ( Iterator<Element> j = elem.elementIterator(); j.hasNext(); ) {
					Element telem = (Element) j.next();
					if(telem.getName().equals("a:CertificationTime")){
						certificationType.addCertificationTime(processCertificationTime(telem));
					}
					
				}
								
		    }			
	     }
		
		return certificationType;
		
	}
	private CertificationTime processCertificationTime(Element certificationTimeElement){
		if(certificationTimeElement==null){
			return null;
		}
		CertificationTime certTime=new CertificationTime();
		certTime.setCheckEndTime(xmlparser.getElementContentByPaths(certificationTimeElement, "CheckEndTime"));
		certTime.setCheckStartTime(xmlparser.getElementContentByPaths(certificationTimeElement, "CheckStartTime"));
		certTime.setDistributionId(xmlparser.getElementContentByPaths(certificationTimeElement, "DistributionId"));
		certTime.setEndTime(xmlparser.getElementContentByPaths(certificationTimeElement, "EndTime"));
		certTime.setId(xmlparser.getElementContentByPaths(certificationTimeElement, "Id"));
		certTime.setScheduleWarningFlag(xmlparser.getElementContentByPaths(certificationTimeElement, "ScheduleWarningFlag"));
		certTime.setStartTime(xmlparser.getElementContentByPaths(certificationTimeElement, "StartTime"));
		certTime.setWarning(xmlparser.getElementContentByPaths(certificationTimeElement, "Warning"));
		return certTime;
	}
	private CertificationCategory processCertificationCategory(Element certificationCategoryElement){
		if(certificationCategoryElement==null){
			return null;			
		}
		CertificationCategory certificationCategory=new CertificationCategory();
		certificationCategory.setDescription(xmlparser.getElementContentByPaths(certificationCategoryElement, "Description"));
		certificationCategory.setId(xmlparser.getElementContentByPaths(certificationCategoryElement, "Id"));
		certificationCategory.setName(xmlparser.getElementContentByPaths(certificationCategoryElement, "Name"));		
		return certificationCategory;
	}
	
	
		
}
