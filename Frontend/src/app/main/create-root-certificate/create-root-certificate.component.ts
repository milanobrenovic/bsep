import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, ValidatorFn } from '@angular/forms';
import { Entity } from 'app/models/entity';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { SubjectService } from 'app/services/subject.service';
import { CertificateService } from 'app/services/certificate.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { formatDate } from '@angular/common';
import { KeyUsage } from 'app/models/keyUsage';
import { ExtendedKeyUsage } from 'app/models/extendedKeyUsage';
import { CreateSubjectComponent } from '../create-subject/create-subject.component';
import { Template } from 'app/models/template';
import { Certificate } from 'app/models/certificate';

const TimeValidator: ValidatorFn = (formGroup: FormGroup) => {
  const from = formGroup.get("validFrom").value;
  const to = formGroup.get("validTo").value;

  if (!from || !to) {
    return null;
  }

  return from !== null && to !== null && from < to ? null : { validError: true };
}

@Component({
  selector: 'app-create-root-certificate',
  templateUrl: './create-root-certificate.component.html',
  styleUrls: ['./create-root-certificate.component.scss']
})
export class CreateRootCertificateComponent implements OnInit {

  public createCertificateFromSubject: FormGroup;
  public createCertificateFromOtherData: FormGroup;
  public createCertificateInfoAboutKeyStorage: FormGroup;
  public minDate = new Date();
  public subjects: Entity[] = [];
  public createdNewSubject: Subscription;
  public selectedTemplate: Template;

  constructor(
    private toastrService: ToastrService,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    private subjectService: SubjectService,
    private certificateService: CertificateService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.createCertificateFromSubject = this.formBuilder.group({
      selectedSubject: new FormControl(null, Validators.required),
    });

    this.createFormCertificateFromOtherData();
    this.createFormCertificateInfoAboutKeyStorage();
    this.getSubjects();

    this.createdNewSubject = this.subjectService.createSuccessEmitter.subscribe(
      () => {
        this.getSubjects();
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Failed to create a new subject");
      }
    );

    if (JSON.parse(localStorage.getItem("selectedTemplate"))) {
      this.selectedTemplate = JSON.parse(localStorage.getItem("selectedTemplate"));
      localStorage.removeItem("selectedTemplate");
      this.setExtensions();
    }
  }

  createFormCertificateFromOtherData() {
    this.createCertificateFromOtherData = this.formBuilder.group({
      validFrom: new FormControl(null, Validators.required),
      validTo: new FormControl(null, Validators.required),
      authorityKeyIdentifier: new FormControl(false, Validators.required),
      subjectKeyIdentifier: new FormControl(false, Validators.required),
      keyUsage: this.formBuilder.group({
        certificateSigning: new FormControl(false),
        crlSign: new FormControl(false),
        dataEncipherment: new FormControl(false),
        decipherOnly: new FormControl(false),
        digitalSignature: new FormControl(false),
        encipherOnly: new FormControl(false),
        keyAgreement: new FormControl(false),
        keyEncipherment: new FormControl(false),
        nonRepudiation: new FormControl(false),
      }),
      extendedKeyUsage: this.formBuilder.group({
        serverAuth: new FormControl(false),
        clientAuth: new FormControl(false),
        codeSigning: new FormControl(false),
        emailProtection: new FormControl(false),
        timeStamping: new FormControl(false),
        ocspSigning: new FormControl(false),
        dvcs: new FormControl(false),
      }),
    }, {
      validator: [TimeValidator],
    });
  }

  createFormCertificateInfoAboutKeyStorage() {
    this.createCertificateInfoAboutKeyStorage = this.formBuilder.group({
      alias: new FormControl(null, Validators.required),
      password: new FormControl(null, Validators.required),
      privateKeyPassword: new FormControl(null, Validators.required),
    });
  }
  
  getSubjects(): void {
    this.subjectService.getAllSubjects().subscribe(
      (subjects: Entity[]) => {
        this.subjects = subjects;
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Failed to get subjects");
      }
    );
  }

  setExtensions() {
    this.createCertificateFromOtherData.patchValue(
      {
        "authorityKeyIdentifier": this.selectedTemplate.authorityKeyId,
        "subjectKeyIdentifier": this.selectedTemplate.subjectKeyId,
        "keyUsage": {
          "digitalSignature": this.selectedTemplate.digitalSignature,
          "keyEncipherment": this.selectedTemplate.keyEncipherment,
          "certificateSigning": this.selectedTemplate.certSigning,
          "crlSign": this.selectedTemplate.CRLSign,
        },
        "extendedKeyUsage": {
          "serverAuth": this.selectedTemplate.TLSWebServerAuth,
          "clientAuth": this.selectedTemplate.TLSWebClientAuth,
          "codeSigning": this.selectedTemplate.codeSigning,
        }
      }
    );
  }

  getSelectedSubject() {
    return this.createCertificateFromSubject.get("selectedSubject").value;
  }

  createRootCertificate() {
    if (this.createCertificateFromSubject.invalid) {
      this.toastrService.error("Please choose a subject.", "Could not create certificate");
      return;
    }

    if (this.createCertificateFromOtherData.invalid) {
      this.toastrService.error("Please set a valid period.", "Could not create certificate");
      return;
    }

    if (!this.checkKeyUsage()) {
      this.toastrService.error("Please select at least one key usage.", "Could not create certificate");
    }
    
    if (!this.checkExtendedKeyUsage()) {
      this.toastrService.error("Please select at least one extended key usage.", "Could not create certificate");
    }

    const keyUsage = this.createKeyUsage();
    const extendedKeyUsage = this.createExtendedKeyUsage();
    
    const validFrom = formatDate(this.createCertificateFromOtherData.value.validFrom, "yyyy-MM-dd", "en-US");
    const validTo = formatDate(this.createCertificateFromOtherData.value.validTo, "yyyy-MM-dd", "en-US");

    console.log(new Date(validFrom));

    const certificate = new Certificate(
      this.createCertificateFromSubject.value.selectedSubject.id,
      this.createCertificateFromSubject.value.selectedSubject.id,
      new Date(validFrom),
      new Date(validTo),
      "alias-temp",
      "123",
      keyUsage,
      extendedKeyUsage,
    );

    this.certificateService.createNewRootCertificate(certificate).subscribe(
      () => {
        this.createCertificateFromOtherData.reset();
        this.createCertificateFromSubject.reset();
        this.createCertificateInfoAboutKeyStorage.reset();
        this.toastrService.success("New self-signed certificate has been created successfully.", "Certificate created");
        this.router.navigate(["/pages/list-certificates"]);
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Could not create certificate");
      }
    );
  }

  checkKeyUsage(): boolean {
    let keyUsage = this.createCertificateFromOtherData.value.keyUsage;
    return  keyUsage.certificateSigning ||
            keyUsage.crlSign ||
            keyUsage.dataEncipherment ||
            keyUsage.decipherOnly ||
            keyUsage.digitalSignature ||
            keyUsage.encipherOnly ||
            keyUsage.keyAgreement ||
            keyUsage.keyEncipherment ||
            keyUsage.nonRepudiation;
  }

  checkExtendedKeyUsage(): boolean {
    let keyUsage = this.createCertificateFromOtherData.value.extendedKeyUsage;
    return  keyUsage.serverAuth ||
            keyUsage.clientAuth ||
            keyUsage.codeSigning ||
            keyUsage.emailProtection ||
            keyUsage.timeStamping ||
            keyUsage.ocspSigning;
  }

  createKeyUsage(): KeyUsage {
    return new KeyUsage(
      this.createCertificateFromOtherData.value.keyUsage.certificateSigning,
      this.createCertificateFromOtherData.value.keyUsage.crlSign,
      this.createCertificateFromOtherData.value.keyUsage.dataEncipherment,
      this.createCertificateFromOtherData.value.keyUsage.decipherOnly,
      this.createCertificateFromOtherData.value.keyUsage.digitalSignature,
      this.createCertificateFromOtherData.value.keyUsage.encipherOnly,
      this.createCertificateFromOtherData.value.keyUsage.keyAgreement,
      this.createCertificateFromOtherData.value.keyUsage.keyEncipherment,
      this.createCertificateFromOtherData.value.keyUsage.nonRepudiation,
    );
  }

  createExtendedKeyUsage(): ExtendedKeyUsage {
    return new ExtendedKeyUsage(
      this.createCertificateFromOtherData.value.extendedKeyUsage.serverAuth,
      this.createCertificateFromOtherData.value.extendedKeyUsage.clientAuth,
      this.createCertificateFromOtherData.value.extendedKeyUsage.codeSigning,
      this.createCertificateFromOtherData.value.extendedKeyUsage.emailProtection,
      this.createCertificateFromOtherData.value.extendedKeyUsage.timeStamping,
      this.createCertificateFromOtherData.value.extendedKeyUsage.ocspSigning,
      this.createCertificateFromOtherData.value.extendedKeyUsage.dvcs,
    );
  }

  openAddSubject() {
    this.dialog.open(CreateSubjectComponent);
  }

}
