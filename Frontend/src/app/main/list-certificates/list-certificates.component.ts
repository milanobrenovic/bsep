import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { CertificateService } from 'app/services/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { ChooseTemplateComponent } from '../choose-template/choose-template.component';
import { Certificate } from 'app/models/certificate';
import { OCSPService } from 'app/services/ocsp.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { CertificateItem } from 'app/models/certificateItem';
import { CertificateStatusComponent } from '../certificate-status/certificate-status.component';
import { CertificateDetails } from 'app/models/certificateDetails';

@Component({
  selector: 'app-list-certificates',
  templateUrl: './list-certificates.component.html',
  styleUrls: ['./list-certificates.component.scss']
})
export class ListCertificatesComponent implements OnInit {

  keyStoreForm: FormGroup;
  displayedColumns: string[] = ['serialNumber', 'subjectName', 'issuerName', 'validFrom', 'validTo', 'buttons'];
  certificatesDataSource: MatTableDataSource<CertificateDetails>;

  constructor(
    public dialog: MatDialog,
    private formBuilder: FormBuilder,
    private certificateService: CertificateService,
    private toastr: ToastrService,
  ) { }

  ngOnInit() {
    this.keyStoreForm = this.formBuilder.group({
      certRole: new FormControl(null, Validators.required),
      keyStorePassword: new FormControl(null, Validators.required)
    });
  }

  fetchCertificates() {
    const keyStorePassword = this.keyStoreForm.value.keyStorePassword;

    if (this.keyStoreForm.value.certRole === "root") {
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
    } else if (this.keyStoreForm.value.certRole === "intermediate") {
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
  }

  viewDetails(cert: Certificate) {
    console.log(cert);
    this.certificateService.getValidity(cert).subscribe(
      (valid: boolean) => {
        if (valid == true) {
          this.toastr.info("This certificate is valid.", 'Certificate info');
        } else {
          this.toastr.info("This certificate is NOT valid.", 'Certificate info');
        }
      },
      (e: HttpErrorResponse) => {
        console.log("sranje");
        this.toastr.error(e.error.message, 'Failed to verify certificate validity');
      }
    );
    // this.dialog.open(CertificateDetailsComponent, {
    //   data: cert,

    // });
  }

  download(cert: Certificate) {
    this.certificateService.download(
      cert).subscribe(
      () => {
        this.toastr.success('Success!', 'Download certificate');
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, 'Failed to download selected certificate');
      }
    );
  }

  revoke(cert: Certificate) {
    this.certificateService.revokeCertificate(cert).subscribe(
      () => {
        this.toastr.success("This certificate has been revoked", 'Certificate revoked successfully');
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, 'Failed to revoke selected certificate');
      }
    );
  }

  openTemplatesDialog() {
    this.dialog.open(ChooseTemplateComponent);
  }

}
