import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { HttpClient } from '@angular/common/http';
import { CertificateID } from 'app/models/certificateID';

@Injectable({
  providedIn: 'root'
})
export class OCSPService {

  url = environment.baseUrl + environment.auth;

  constructor(
    private httpClient: HttpClient,
  ) { }

  public checkStatus(certificateIdentifier: CertificateID) {
    console.log(certificateIdentifier);
    const OCSPRequest = {
      "OCSP Request Data": {
        "Version": "1 (0x0)",
        "Requestor List": {
          "Certificate ID": {
            "Hash Algorithm": certificateIdentifier.hashAlgorithm,
            "Issuer Name Hash": certificateIdentifier.issuerNameHash,
            "Issuer Key Hash": certificateIdentifier.issuerKeyHash,
            "Serial Number": certificateIdentifier.serialNumber,
          }
        }
      }
    }
    return this.httpClient.post(this.url + "/check-status", OCSPRequest);
  }

  public revoke(serialNumber: string, alias: string, certRole: string, rootKeyStorePass: string,
    intermediateKeyStorePass: string, endEntityKeyStorePass: string) {
    
    return this.httpClient.put(this.url, {
      serialNumber, alias, certRole, rootKeyStorePass, intermediateKeyStorePass, endEntityKeyStorePass
    });
  }

}
