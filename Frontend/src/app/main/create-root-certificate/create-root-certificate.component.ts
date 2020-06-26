import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, ValidatorFn } from '@angular/forms';
import { Entity } from 'app/models/entity';
import { ToastrService } from 'ngx-toastr';
import { SubjectService } from 'app/services/subject.service';
import { CertificateService } from 'app/services/certificate.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { formatDate } from '@angular/common';
import { KeyUsage } from 'app/models/keyUsage';
import { ExtendedKeyUsage } from 'app/models/extendedKeyUsage';
import { Certificate } from 'app/models/certificate';

const TimeValidator: ValidatorFn = (formGroup: FormGroup) => {
  const from = formGroup.get("validFrom").value;
  const to = formGroup.get("validTo").value;

  if (!from || !to) {
    return null;
  }

  if (from !== null && to !== null && from < to) {
    return null;
  }

  return { validError: true };
}

@Component({
  selector: 'app-create-root-certificate',
  templateUrl: './create-root-certificate.component.html',
  styleUrls: ['./create-root-certificate.component.scss']
})
export class CreateRootCertificateComponent implements OnInit {

  public createRootCertificateSubjectForm: FormGroup;
  public createRootCertificateValidityForm: FormGroup;
  public createRootCertificateKeyUsageExtensionsForm: FormGroup;
  public createRootCertificateExtendedKeyUsageExtensionsForm: FormGroup;
  public createRootCertificateAccessInformationForm: FormGroup;

  public minDate = new Date();
  public subjects: Entity[] = [];

  constructor(
    private certificateService: CertificateService,
    private subjectService: SubjectService,
    private toastrService: ToastrService,
    private formBuilder: FormBuilder,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.initSubjectForm();
    this.initValidityForm();
    this.initKeyUsageForm();
    this.initExtendedKeyUsageForm();
    this.initAccessInformationForm();
    this.getSubjects();
  }

  private initSubjectForm() {
    this.createRootCertificateSubjectForm = this.formBuilder.group({
      selectedSubject: new FormControl(null, [Validators.required]),
    });
  }

  private initValidityForm() {
    this.createRootCertificateValidityForm = this.formBuilder.group({
      validFrom: new FormControl(null, [Validators.required]),
      validTo: new FormControl(null, [Validators.required]),
    }, {
      validator: [TimeValidator],
    });
  }

  private initKeyUsageForm() {
    this.createRootCertificateKeyUsageExtensionsForm = this.formBuilder.group({
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
    });
  }

  private initExtendedKeyUsageForm() {
    this.createRootCertificateExtendedKeyUsageExtensionsForm = this.formBuilder.group({
      extendedKeyUsage: this.formBuilder.group({
        serverAuth: new FormControl(false),
        clientAuth: new FormControl(false),
        codeSigning: new FormControl(false),
        emailProtection: new FormControl(false),
        timeStamping: new FormControl(false),
        ocspSigning: new FormControl(false),
        dvcs: new FormControl(false),
      }),
    });
  }

  private initAccessInformationForm() {
    this.createRootCertificateAccessInformationForm = this.formBuilder.group({
      alias: new FormControl(null, [Validators.required]),
      password: new FormControl(null, [Validators.required]),
      keyStorePassword: new FormControl(null, [Validators.required]),
    });
  }
  
  private getSubjects() {
    this.subjectService.getAllSubjects().subscribe(
      (subjects: Entity[]) => {
        this.subjects = subjects;
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Failed to get subjects");
      }
    );
  }

  public getSelectedSubject() {
    return this.createRootCertificateSubjectForm.value.selectedSubject;
  }

  public createRootCertificate() {
    if (this.createRootCertificateSubjectForm.invalid) {
      this.toastrService.error("Please choose a subject.", "Could not create certificate");
      return;
    }

    if (this.createRootCertificateValidityForm.invalid) {
      this.toastrService.error("Please set the correct validity date.", "Could not create certificate");
      return;
    }

    if (this.createRootCertificateKeyUsageExtensionsForm.invalid) {
      this.toastrService.error("Please select at least one key usage extension.", "Could not create certificate");
      return;
    }

    if (this.createRootCertificateExtendedKeyUsageExtensionsForm.invalid) {
      this.toastrService.error("Please select at least one extended key usage extension.", "Could not create certificate");
      return;
    }

    if (!this.isAtLeastOneKeyUsageChecked()) {
      this.toastrService.error("Please select at least one key usage.", "Could not create certificate");
      return;
    }
    
    if (!this.isAtLeastOneExtendedKeyUsageChecked()) {
      this.toastrService.error("Please select at least one extended key usage.", "Could not create certificate");
      return;
    }

    if (this.createRootCertificateAccessInformationForm.invalid) {
      this.toastrService.error("Please fill out all fields for the access information.", "Could not create certificate");
      return;
    }

    const keyUsage = this.createKeyUsage();
    const extendedKeyUsage = this.createExtendedKeyUsage();
    
    const validFrom = formatDate(this.createRootCertificateValidityForm.value.validFrom, "yyyy-MM-dd", "en-US");
    const validTo = formatDate(this.createRootCertificateValidityForm.value.validTo, "yyyy-MM-dd", "en-US");

    const certificate = new Certificate(
      this.createRootCertificateSubjectForm.value.selectedSubject.id,
      this.createRootCertificateSubjectForm.value.selectedSubject.id,
      new Date(validFrom),
      new Date(validTo),
      this.createRootCertificateAccessInformationForm.value.alias,
      this.createRootCertificateAccessInformationForm.value.password,
      this.createRootCertificateAccessInformationForm.value.keyStorePassword,
      keyUsage,
      extendedKeyUsage,
      true
    );

    this.toastrService.info("Creating root certificate...", "Please wait");
    this.certificateService.createNewRootCertificate(certificate).subscribe(
      () => {
        this.toastrService.success("New self-signed certificate has been created successfully.", "Root certificate created");
        this.router.navigate(["/pages/list-certificates"]);
      },
      (e: HttpErrorResponse) => {
        if (e.status == 409) {
          this.toastrService.error("Root certificate under this alias name already exists.", "Failed to create a root certificate");
        }
        this.toastrService.error(e.error.message, "Could not create a root certificate");
      }
    );
  }

  private isAtLeastOneKeyUsageChecked(): boolean {
    let keyUsage = this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage;
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

  private isAtLeastOneExtendedKeyUsageChecked(): boolean {
    let keyUsage = this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage;
    return  keyUsage.serverAuth ||
            keyUsage.clientAuth ||
            keyUsage.codeSigning ||
            keyUsage.emailProtection ||
            keyUsage.timeStamping ||
            keyUsage.ocspSigning;
  }

  private createKeyUsage(): KeyUsage {
    return new KeyUsage(
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.certificateSigning,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.crlSign,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.dataEncipherment,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.decipherOnly,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.digitalSignature,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.encipherOnly,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.keyAgreement,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.keyEncipherment,
      this.createRootCertificateKeyUsageExtensionsForm.value.keyUsage.nonRepudiation,
    );
  }

  private createExtendedKeyUsage(): ExtendedKeyUsage {
    return new ExtendedKeyUsage(
      this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.serverAuth,
      this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.clientAuth,
      this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.codeSigning,
      this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.emailProtection,
      this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.timeStamping,
      this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.ocspSigning,
      this.createRootCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.dvcs,
    );
  }

}
