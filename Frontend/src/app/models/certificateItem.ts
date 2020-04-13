import { Entity } from './entity';
import { DateTime } from 'luxon';
import { ExtendedKeyUsage } from './extendedKeyUsage';
import { CertificateID } from './certificateID';

export class CertificateItem {
  
  public serialNumber: string;
  public subject: Entity;
  public issuer: Entity;
  public validFrom: DateTime;
  public validTo: DateTime;
  public subjectIsCa: boolean;
  public keyUsage: KeyUsage;
  public extendedKeyUsage: ExtendedKeyUsage;
  public alias: string;
  public certificateIdentifier: CertificateID;

  constructor(subject: Entity, issuer: Entity, validFrom: DateTime, validTo: DateTime, subjectIsCa: boolean,
    keyUsage: KeyUsage, extendedKeyUsage: ExtendedKeyUsage, alias: string, serialNumber: string,
    certificateIdentifier: CertificateID) {
    
		this.subject = subject;
		this.issuer = issuer;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.subjectIsCa = subjectIsCa;
		this.serialNumber = serialNumber;
		this.keyUsage = keyUsage;
		this.extendedKeyUsage = extendedKeyUsage;
		this.alias = alias;
		this.certificateIdentifier = certificateIdentifier;
	}

}