export class Template {

  public authorityKeyId: boolean;
  public subjectKeyId: boolean;
  public CA: boolean;
  public digitalSignature: boolean;
  public keyEncipherment: boolean;
  public certSigning: boolean;
  public CRLSign: boolean;
  public TLSWebServerAuth: boolean;
  public TLSWebClientAuth: boolean;
  public codeSigning: boolean;

	constructor(authorityKeyId: boolean, subjectKeyId: boolean, CA: boolean, digitalSignature: boolean, keyEncipherment: boolean, certSigning: boolean, CRLSign: boolean, TLSWebServerAuth: boolean, TLSWebClientAuth: boolean, codeSigning: boolean) {
		this.authorityKeyId = authorityKeyId;
		this.subjectKeyId = subjectKeyId;
		this.CA = CA;
		this.digitalSignature = digitalSignature;
		this.keyEncipherment = keyEncipherment;
		this.certSigning = certSigning;
		this.CRLSign = CRLSign;
		this.TLSWebServerAuth = TLSWebServerAuth;
		this.TLSWebClientAuth = TLSWebClientAuth;
		this.codeSigning = codeSigning;
	}

}