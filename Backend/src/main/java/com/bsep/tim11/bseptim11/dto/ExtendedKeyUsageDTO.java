package com.bsep.tim11.bseptim11.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.asn1.x509.KeyPurposeId;

public class ExtendedKeyUsageDTO {

	private boolean serverAuth;
	
	private boolean clientAuth;
	
	private boolean codeSigning;
	
	private boolean emailProtection;
	
	private boolean timeStamping;
	
	private boolean OCSPSigning;
	
	private boolean dvcs;
	
	public ExtendedKeyUsageDTO() {}
	
	public ExtendedKeyUsageDTO(boolean serverAuth, boolean clientAuth, boolean codeSigning, boolean emailProtection,
			boolean timeStamping, boolean oCSPSigning, boolean dvcs) {
		super();
		this.serverAuth = serverAuth;
		this.clientAuth = clientAuth;
		this.codeSigning = codeSigning;
		this.emailProtection = emailProtection;
		this.timeStamping = timeStamping;
		OCSPSigning = oCSPSigning;
		this.dvcs = dvcs;
	}

	public ExtendedKeyUsageDTO(ExtendedKeyUsageDTO extendedKeyUsage) {
		this.serverAuth = extendedKeyUsage.serverAuth;
		this.clientAuth = extendedKeyUsage.clientAuth;
		this.codeSigning = extendedKeyUsage.codeSigning;
		this.emailProtection = extendedKeyUsage.emailProtection;
		this.timeStamping = extendedKeyUsage.timeStamping;
		this.OCSPSigning = extendedKeyUsage.OCSPSigning;
		this.dvcs = extendedKeyUsage.dvcs;
	}
	
	public boolean isServerAuth() {
		return serverAuth;
	}

	public void setServerAuth(boolean serverAuth) {
		this.serverAuth = serverAuth;
	}
	
	public KeyPurposeId getServerAuth() {
		if(this.serverAuth)
			return KeyPurposeId.id_kp_serverAuth;
		else
			return null;
	}

	public boolean isClientAuth() {
		return clientAuth;
	}

	public void setClientAuth(boolean clientAuth) {
		this.clientAuth = clientAuth;
	}

	public KeyPurposeId getClientAuth() {
		if(this.clientAuth)
			return KeyPurposeId.id_kp_clientAuth;
		else
			return null;
	}
	
	public boolean isCodeSigning() {
		return codeSigning;
	}

	public void setCodeSigning(boolean codeSigning) {
		this.codeSigning = codeSigning;
	}

	public KeyPurposeId getCodeSigning() {
		if(this.codeSigning)
			return KeyPurposeId.id_kp_codeSigning;
		else
			return null;
	}
	
	public boolean isEmailProtection() {
		return emailProtection;
	}

	public void setEmailProtection(boolean emailProtection) {
		this.emailProtection = emailProtection;
	}

	public KeyPurposeId getEmailProtection() {
		if(this.emailProtection)
			return KeyPurposeId.id_kp_emailProtection;
		else
			return null;
	}
	
	public boolean isTimeStamping() {
		return timeStamping;
	}

	public void setTimeStamping(boolean timeStamping) {
		this.timeStamping = timeStamping;
	}

	public KeyPurposeId getTimeStamping() {
		if(this.timeStamping)
			return KeyPurposeId.id_kp_timeStamping;
		else
			return null;
	}
	
	public boolean isOCSPSigning() {
		return OCSPSigning;
	}

	public void setOCSPSigning(boolean oCSPSigning) {
		OCSPSigning = oCSPSigning;
	}

	public KeyPurposeId getOCSPSigning() {
		if(this.OCSPSigning)
			return KeyPurposeId.id_kp_OCSPSigning;
		else
			return null;
	}
	
	public boolean isDvcs() {
		return dvcs;
	}

	public void setDvcs(boolean dvcs) {
		this.dvcs = dvcs;
	}
	
	public KeyPurposeId getDvcs() {
		if(this.dvcs)
			return KeyPurposeId.id_kp_dvcs;
		else
			return null;
	}
	
	// Metoda koja pravi KeyPurposeId niz potreban za konstruktor by Nikola
	public KeyPurposeId[] makeArray() {

 		List<KeyPurposeId> list = new ArrayList<>(Arrays.asList());
 		int counter = 0;
 		if(this.clientAuth) {
 			list.add(getClientAuth());
 			counter++;
 		} 
 		if(this.codeSigning) {
 			list.add(getCodeSigning());
 			counter++; 			
 		} 
 		if(this.dvcs) {
 			list.add(getDvcs());
 			counter++; 			
 		} 
 		if(this.emailProtection) {
 			list.add(getEmailProtection());
 			counter++;
 		} 
 		if(this.OCSPSigning) {
 			list.add(getOCSPSigning());
 			counter++;
 		} 
 		if(this.serverAuth) {
 			list.add(getServerAuth());
 			counter++;
 		} 
 		if(this.timeStamping) {
 			list.add(getTimeStamping());
 			counter++;
 		}
		
 		KeyPurposeId array[] = new KeyPurposeId[counter];
 		array = list.toArray(array);
 		
 		return array;
	}
	
}
