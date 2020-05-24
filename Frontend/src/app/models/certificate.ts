import { ExtendedKeyUsage } from './extendedKeyUsage';
import { KeyUsage } from './keyUsage';
import { DateTime } from 'luxon';

export class Certificate {

	public subjectId: number;
	public issuerId: number;
  public startDate: Date;
  public endDate: Date;
	public alias: string;
	public password: string;
  public keyUsage: KeyUsage;
  public extendedKeyUsage: ExtendedKeyUsage;

	constructor(subjectId: number, issuerId: number, startDate: Date, endDate: Date, alias: string, password: string, keyUsage: KeyUsage, extendedKeyUsage: ExtendedKeyUsage) {
		this.subjectId = subjectId;
		this.issuerId = issuerId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.alias = alias;
		this.password = password;
		this.keyUsage = keyUsage;
		this.extendedKeyUsage = extendedKeyUsage;
	}

}