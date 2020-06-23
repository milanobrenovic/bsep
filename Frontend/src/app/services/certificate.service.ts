import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { HttpClient } from '@angular/common/http';
import { Certificate } from 'app/models/certificate';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  public url = environment.baseUrl + environment.certificate;

  constructor(
    private httpClient: HttpClient,
  ) { }

  public createNewCertificate(certificate: Certificate) {
    return this.httpClient.post(this.url + "/create", certificate);
  }

  public createNewRootCertificate(certificate: Certificate) {
    return this.httpClient.post(this.url + "/createRoot", certificate);
  }
  
  public getCACertificates(keyStorePassword: number): any {
    return this.httpClient.get(this.url + "/issuersKeyStore/" + keyStorePassword);
  }

  public getValidity(certificate: Certificate) {
    return this.httpClient.post(this.url + "/isValid", certificate);
  }
  
  public getAllRootCertificates(keyStorePassword: number) {
    return this.httpClient.get(this.url + "/get-all-root-certificates/" + keyStorePassword);
  }
  
  public getAllIntermediateCertificates(keyStorePassword: number) {
    return this.httpClient.get(this.url + "/get-all-intermediate-certificates/" + keyStorePassword);
  }

  public download(certificate: Certificate) {
    return this.httpClient.post(this.url + "/downloadCertificate", certificate );
  }

  public revokeCertificate(certificate: Certificate) {
    return this.httpClient.post(this.url + "/revoke", certificate);
  }

}
