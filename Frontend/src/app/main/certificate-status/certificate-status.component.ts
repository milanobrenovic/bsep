import { Component, OnInit, Inject } from '@angular/core';
import { CertificateItem } from 'app/models/certificateItem';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { OCSPService } from 'app/services/ocsp.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-certificate-status',
  templateUrl: './certificate-status.component.html',
  styleUrls: ['./certificate-status.component.scss']
})
export class CertificateStatusComponent implements OnInit {

  cert: CertificateItem;
  status: String;
  revokeCertificateForm: FormGroup;

  constructor(
    private toastrService: ToastrService,
    private formBuilder: FormBuilder,
    public dialog: MatDialogRef<CertificateStatusComponent>,
    private ocspService: OCSPService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
    this.checkStatus();

    this.revokeCertificateForm = this.formBuilder.group({
      rootKeyStorePass: new FormControl(null, Validators.required),
      intermediateKeyStorePass: new FormControl(null, Validators.required),
      endEntityKeyStorePass: new FormControl(null, Validators.required),
    });
  }

  checkStatus() {
    console.log(this.data.cert.certificateIdentifier);
    this.ocspService.checkStatus(this.data.cert.certificateIdentifier).subscribe(
      (response: String) => {
        this.status = response;
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Failed to load certifiate status");
      }
    );
  }

  revoke() {
    this.ocspService.revoke(
      this.data.cert.certificateIdentifier.serialNumber,
      this.data.cert.alias,
      this.data.certRole,
      this.revokeCertificateForm.value.rootKeyStorePass,
      this.revokeCertificateForm.value.intermediateKeyStorePass,
      this.revokeCertificateForm.value.endEntityKeyStorePass,
    ).subscribe(
      () => {
        this.toastrService.success("Certificate has been revoked successfully.", "Certificate revoked successfully");
        this.dialog.close();
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Failed to revoke selected certificate");
      }
    );
  }

}
