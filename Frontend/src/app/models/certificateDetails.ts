export class CertificateDetails {

	public subjectName: string;
	public issuerName: string;
  public serialNumber: number;
  public validFrom: Date;
	public validTo: Date;
	public alias: String;

	constructor($subjectName: string, $issuerName: string, $serialNumber: number, $validFrom: Date, $validTo: Date, $alias: String) {
		this.subjectName = $subjectName;
		this.issuerName = $issuerName;
		this.serialNumber = $serialNumber;
		this.validFrom = $validFrom;
		this.validTo = $validTo;
		this.alias = $alias;
	}

}