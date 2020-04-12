import { ExtendedKeyUsage } from './extendedKeyUsage';
import { KeyUsage } from './keyUsage';
import { Entity } from './entity';
import { DateTime } from 'luxon';

export class Certificate {

  public serialNumber: string;
  public subject: Entity;
  public issuer: Entity;
  public validFrom: DateTime;
  public validTo: DateTime;
  public authorityKeyIdentifier: boolean;
  public subjectKeyIdentifier: boolean;
  public subjectIsCa: boolean;
  public keyUsage: KeyUsage;
  public extendedKeyUsage: ExtendedKeyUsage;
  public alias: string;

	constructor(subject: Entity, issuer: Entity, validFrom: DateTime, validTo: DateTime, authorityKeyIdentifier: boolean, subjectKeyIdentifier: boolean, subjectIsCa: boolean, keyUsage: KeyUsage, extendedKeyUsage: ExtendedKeyUsage, alias?: string, serialNumber?: string) {
		this.serialNumber = serialNumber;
		this.subject = subject;
		this.issuer = issuer;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.authorityKeyIdentifier = authorityKeyIdentifier;
		this.subjectKeyIdentifier = subjectKeyIdentifier;
		this.subjectIsCa = subjectIsCa;
		this.keyUsage = keyUsage;
		this.extendedKeyUsage = extendedKeyUsage;
		this.alias = alias;
	}

}