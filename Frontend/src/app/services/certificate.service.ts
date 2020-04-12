import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { CreateCertificate } from 'app/models/createCertificate';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  url = environment.baseUrl + environment.certificate;

  constructor(
    private httpClient: HttpClient,
    private router: Router,
  ) { }

  public addNewCertificate(certificate: CreateCertificate) {
    console.log(certificate);
    return this.httpClient.post(this.url + "/new", certificate);
  }

  public addNewSelfSignedCertificate(certificate: CreateCertificate) {
    console.log(certificate);
    return this.httpClient.post(this.url + "/self-signed", certificate);
  }

  public getCACertificates(rootKeyStoragePassword, intermediateKeyStoragePassword): any {
    let params = new HttpParams();

    if (rootKeyStoragePassword != null) {
      params = params.append('rootKeyStoragePassword', rootKeyStoragePassword);
    }

    if (intermediateKeyStoragePassword != null) {
      params = params.append('intermediateKeyStoragePassword', intermediateKeyStoragePassword);
    }
    
    return this.httpClient.get(this.url, {
      params: params
    });
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
