import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { CertificateService } from 'app/services/certificate.service';
import { ToastrService } from 'ngx-toastr';
import { ChooseTemplateComponent } from '../choose-template/choose-template.component';
import { CertificateDetailsComponent } from '../certificate-details/certificate-details.component';
import { Certificate } from 'app/models/certificate';
import { OCSPService } from 'app/services/ocsp.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CertificateItem } from 'app/models/certificateItem';
import { CertificateStatusComponent } from '../certificate-status/certificate-status.component';

@Component({
  selector: 'app-list-certificates',
  templateUrl: './list-certificates.component.html',
  styleUrls: ['./list-certificates.component.scss']
})
export class ListCertificatesComponent implements OnInit {

  keyStoreForm: FormGroup;
  displayedColumns: string[] = ['serialNumber', 'subjectCN', 'issuerCN', 'validFrom', 'validTo', 'buttons'];
  certificatesDataSource: MatTableDataSource<CertificateItem>;

  constructor(
    public dialog: MatDialog,
    private formBuilder: FormBuilder,
    private certificateService: CertificateService,
    private ocspService: OCSPService,
    private toastr: ToastrService,
  ) { }

  ngOnInit() {
    this.keyStoreForm = this.formBuilder.group({
      certRole: new FormControl(null, Validators.required),
      keyStorePassword: new FormControl(null, Validators.required)
    });
  }

  fetchCertificates() {
    this.certificateService.getCertificates(this.keyStoreForm.value.certRole, this.keyStoreForm.value.keyStorePassword).subscribe(
      (data: CertificateItem[]) => {
        this.certificatesDataSource = new MatTableDataSource(data)
        if (data.length == 0) {
          this.toastr.info('No certificates in the specified KeyStore.', 'Show certificates');
        }
      },
      (e: HttpErrorResponse) => {
        const data: CertificateItem[] = []
        this.certificatesDataSource = new MatTableDataSource(data)
        this.toastr.error(e.error.message, 'Failed to show certificates');
      }
    );
  }

  viewDetails(cert: CertificateItem) {
    this.dialog.open(CertificateDetailsComponent, { data: cert });
  }

  download(cert: CertificateItem) {
    this.certificateService.download(
      this.keyStoreForm.value.certRole,
      this.keyStoreForm.value.keyStorePassword,
      cert.alias,
    ).subscribe(
      () => {
        this.toastr.success('Success!', 'Download certificate');
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, 'Failed to download selected certificate');
      }
    );
  }

  checkStatus(cert: CertificateItem) {
    console.log("LIST-CERTIFICATES: " + cert.certificateIdentifier);
    this.dialog.open(CertificateStatusComponent, {
      data: {
        "cert": cert,
        "certRole": this.keyStoreForm.value.certRole,
      }
    });
  }

  openTemplatesDialog() {
    this.dialog.open(ChooseTemplateComponent);
  }

}
