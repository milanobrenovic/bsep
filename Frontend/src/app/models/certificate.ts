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
  public keyUsageDTO: KeyUsage;
	public extendedKeyUsageDTO: ExtendedKeyUsage;

	constructor(subjectId: number, issuerId: number, startDate: Date, endDate: Date, alias: string, password: string, keyUsageDTO: KeyUsage, extendedKeyUsageDTO: ExtendedKeyUsage) {
		this.subjectId = subjectId;
		this.issuerId = issuerId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.alias = alias;
		this.password = password;
		this.keyUsageDTO = keyUsageDTO;
		this.extendedKeyUsageDTO = extendedKeyUsageDTO;
	}

}