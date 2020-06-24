package com.bsep.tim11.bseptim11.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Certificate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "subjectid")
	private Long subjectId;
	
	@Column(name = "issuerid")
	private Long issuerId;
	
	@Column(name = "startdate")
	private Date startDate;
	
	@Column(name = "enddate")
	private Date endDate;
	
	@Column(name = "alias")
	private String alias;
	
	@Column(name = "isRevoked")
	private Boolean isRevoked;
	
	public Certificate() {}
	
	public Certificate(Long subjectId, Long issuerId, Date startDate, Date endDate, String alias ) {
		super();
		this.subjectId = subjectId;
		this.issuerId = issuerId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.alias = alias;
		this.isRevoked = false;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Boolean getIsRevoked() {
		return isRevoked;
	}

	public void setIsRevoked(Boolean isRevoked) {
		this.isRevoked = isRevoked;
	}
	public Boolean isValid(){
		
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date now = new Date();  
		   System.out.println(df.format(now));
		   if((now).compareTo(this.startDate) < 0){
			   return false;
		   }
		   else if(now.compareTo(this.endDate) > 0){
			   return false;
		   }
		   return true;
		
	}

	
}
