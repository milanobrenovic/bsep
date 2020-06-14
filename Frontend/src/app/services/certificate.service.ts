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

  public getCACertificates(id: number, rootKeyStoragePassword: string, intermediateKeyStoragePassword: string): any {
    let params = new HttpParams();

    if (rootKeyStoragePassword != null) {
      params = params.append('rootKeyStoragePassword', rootKeyStoragePassword);
    }

    if (intermediateKeyStoragePassword != null) {
      params = params.append('intermediateKeyStoragePassword', intermediateKeyStoragePassword);
    }

    console.log(this.url + "/" + id);
    return this.httpClient.get(this.url + "/issuers");
  }

  public getCertificates(keyStoreLevel: string, keyStorePassword: string) {
    let params = new HttpParams();
    params = params.append('role', keyStoreLevel);
    params = params.append('keyStorePassword', keyStorePassword);

    return this.httpClient.get(this.url + "/all", {
      params: params
    });
  }

  public download(certRole: string, keyStorePassword: string, alias: string) {
    return this.httpClient.post(this.url + "/download", { certRole, keyStorePassword, alias });
  }

}
