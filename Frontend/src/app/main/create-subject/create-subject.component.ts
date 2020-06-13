import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { SubjectService } from 'app/services/subject.service';
import { Entity } from 'app/models/entity';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-create-subject',
  templateUrl: './create-subject.component.html',
  styleUrls: ['./create-subject.component.scss']
})
export class CreateSubjectComponent implements OnInit {

  public createSubjectForm: FormGroup;

  constructor(
    private toastrService: ToastrService,
    private subjectService: SubjectService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    this.createSubjectForm = this.formBuilder.group({
      commonName: new FormControl(null, [Validators.required]),
      surname: new FormControl(null, [Validators.required]),
      givename: new FormControl(null, [Validators.required]),
      organization: new FormControl(null, [Validators.required, Validators.maxLength(64)]),
      organizationUnit: new FormControl(null, [Validators.required]),
      countryCode: new FormControl(null, [Validators.required, Validators.maxLength(2), Validators.minLength(2)]),
      email: new FormControl(null, [Validators.required, Validators.email]),
    });
  }

  onChange() {
    this.createSubjectForm.patchValue({
      "commonName": null,
      "surname": null,
      "givename": null,
      "organization": null,
      "organizationUnit": null,
      "countryCode": null,
      "email": null,
    });
  }

  create() {
    if (this.createSubjectForm.invalid) {
      this.toastrService.error("Please enter all required fields.", "Could not create subject");
      return;
    }

    const subject = new Entity(
      this.createSubjectForm.value.commonName,
      this.createSubjectForm.value.surname,
      this.createSubjectForm.value.givename,
      this.createSubjectForm.value.organization,
      this.createSubjectForm.value.organizationUnit,
      this.createSubjectForm.value.countryCode,
      this.createSubjectForm.value.email,
      false,
      false,
    );
    
    this.subjectService.createSubject(subject).subscribe(
      () => {
        this.createSubjectForm.reset();
        this.toastrService.success("New subject added successfully.", "Subject created");
        this.subjectService.createSuccessEmitter.next(subject);
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Could not create new subject");
      }
    );
  }

}
