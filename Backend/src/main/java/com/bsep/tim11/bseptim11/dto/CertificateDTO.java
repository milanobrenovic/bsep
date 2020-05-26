package com.bsep.tim11.bseptim11.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CertificateDTO {

	private Long subjectId;
	private Long issuerId;
	private Date startDate;
	private Date endDate;
	private String alias;
	private String password;
	private KeyUsageDTO keyUsageDTO;
	private ExtendedKeyUsageDTO extendedKeyUsageDTO;
	
	public CertificateDTO() {}

	public CertificateDTO(Long subjectId, Long issuerId, String startDate, String endDate, String alias, String password, KeyUsageDTO keyUsageDTO, ExtendedKeyUsageDTO extendedKeyUsageDTO) {
		super();
		this.subjectId = subjectId;
		this.issuerId = issuerId;
		this.alias = alias;
		this.password = password;
		this.keyUsageDTO = new KeyUsageDTO(keyUsageDTO);
		this.extendedKeyUsageDTO = new ExtendedKeyUsageDTO(extendedKeyUsageDTO);
		
		//Datumi od kad do kad vazi sertifikat
		SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.startDate = iso8601Formater.parse(startDate);
			this.endDate = iso8601Formater.parse(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public Long getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(Long issuerId) {
		this.issuerId = issuerId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public KeyUsageDTO getKeyUsageDTO() {
		return keyUsageDTO;
	}

	public void setKeyUsageDTO(KeyUsageDTO keyUsage) {
		this.keyUsageDTO = keyUsage;
	}

	public ExtendedKeyUsageDTO getExtendedKeyUsageDTO() {
		return extendedKeyUsageDTO;
	}

	public void setExtendedKeyUsageDTO(ExtendedKeyUsageDTO extendedKeyUsageDTO) {
		this.extendedKeyUsageDTO = extendedKeyUsageDTO;
	}

	@Override
	public String toString() {
		return "CertificateDTO{" +
				"subjectId=" + subjectId +
				", issuerId=" + issuerId +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", alias='" + alias + '\'' +
				", password='" + password + '\'' +
				", keyUsage=" + keyUsageDTO.toString() +
				", extendedKeyUsage=" + extendedKeyUsageDTO.toString() +
				'}';
	}
}
