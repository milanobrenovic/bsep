export class ExtendedKeyUsage {

  public serverAuth: boolean;
  public clientAuth: boolean;
  public codeSigning: boolean;
  public emailProtection: boolean;
  public timeStamping: boolean;
  public ocspSigning: boolean;
  public dvcs: boolean;

	constructor(serverAuth: boolean, clientAuth: boolean, codeSigning: boolean, emailProtection: boolean, timeStamping: boolean, ocspSigning: boolean, dvcs: boolean) {
		this.serverAuth = serverAuth;
		this.clientAuth = clientAuth;
		this.codeSigning = codeSigning;
		this.emailProtection = emailProtection;
		this.timeStamping = timeStamping;
		this.ocspSigning = ocspSigning;
		this.dvcs = dvcs;
	}

}