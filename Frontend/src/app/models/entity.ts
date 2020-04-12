export class Entity {
  
  public id: number;
  public type: string;
  public commonName: string;
  public email: string;
  public organizationUnit: string;
  public organization: string;
  public countryCode: string;
  public surname: string;
  public givename: string;

	constructor(type: string, commonName: string, email: string, organizationUnit: string, organization: string, countryCode: string, surname: string, givename: string, id?: number) {
		this.id = id;
		this.type = type;
		this.commonName = commonName;
		this.email = email;
		this.organizationUnit = organizationUnit;
		this.organization = organization;
		this.countryCode = countryCode;
		this.surname = surname;
		this.givename = givename;
	}
  
}