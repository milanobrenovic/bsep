export class Entity {
  
  public id: number;
	public commonName: string;
  public surname: string;
	public givenName: string;
  public organization: string;
  public organizationUnit: string;
  public country: string;
	public email: string;
	public isCA: boolean;
	public hasCertificate: boolean;

	constructor(
			commonName: string,
			surname: string,
			givenName: string,
			organization: string,
			organizationUnit: string,
			country: string,
			email: string,
			isCA: boolean,
			hasCertificate: boolean,
			id?: number
	) {
		this.id = id;
		this.commonName = commonName;
		this.surname = surname;
		this.givenName = givenName;
		this.organization = organization;
		this.organizationUnit = organizationUnit;
		this.country = country;
		this.email = email;
		this.isCA = isCA;
		this.hasCertificate = hasCertificate;
	}
  
}