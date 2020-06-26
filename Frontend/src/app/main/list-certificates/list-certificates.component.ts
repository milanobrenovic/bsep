import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { CertificateService } from 'app/services/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { Certificate } from 'app/models/certificate';
import { HttpErrorResponse } from '@angular/common/http';
import { CertificateDetails } from 'app/models/certificateDetails';

@Component({
  selector: 'app-list-certificates',
  templateUrl: './list-certificates.component.html',
  styleUrls: ['./list-certificates.component.scss']
})
export class ListCertificatesComponent implements OnInit {

  public listCertificatesForm: FormGroup;
  public displayedColumns: string[] = ['serialNumber', 'subjectName', 'issuerName', 'validFrom', 'validTo', 'options'];
  public certificatesDataSource: MatTableDataSource<CertificateDetails>;

  constructor(
    private formBuilder: FormBuilder,
    private certificateService: CertificateService,
    private toastr: ToastrService,
  ) { }

  ngOnInit() {
    this.listCertificatesForm = this.formBuilder.group({
      certRole: new FormControl(null, [Validators.required]),
      keyStorePassword: new FormControl(null, [Validators.required]),
    });
  }

  fetchCertificates() {
    const keyStorePassword = this.listCertificatesForm.value.keyStorePassword;

    if (this.listCertificatesForm.value.certRole === "root") {
      this.fetchRootCertificates(keyStorePassword);
    } else if (this.listCertificatesForm.value.certRole === "intermediate") {
      this.fetchIntermediateCertificates(keyStorePassword);
    } else if (this.listCertificatesForm.value.certRole === "end-entity"){
      this.fetchEndEntityCertificates(keyStorePassword);
    }
  }

  private fetchRootCertificates(keyStorePassword: string) {
    this.certificateService.getAllRootCertificates(keyStorePassword).subscribe(
      (data: CertificateDetails[]) => {
        this.certificatesDataSource = new MatTableDataSource(data)
        if (data.length == 0) {
          this.toastr.info('No certificates in the specified KeyStore.', 'Show certificates');
        }
      },
      (e: HttpErrorResponse) => {
        const data: CertificateDetails[] = []
        this.certificatesDataSource = new MatTableDataSource(data)
        this.toastr.error(e.error.message, 'Failed to show certificates');
      }
    );
  }

  private fetchIntermediateCertificates(keyStorePassword: string) {
    this.certificateService.getAllIntermediateCertificates(keyStorePassword).subscribe(
      (data: CertificateDetails[]) => {
        this.certificatesDataSource = new MatTableDataSource(data)
        if (data.length == 0) {
          this.toastr.info('No certificates in the specified KeyStore.', 'Show certificates');
        }
      },
      (e: HttpErrorResponse) => {
        const data: CertificateDetails[] = []
        this.certificatesDataSource = new MatTableDataSource(data)
        this.toastr.error(e.error.message, 'Failed to show certificates');
      }
    );
  }

  private fetchEndEntityCertificates(keyStorePassword: string) {
    this.certificateService.getAllEndEntityCertificates(keyStorePassword).subscribe(
      (data: CertificateDetails[]) => {
        this.certificatesDataSource = new MatTableDataSource(data)
        if (data.length == 0) {
          this.toastr.info('No certificates in the specified KeyStore.', 'Show certificates');
        }
      },
      (e: HttpErrorResponse) => {
        const data: CertificateDetails[] = []
        this.certificatesDataSource = new MatTableDataSource(data)
        this.toastr.error(e.error.message, 'Failed to show certificates');
      }
    );
  }

  public viewDetails(certificate: Certificate) {
    this.certificateService.getValidity(certificate).subscribe(
      (valid: boolean) => {
        if (valid == true) {
          this.toastr.info("This certificate is valid.", "Certificate info");
        } else {
          this.toastr.info("This certificate is NOT valid.", "Certificate info");
        }
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, "Failed to verify certificate validity");
      }
    );
  }

  public download(certificate: Certificate) {
    this.certificateService.download(certificate).subscribe(
      () => {
        this.toastr.success("Certificate downloaded successfully.", "Success");
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, "Failed to download selected certificate");
      }
    );
  }

  public revoke(certificate: Certificate) {
    this.certificateService.revokeCertificate(certificate).subscribe(
      () => {
        this.toastr.success("This certificate has been revoked.", "Success");
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, "Failed to revoke selected certificate");
      }
    );
  }

}
