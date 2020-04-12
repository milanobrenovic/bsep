export class KeyUsage {

  public certificateSigning: boolean;
  public crlSign: boolean;
  public dataEncipherment: boolean;
  public decipherOnly: boolean;
  public digitalSignature: boolean;
  public encipherOnly: boolean;
  public keyAgreement: boolean;
  public keyEncipherment: boolean;
  public nonRepudiation: boolean;

	constructor(certificateSigning: boolean, crlSign: boolean, dataEncipherment: boolean, decipherOnly: boolean, digitalSignature: boolean, encipherOnly: boolean, keyAgreement: boolean, keyEncipherment: boolean, nonRepudiation: boolean) {
		this.certificateSigning = certificateSigning;
		this.crlSign = crlSign;
		this.dataEncipherment = dataEncipherment;
		this.decipherOnly = decipherOnly;
		this.digitalSignature = digitalSignature;
		this.encipherOnly = encipherOnly;
		this.keyAgreement = keyAgreement;
		this.keyEncipherment = keyEncipherment;
		this.nonRepudiation = nonRepudiation;
	}

}