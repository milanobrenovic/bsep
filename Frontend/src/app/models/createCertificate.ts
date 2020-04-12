import { Certificate } from './certificate';

export class CreateCertificate {

  public certificateData: Certificate;
  public issuerCertificate: Certificate;
  public alias: string;
  public keyStorePassword: string;
  public privateKeyPassword: string;
  public issuerKeyStorePassword: string;
  public issuerPrivateKeyPassword: string;

	constructor(certificateData: Certificate, issuerCertificate: Certificate, alias: string, keyStorePassword: string, privateKeyPassword: string, issuerPrivateKeyPassword?: string, issuerKeyStorePassword?: string) {
		this.certificateData = certificateData;
		this.issuerCertificate = issuerCertificate;
		this.alias = alias;
		this.keyStorePassword = keyStorePassword;
		this.privateKeyPassword = privateKeyPassword;
		this.issuerKeyStorePassword = issuerKeyStorePassword;
		this.issuerPrivateKeyPassword = issuerPrivateKeyPassword;
	}

}