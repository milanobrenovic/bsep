import { Component, OnInit } from '@angular/core';
import { FormGroup, ValidatorFn, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Entity } from 'app/models/entity';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { SubjectService } from 'app/services/subject.service';
import { CertificateService } from 'app/services/certificate.service';
import { Router } from '@angular/router';
import { Template } from 'app/models/template';
import { KeyUsage } from 'app/models/keyUsage';
import { ExtendedKeyUsage } from 'app/models/extendedKeyUsage';
import { formatDate } from '@angular/common';
import { Certificate } from 'app/models/certificate';
import { CreateCertificate } from 'app/models/createCertificate';
import { AddSubjectComponent } from '../add-subject/add-subject.component';

const TimeValidator: ValidatorFn = (formGroup: FormGroup) => {
  const from = formGroup.get("validFrom").value;
  const to = formGroup.get("validTo").value;

  if (!from || !to) {
    return null;
  }

  return from !== null && to !== null && from < to ? null : { validError: true };
}

@Component({
  selector: 'app-create-self-signed-certificate',
  templateUrl: './create-self-signed-certificate.component.html',
  styleUrls: ['./create-self-signed-certificate.component.scss']
})
export class CreateSelfSignedCertificateComponent implements OnInit {

  createCertificateFromSubject: FormGroup;
  createCertificateFromOtherData: FormGroup;
  createCertificateInfoAboutKeyStorage: FormGroup;
  minDate = new Date();
  subjects: Entity[] = [];
  createdNewSubject: Subscription;
  selectedTemplate: Template;

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
      (e) => {
        console.log(e);
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
    this.subjectService.getAll().subscribe(
      (subjects: Entity[]) => {
        this.subjects = subjects;
      },
      (e) => {
        console.log(e);
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

  createCertificate() {
    if (this.createCertificateFromSubject.invalid) {
      this.toastrService.error("Please choose a subject.", "Could not create certificate");
      return;
    }

    if (this.createCertificateFromOtherData.invalid) {
      this.toastrService.error("Please set a valid period.", "Could not create certificate");
      return;
    }

    const keyUsage = this.createKeyUsage();
    const extendedKeyUsage = this.createExtendedKeyUsage();
    const validFrom = formatDate(this.createCertificateFromOtherData.value.validFrom, "yyyy-MM-dd", "en-US");
    const validTo = formatDate(this.createCertificateFromOtherData.value.validTo, "yyyy-MM-dd", "en-US");
    const certificate = new Certificate(
      this.createCertificateFromSubject.value.selectedSubject,
      this.createCertificateFromSubject.value.selectedSubject,
      validFrom,
      validTo,
      this.createCertificateFromSubject.value.authorityKeyIdentifier,
      this.createCertificateFromSubject.value.subjectKeyIdentifier,
      true,
      keyUsage,
      extendedKeyUsage,
    );
    const createCertificate = new CreateCertificate(
      certificate,
      certificate,
      this.createCertificateInfoAboutKeyStorage.value.alias,
      this.createCertificateInfoAboutKeyStorage.value.password,
      this.createCertificateInfoAboutKeyStorage.value.privateKeyPassword,
    );

    this.certificateService.addNewSelfSignedCertificate(createCertificate).subscribe(
      () => {
        this.createCertificateFromOtherData.reset();
        this.createCertificateFromSubject.reset();
        this.createCertificateInfoAboutKeyStorage.reset();
        this.toastrService.success("New self-signed certificate has been created successfully.", "Certificate created");
        this.router.navigate(["/pages/list-certificates"]);
      },
      (e) => {
        this.toastrService.error("Failed to create a self-signed certificate.", "Could not create certificate");
      }
    );
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
    );
  }

  openAddSubject() {
    this.dialog.open(AddSubjectComponent);
  }

}
