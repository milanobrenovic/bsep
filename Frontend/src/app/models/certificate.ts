import { ExtendedKeyUsage } from './extendedKeyUsage';
import { KeyUsage } from './keyUsage';

export class Certificate {

	public subjectId: number;
	public issuerId: number;
  	public startDate: Date;
  	public endDate: Date;
	public alias: string;
	public password: string;
	public keyStorePassword: string;
  	public keyUsageDTO: KeyUsage;
	public extendedKeyUsageDTO: ExtendedKeyUsage;
	public isCA: boolean;

	constructor(subjectId: number, issuerId: number, startDate: Date, endDate: Date, alias: string, password: string, keyStorePassword: string, keyUsageDTO: KeyUsage, extendedKeyUsageDTO: ExtendedKeyUsage, isCA:boolean) {
		this.subjectId = subjectId;
		this.issuerId = issuerId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.alias = alias;
		this.password = password;
		this.keyStorePassword = keyStorePassword;
		this.keyUsageDTO = keyUsageDTO;
		this.extendedKeyUsageDTO = extendedKeyUsageDTO;
		this.isCA = isCA;
	}

}