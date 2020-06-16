import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CreateCertificate } from 'app/models/createCertificate';
import { Certificate } from 'app/models/certificate';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  url = environment.baseUrl + environment.certificate;

  constructor(
    private httpClient: HttpClient,
  ) { }

  public createNewCertificate(certificate: Certificate) {
    return this.httpClient.post(this.url + "/create", certificate);
  }

  public createNewRootCertificate(certificate: Certificate) {
    return this.httpClient.post(this.url + "/createRoot", certificate);
  }
  
  public getCACertificates(): any {
    return this.httpClient.get(this.url + "/issuersKeyStore");
  }

  public getValidity(certificate: Certificate) {
    return this.httpClient.post(this.url + "/isValid", certificate);
  }
  
  public getAllRootCertificates() {
    return this.httpClient.get(this.url + "/get-all-root-certificates");
  }

  public download(certRole: string, keyStorePassword: string, alias: string) {
    return this.httpClient.post(this.url + "/download", { certRole, keyStorePassword, alias });
  }

}
