import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { Entity } from 'app/models/entity';
import { Certificate } from 'app/models/certificate';
import { ToastrService } from 'ngx-toastr';
import { SubjectService } from 'app/services/subject.service';
import { CertificateService } from 'app/services/certificate.service';
import { Router } from '@angular/router';
import { KeyUsage } from 'app/models/keyUsage';
import { ExtendedKeyUsage } from 'app/models/extendedKeyUsage';
import { formatDate } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject } from 'app/models/subject';

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

const PasswordStrengthValidator = function (control: AbstractControl): ValidationErrors | null {
  let value: string = control.value || '';
  let msg = "";

  if (!value) {
    return null
  }

  let upperCaseCharacters = /[A-Z]+/g;
  let lowerCaseCharacters = /[a-z]+/g;
  let numberCharacters = /[0-9]+/g;
  let specialCharacters = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
  if (upperCaseCharacters.test(value) === false || lowerCaseCharacters.test(value) === false || numberCharacters.test(value) === false || specialCharacters.test(value) === false) {
    return {
      passwordStrength: "Password must contain at least two of the following: numbers, lowercase letters, uppercase letters, or special characters.",
    }
  }
}

const KeyStorePasswordStrengthValidator = function (control: AbstractControl): ValidationErrors | null {
  let value: string = control.value || '';
  let msg = "";

  if (!value) {
    return null
  }

  let upperCaseCharacters = /[A-Z]+/g;
  let lowerCaseCharacters = /[a-z]+/g;
  let numberCharacters = /[0-9]+/g;
  let specialCharacters = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
  if (upperCaseCharacters.test(value) === false || lowerCaseCharacters.test(value) === false || numberCharacters.test(value) === false || specialCharacters.test(value) === false) {
    return {
      passwordStrength: "Key store password must contain at least two of the following: numbers, lowercase letters, uppercase letters, or special characters.",
    }
  }
}

@Component({
  selector: 'app-create-certificate',
  templateUrl: './create-certificate.component.html',
  styleUrls: ['./create-certificate.component.scss']
})
export class CreateCertificateComponent implements OnInit {

  public createCertificateSubjectForm: FormGroup;
  public createCertificateExistingIssuerForm: FormGroup;
  public createCertificateValidityForm: FormGroup;
  public createCertificateExtensionsForm: FormGroup;
  public createCertificateKeyUsageExtensionsForm: FormGroup;
  public createCertificateExtendedKeyUsageExtensionsForm: FormGroup;
  public createCertificateAccessInformationForm: FormGroup;

  public createCertificateFormIssuer: FormGroup;
  public createCertificateFormOtherData: FormGroup;
  public createCertificateKeyStoragePasswords: FormGroup;

  public minDate = new Date();
  public subjects: Entity[] = [];
  public issuerCertificates: Subject[] = [];

  constructor(
    private toastrService: ToastrService,
    private formBuilder: FormBuilder,
    private subjectService: SubjectService,
    private certificateService: CertificateService,
    private router: Router,
  ) { }
  
  ngOnInit() {
    this.initSubjectForm();
    this.initExistingIssuerForm();
    this.initValidityForm();
    this.initExtensionsForm();
    this.initKeyUsageExtensionsForm();
    this.initExtendedKeyUsageExtensionsForm();
    this.initAccessInformationForm();
    this.getSubjects();
  }

  private initSubjectForm() {
    this.createCertificateSubjectForm = this.formBuilder.group({
      selectedSubject: new FormControl(null, [Validators.required]),
    });
  }

  private initExistingIssuerForm() {
    this.createCertificateExistingIssuerForm = this.formBuilder.group({
      keyStorePassword:  new FormControl(null, [Validators.compose([
        Validators.required,
        Validators.minLength(8),
        PasswordStrengthValidator,
      ])]),
      selectedIssuerCertificate: new FormControl(null, Validators.required),
    });
  }

  private initValidityForm() {
    this.createCertificateValidityForm = this.formBuilder.group({
      validFrom: new FormControl(null, [Validators.required]),
      validTo: new FormControl(null, [Validators.required]),
    }, {
      validator: [TimeValidator],
    });
  }

  private initExtensionsForm() {
    this.createCertificateExtensionsForm = this.formBuilder.group({
      subjectIsCa: new FormControl(false),
    });
  }

  private initKeyUsageExtensionsForm() {
    this.createCertificateKeyUsageExtensionsForm = this.formBuilder.group({
      keyUsage: this.formBuilder.group({
        certificateSigning: new FormControl(false),
        crlSign: new FormControl(false),
        dataEncipherment: new FormControl(false),
        decipherOnly: new FormControl(false),
        digitalSignature: new FormControl(false),
        enchiperOnly: new FormControl(false),
        keyAgreement: new FormControl(false),
        keyEncipherment: new FormControl(false),
        nonRepudiation: new FormControl(false),
      }),
    });
  }

  private initExtendedKeyUsageExtensionsForm() {
    this.createCertificateExtendedKeyUsageExtensionsForm = this.formBuilder.group({
      extendedKeyUsage: this.formBuilder.group({
        serverAuth: new FormControl(false),
        clientAuth: new FormControl(false),
        codeSigning: new FormControl(false),
        emailProtection: new FormControl(false),
        timeStamping: new FormControl(false),
        ocspSigning: new FormControl(false),
      }),
    });
  }

  private initAccessInformationForm() {
    this.createCertificateAccessInformationForm = this.formBuilder.group({
      alias: new FormControl(null, [Validators.required]),
      password:  new FormControl(null, [Validators.compose([
        Validators.required,
        Validators.minLength(8),
        PasswordStrengthValidator,
      ])]),
      keyStorePassword:  new FormControl(null, [Validators.compose([
        Validators.required,
        Validators.minLength(8),
        KeyStorePasswordStrengthValidator,
      ])]),
    });
  }
  
  private getSubjects() {
    this.subjectService.getAllSubjectsWithoutCertificate().subscribe(
      (subjects: Entity[]) => {
        this.subjects = subjects;
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, 'Failed to get subjects');
      }
    );
  }
  
  public getSelectedSubject() {
    return this.createCertificateSubjectForm.value.selectedSubject;
  }

  public getSelectedIssuerCertificate() {
    return this.createCertificateExistingIssuerForm.value.selectedIssuerCertificate;
  }

  public getSelectedIssuerCertificateValidTo() {
    return this.getSelectedIssuerCertificate() ? this.getSelectedIssuerCertificate().validTo : new Date();
  }

  public createCertificate() {
    if (this.createCertificateSubjectForm.invalid) {
      this.toastrService.error("Please choose a subject.", "Failed to create a certificate");
      return;
    }

    if (this.createCertificateExistingIssuerForm.invalid) {
      this.toastrService.error("Please choose the existing issuer.", "Failed to create a certificate");
      return;
    }

    if (this.createCertificateValidityForm.invalid) {
      this.toastrService.error("Please select a valid date.", "Failed to create a certificate");
      return;
    }

    if (this.createCertificateExtensionsForm.invalid) {
      this.toastrService.error("Please select the proper extensions.", "Failed to create a certificate");
      return;
    }

    if (!this.checkKeyUsage()) {
      this.toastrService.error("Please select at least one key usage extension.", "Failed to create a certificate");
      return;
    }

    if (!this.checkExtendedKeyUsage()) {
      this.toastrService.error("Please select at least one extended key usage extension.", "Failed to create a certificate");
      return;
    }

    if (this.createCertificateAccessInformationForm.invalid) {
      this.toastrService.error("Please fill out all the required password fields.", "Failed to create a certificate");
      return;
    }

    const keyUsage = this.createKeyUsage();
    const extendedKeyUsage = this.createExtendedKeyUsage();

    const validFrom = formatDate(this.createCertificateValidityForm.value.validFrom, 'yyyy-MM-dd', 'en-US');
    const validTo = formatDate(this.createCertificateValidityForm.value.validTo, 'yyyy-MM-dd', 'en-US');

    const certificate = new Certificate(
      this.createCertificateSubjectForm.value.selectedSubject.id,
      this.createCertificateExistingIssuerForm.value.selectedIssuerCertificate.id,
      new Date(validFrom),
      new Date(validTo),
      this.createCertificateAccessInformationForm.value.alias,
      this.createCertificateAccessInformationForm.value.password,
      this.createCertificateAccessInformationForm.value.keyStorePassword,
      keyUsage,
      extendedKeyUsage,
    );

    this.toastrService.info("Creating certificate...", "Please wait");
    this.certificateService.createNewCertificate(certificate).subscribe(
      () => {
        this.toastrService.success("New certificate has been created successfully.", "Success");
        this.router.navigate(["/pages/list-certificates"]);
      },
      (e: HttpErrorResponse) => {
        if (e.status == 409) {
          this.toastrService.error("Certificate under this alias name already exists.", "Failed to create a certificate");
        }
        this.toastrService.error(e.error.message, "Failed to create a certificate");
      }
    );
  }

  private checkKeyUsage(): boolean {
    if (!this.createCertificateExtensionsForm.value.subjectIsCa) {
      return true;
    }

    let keyUsage = this.createCertificateKeyUsageExtensionsForm.value.keyUsage;
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

  private checkExtendedKeyUsage(): boolean {
    if (!this.createCertificateExtensionsForm.value.subjectIsCa) {
      return true;
    }

    let extendedKeyUsage = this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage;
    return  extendedKeyUsage.serverAuth ||
            extendedKeyUsage.clientAuth ||
            extendedKeyUsage.codeSigning ||
            extendedKeyUsage.emailProtection ||
            extendedKeyUsage.timeStamping ||
            extendedKeyUsage.ocspSigning;
  }

  private createKeyUsage(): KeyUsage {
    return new KeyUsage(
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.certificateSigning,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.crlSign,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.dataEncipherment,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.decipherOnly,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.digitalSignature,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.enchiperOnly,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.keyAgreement,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.keyEncipherment,
      this.createCertificateKeyUsageExtensionsForm.value.keyUsage.nonRepudiation,
    );
  }

  private createExtendedKeyUsage(): ExtendedKeyUsage {
    return new ExtendedKeyUsage(
      this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.serverAuth,
      this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.clientAuth,
      this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.codeSigning,
      this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.emailProtection,
      this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.timeStamping,
      this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.ocspSigning,
      this.createCertificateExtendedKeyUsageExtensionsForm.value.extendedKeyUsage.dvcs,
    );
  }

  public getCACertificates() {
    const keyStorePassword = this.createCertificateExistingIssuerForm.value.keyStorePassword;
    this.certificateService.getCACertificates(keyStorePassword).subscribe(
      (issuers: Subject[]) => {
        this.issuerCertificates = issuers;
        this.toastrService.success("Keystore password was successful.", "Success");
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Failed to get CA certificates");
      }
    );
  }

  public issuerExtendedKeyUsageExists() {
    const selectedIssuerCertificate = this.createCertificateExistingIssuerForm.value.selectedIssuerCertificate;
    return (!selectedIssuerCertificate || !selectedIssuerCertificate.extendedKeyUsage) ? false : true;
  }

  public issuerKeyUsageExists() {
    const selectedIssuerCertificate = this.createCertificateExistingIssuerForm.value.selectedIssuerCertificate;
    return (!selectedIssuerCertificate || !selectedIssuerCertificate.keyUsage) ? false : true;
  }

}
