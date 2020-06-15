import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Entity } from 'app/models/entity';
import { Certificate } from 'app/models/certificate';
import { Subscription } from 'rxjs';
import { Template } from 'app/models/template';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { SubjectService } from 'app/services/subject.service';
import { CertificateService } from 'app/services/certificate.service';
import { Router } from '@angular/router';
import { KeyUsage } from 'app/models/keyUsage';
import { ExtendedKeyUsage } from 'app/models/extendedKeyUsage';
import { formatDate } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { CreateSubjectComponent } from '../create-subject/create-subject.component';
import { Subject } from 'app/models/subject';

@Component({
  selector: 'app-create-certificate',
  templateUrl: './create-certificate.component.html',
  styleUrls: ['./create-certificate.component.scss']
})
export class CreateCertificateComponent implements OnInit {

  public createCertificateFormSubject: FormGroup;
  public createCertificateFormIssuer: FormGroup;
  public createCertificateFormOtherData: FormGroup;
  public createCertificateInfoAboutKeyStorage: FormGroup;
  public createCertificateKeyStoragePasswords: FormGroup;

  public minDate = new Date();
  public subjects: Entity[] = [];
  public issuerCertificates: Subject[] = [];
  public createdNewSubject: Subscription;
  public selectedTemplate: Template;

  constructor(
    private toastr: ToastrService,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    private subjectService: SubjectService,
    private certificateService: CertificateService,
    private router: Router,
  ) { }
  
  ngOnInit() {
    this.createCertificateFormSubject = this.formBuilder.group({
      selectedSubject: new FormControl(null, Validators.required),
    });

    this.createCertificateKeyStoragePasswords = this.formBuilder.group({
      rootKeyStoragePassword: new FormControl(null),
      intermediateKeyStoragePassword: new FormControl(null),
    });

    this.createCertificateFormIssuer = this.formBuilder.group({
      selectedIssuerCertificate: new FormControl(null, Validators.required),
    });

    this.functionForCreatingFormCertificateFormOtherData();
    this.functionForCreatingFormCertificateInfoAboutKeyStorage();

    this.getSubjects();

    this.createdNewSubject = this.subjectService.createSuccessEmitter.subscribe(
      () => {
        this.getSubjects();
      }
    );

    if (JSON.parse(localStorage.getItem('selectedTemplate'))) {
      this.selectedTemplate = JSON.parse(localStorage.getItem('selectedTemplate'));
      localStorage.removeItem('selectedTemplate');
      this.setExtensions();
    }
  }
  
  getSubjects(): void {
    this.subjectService.getAllSubjectsWithoutCertificate().subscribe((subjects: Entity[]) => {
      this.subjects = subjects;
    })
  }
  
  getKeyUsages(issuerCertificate: Certificate): string {
    if (!issuerCertificate.keyUsageDTO) {
      return " (certS, crlS, dataE, decO, digS, encO, keyAgr, keyE, nonRep)";
    }
    var keyUsages = '(';
    if (issuerCertificate.keyUsageDTO.certificateSigning) {
      keyUsages += 'certS, ';
    }
    if (issuerCertificate.keyUsageDTO.crlSign) {
      keyUsages += 'crlS, ';
    }
    if (issuerCertificate.keyUsageDTO.dataEncipherment) {
      keyUsages += 'dataE, ';
    }
    if (issuerCertificate.keyUsageDTO.decipherOnly) {
      keyUsages += 'decO, ';
    }
    if (issuerCertificate.keyUsageDTO.digitalSignature) {
      keyUsages += 'digS, ';
    }
    if (issuerCertificate.keyUsageDTO.encipherOnly) {
      keyUsages += 'encO, ';
    }
    if (issuerCertificate.keyUsageDTO.keyAgreement) {
      keyUsages += 'keyAgr, ';
    }
    if (issuerCertificate.keyUsageDTO.keyEncipherment) {
      keyUsages += 'keyE, ';
    }
    if (issuerCertificate.keyUsageDTO.nonRepudiation) {
      keyUsages += 'nonRep, ';
    }
    if (keyUsages.length == 1) {
      return '';
    }

    keyUsages = keyUsages.substring(0, keyUsages.length - 2);
    keyUsages += ')';

    return keyUsages;
  }

  functionForCreatingFormCertificateInfoAboutKeyStorage() {
    this.createCertificateInfoAboutKeyStorage = this.formBuilder.group({
      alias: new FormControl(null, Validators.required),
      password: new FormControl(null, Validators.required),
      privateKeyPassword: new FormControl(null, Validators.required),
      issuerKeyStorePassword: new FormControl(null, Validators.required),
      issuerPrivateKeyPassword: new FormControl(null, Validators.required)
    });
  }

  functionForCreatingFormCertificateFormOtherData() {
    this.createCertificateFormOtherData = this.formBuilder.group({
      validFrom: new FormControl(null, Validators.required),
      validTo: new FormControl(null, Validators.required),
      authorityKeyIdentifier: new FormControl(false, Validators.required),
      subjectKeyIdentifier: new FormControl(false, Validators.required),
      subjectIsCa: new FormControl(false, Validators.required),
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
      extendedKeyUsage: this.formBuilder.group({
        serverAuth: new FormControl(false),
        clientAuth: new FormControl(false),
        codeSigning: new FormControl(false),
        emailProtection: new FormControl(false),
        timeStamping: new FormControl(false),
        ocspSigning: new FormControl(false)
      }),
    });
  }

  getSelectedSubject() {
    return this.createCertificateFormSubject.get('selectedSubject').value;
  }

  getSelectedIssuerCertificate() {
    return this.createCertificateFormIssuer.get('selectedIssuerCertificate').value;
  }

  getSelectedIssuerCertificateValidTo() {
    if (this.getSelectedIssuerCertificate()) {
      return this.getSelectedIssuerCertificate().validTo;
    }
    return new Date();
  }

  createCertificate() {
    if (this.createCertificateFormSubject.invalid) {
      this.toastr.error("Please choose a subject.", 'Failed to create a certificate');
      return;
    }

    if (this.createCertificateFormIssuer.invalid) {
      this.toastr.error("Please choose an issuer.", 'Failed to create a certificate');
      return;
    }

    if (this.createCertificateFormOtherData.invalid) {
      this.toastr.error("Please set a valid period.", 'Failed to create a certificate');
      return;
    }

    if (!this.checkKeyUsage()) {
      this.toastr.error("Please select at least one key usage.", "Failed to create a certificate");
      return;
    }

    if (!this.checkExtendedKeyUsage()) {
      this.toastr.error("Please select at least one extended key usage.", "Failed to create a certificate");
      return;
    }

    const keyUsage = this.createKeyUsage();
    const extendedKeyUsage = this.createExtendedKeyUsage();

    const validFrom = formatDate(this.createCertificateFormOtherData.value.validFrom, 'yyyy-MM-dd', 'en-US')
    const validTo = formatDate(this.createCertificateFormOtherData.value.validTo, 'yyyy-MM-dd', 'en-US')

    console.log(this.createCertificateFormSubject.value.selectedSubject.id);
    console.log(this.createCertificateFormIssuer.value.selectedIssuerCertificate.id);

    const certificate = new Certificate(
      this.createCertificateFormSubject.value.selectedSubject.id,
      this.createCertificateFormIssuer.value.selectedIssuerCertificate.id,
      new Date(validFrom),
      new Date(validTo),
      this.createCertificateInfoAboutKeyStorage.value.alias,
      this.createCertificateInfoAboutKeyStorage.value.password,
      keyUsage,
      extendedKeyUsage,
    );

    this.certificateService.createNewCertificate(certificate).subscribe(
      () => {
        this.createCertificateFormOtherData.reset();
        this.createCertificateFormSubject.reset();
        this.createCertificateFormIssuer.reset();
        this.createCertificateInfoAboutKeyStorage.reset();
        this.toastr.success('Successfully created a new certificate.', 'Create certificate');
        this.router.navigate(['/pages/list-certificates']);
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, 'Failed to create a certificate');
      }
    );
  }

  checkKeyUsage(): boolean {
    if (!this.createCertificateFormOtherData.value.subjectIsCa) {
      return true;
    }
    let keyUsage = this.createCertificateFormOtherData.value.keyUsage;
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
    if (!this.createCertificateFormOtherData.value.subjectIsCa) {
      return true;
    }
    let extendedKeyUsage = this.createCertificateFormOtherData.value.extendedKeyUsage;
    return  extendedKeyUsage.serverAuth ||
            extendedKeyUsage.clientAuth ||
            extendedKeyUsage.codeSigning ||
            extendedKeyUsage.emailProtection ||
            extendedKeyUsage.timeStamping ||
            extendedKeyUsage.ocspSigning;
  }

  createKeyUsage(): KeyUsage {
    return new KeyUsage(
      this.createCertificateFormOtherData.value.keyUsage.certificateSigning,
      this.createCertificateFormOtherData.value.keyUsage.crlSign,
      this.createCertificateFormOtherData.value.keyUsage.dataEncipherment,
      this.createCertificateFormOtherData.value.keyUsage.decipherOnly,
      this.createCertificateFormOtherData.value.keyUsage.digitalSignature,
      this.createCertificateFormOtherData.value.keyUsage.enchiperOnly,
      this.createCertificateFormOtherData.value.keyUsage.keyAgreement,
      this.createCertificateFormOtherData.value.keyUsage.keyEncipherment,
      this.createCertificateFormOtherData.value.keyUsage.nonRepudiation
    )
  }

  createExtendedKeyUsage(): ExtendedKeyUsage {
    return new ExtendedKeyUsage(
      this.createCertificateFormOtherData.value.extendedKeyUsage.serverAuth,
      this.createCertificateFormOtherData.value.extendedKeyUsage.clientAuth,
      this.createCertificateFormOtherData.value.extendedKeyUsage.codeSigning,
      this.createCertificateFormOtherData.value.extendedKeyUsage.emailProtection,
      this.createCertificateFormOtherData.value.extendedKeyUsage.timeStamping,
      this.createCertificateFormOtherData.value.extendedKeyUsage.ocspSigning,
      this.createCertificateFormOtherData.value.extendedKeyUsage.dvcs,
    )
  }

  setExtensions() {
    this.createCertificateFormOtherData.patchValue(
      {
        'authorityKeyIdentifier': this.selectedTemplate.authorityKeyId,
        'subjectKeyIdentifier': this.selectedTemplate.subjectKeyId,
        'subjectIsCa': this.selectedTemplate.CA,
        'keyUsageDTO': {
          'digitalSignature': this.selectedTemplate.digitalSignature,
          'keyEncipherment': this.selectedTemplate.keyEncipherment,
          'certificateSigning': this.selectedTemplate.certSigning,
          'crlSign': this.selectedTemplate.CRLSign,
        },
        'extendedKeyUsageDTO': {
          'serverAuth': this.selectedTemplate.TLSWebServerAuth,
          'clientAuth': this.selectedTemplate.TLSWebClientAuth,
          'codeSigning': this.selectedTemplate.codeSigning
        }
      }
    );
  }

  setExtensionsAfterChoosingIssuer() {
    if (!this.getSelectedIssuerCertificate().keyUsage) {
      return;
    }
    if (!this.selectedTemplate) {
      return;
    }
    this.functionForCreatingFormCertificateFormOtherData();
    this.createCertificateFormOtherData.patchValue(
      {
        'keyUsage': {
          'digitalSignature': this.selectedTemplate.digitalSignature && this.getSelectedIssuerCertificate().keyUsage.digitalSignature,
          'keyEncipherment': this.selectedTemplate.keyEncipherment && this.getSelectedIssuerCertificate().keyUsage.keyEncipherment,
          'certificateSigning': this.selectedTemplate.certSigning && this.getSelectedIssuerCertificate().keyUsage.certificateSigning,
          'crlSign': this.selectedTemplate.CRLSign && this.getSelectedIssuerCertificate().keyUsage.crlSign,
        },
        'extendedKeyUsage': {
          'serverAuth': this.selectedTemplate.TLSWebServerAuth && this.getSelectedIssuerCertificate().extendedKeyUsage.serverAuth,
          'clientAuth': this.selectedTemplate.TLSWebClientAuth && this.getSelectedIssuerCertificate().extendedKeyUsage.clientAuth,
          'codeSigning': this.selectedTemplate.codeSigning && this.getSelectedIssuerCertificate().extendedKeyUsage.codeSigning,
        }
      }
    );
  }

  openAddSubject() {
    this.dialog.open(CreateSubjectComponent);
  }

  getCACertificates() {
    this.createCertificateFormIssuer.patchValue(
      {
        "selectedIssuerCertificate": null,
      }
    );

    this.certificateService.getCACertificates().subscribe(
      (issuers: Subject[]) => {
        this.issuerCertificates = issuers;
      },
      (e: HttpErrorResponse) => {
        this.toastr.error(e.error.message, "Failed to get CA certificates");
      }
    );
  }

  checkIfIssuerCertificateExtendedKeyUsageExists() {
    const selectedIssuerCertificate = this.createCertificateFormIssuer.value.selectedIssuerCertificate;
    if (!selectedIssuerCertificate) {
      return false;
    } else if (!selectedIssuerCertificate.extendedKeyUsage) {
      return false;
    }
    return true;
  }

  checkIfIssuerCertificateKeyUsageExists() {
    const selectedIssuerCertificate = this.createCertificateFormIssuer.value.selectedIssuerCertificate;
    if (!selectedIssuerCertificate) {
      return false;
    } else if (!selectedIssuerCertificate.keyUsage) {
      return false;
    }
    return true;
  }

}
