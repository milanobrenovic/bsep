package com.bsep.tim11.bseptim11.dto;

import org.bouncycastle.asn1.x509.KeyUsage;

public class KeyUsageDTO {

	private boolean cRLSign;
	
	private boolean dataEncipherment;
	
	private boolean decipherOnly;
	
	private boolean digitalSignature;
	
	private boolean encipherOnly;
	
	private boolean keyAgreement;
	
	private boolean keyCertSign;
	
	private boolean keyEncipherment;
	
	private boolean nonRepudiation;
	
	public KeyUsageDTO() {}

	public KeyUsageDTO(boolean cRLSign, boolean dataEncipherment, boolean decipherOnly, boolean digitalSignature,
			boolean encipherOnly, boolean keyAgreement, boolean keyCertSign, boolean keyEncipherment,
			boolean nonRepudiation) {
		super();
		this.cRLSign = cRLSign;
		this.dataEncipherment = dataEncipherment;
		this.decipherOnly = decipherOnly;
		this.digitalSignature = digitalSignature;
		this.encipherOnly = encipherOnly;
		this.keyAgreement = keyAgreement;
		this.keyCertSign = keyCertSign;
		this.keyEncipherment = keyEncipherment;
		this.nonRepudiation = nonRepudiation;
	}

	public KeyUsageDTO(KeyUsageDTO ku) {
		this.cRLSign = ku.iscRLSign();
		this.dataEncipherment = ku.isDataEncipherment();
		this.decipherOnly = ku.isDecipherOnly();
		this.digitalSignature = ku.isDigitalSignature();
		this.encipherOnly = ku.isEncipherOnly();
		this.keyAgreement = ku.isKeyAgreement();
		this.keyCertSign = ku.isKeyCertSign();
		this.keyEncipherment = ku.isKeyEncipherment();
		this.nonRepudiation = ku.isNonRepudiation();
	}
	
	public boolean iscRLSign() {
		return cRLSign;
	}

	public void setcRLSign(boolean cRLSign) {
		this.cRLSign = cRLSign;
	}

	public int getcRLSign() {
		if(this.cRLSign) 
			return KeyUsage.cRLSign;
		else
			return 0;
	}
	
	public boolean isDataEncipherment() {
		return dataEncipherment;
	}

	public void setDataEncipherment(boolean dataEncipherment) {
		this.dataEncipherment = dataEncipherment;
	}

	public int getDataEncipherment() {
		if(this.dataEncipherment)
			return KeyUsage.dataEncipherment;
		else
			return 0;
	}
	
	public boolean isDecipherOnly() {
		return decipherOnly;
	}

	public void setDecipherOnly(boolean decipherOnly) {
		this.decipherOnly = decipherOnly;
	}

	public int getDecipherOnly() {
		if(this.decipherOnly)
			return KeyUsage.decipherOnly;
		else
			return 0;
	}
		
	public boolean isDigitalSignature() {
		return digitalSignature;
	}

	public void setDigitalSignature(boolean digitalSignature) {
		this.digitalSignature = digitalSignature;
	}

	public int getDigitalSignature() {
		if(this.digitalSignature)
			return KeyUsage.digitalSignature;
		else
			return 0;
	}
	
	public boolean isEncipherOnly() {
		return encipherOnly;
	}

	public void setEncipherOnly(boolean encipherOnly) {
		this.encipherOnly = encipherOnly;
	}
	
	public int getEncipherOnly() {
		if(this.encipherOnly)
			return KeyUsage.encipherOnly;
		else
			return 0;
	}

	public boolean isKeyAgreement() {
		return keyAgreement;
	}

	public void setKeyAgreement(boolean keyAgreement) {
		this.keyAgreement = keyAgreement;
	}

	public int getKeyAgreement() {
		if(this.keyAgreement)
			return KeyUsage.keyAgreement;
		else
			return 0;
	}
	
	public boolean isKeyCertSign() {
		return keyCertSign;
	}

	public void setKeyCertSign(boolean keyCertSign) {
		this.keyCertSign = keyCertSign;
	}

	public int getKeyCertSign() {
		if(this.keyCertSign)
			return KeyUsage.keyCertSign;
		else
			return 0;
	}
	
	public boolean isKeyEncipherment() {
		return keyEncipherment;
	}
	
	public void setKeyEncipherment(boolean keyEncipherment) {
		this.keyEncipherment = keyEncipherment;
	}

	public int getKeyEncipherment() {
		if(this.keyEncipherment)
			return KeyUsage.keyEncipherment;
		else
			return 0;
	}
	
	public boolean isNonRepudiation() {
		return nonRepudiation;
	}

	public void setNonRepudiation(boolean nonRepudiation) {
		this.nonRepudiation = nonRepudiation;
	}
	
	public int getNonRepudiation() {
		if(this.nonRepudiation)
			return KeyUsage.nonRepudiation;
		else
			return 0;
	}
	

}
