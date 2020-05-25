import { Component, OnInit } from '@angular/core';
import { ValidatorFn, FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { SubjectService } from 'app/services/subject.service';
import { Entity } from 'app/models/entity';
import { HttpErrorResponse } from '@angular/common/http';

const ValidForm: ValidatorFn = (formGroup: FormGroup) => {
  // const type = formGroup.get("type").value;

  // if (type == "user") {
  if (!formGroup.get("email").value || !formGroup.get("surname").value || !formGroup.get("givename").value) {
    return { formIsInvalid: true };
  }
  // } else {
  //   if (!formGroup.get("organizationUnit").value) {
  //     return { formIsInvalid: true };
  //   }
  // }

  return null;
}

@Component({
  selector: 'app-add-subject',
  templateUrl: './add-subject.component.html',
  styleUrls: ['./add-subject.component.scss']
})
export class AddSubjectComponent implements OnInit {

  createSubjectForm: FormGroup;

  constructor(
    private toastrService: ToastrService,
    private subjectService: SubjectService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    
    this.createSubjectForm = this.formBuilder.group({
      // type: new FormControl("user", [Validators.required]),
      commonName: new FormControl("", [Validators.required]),
      surname: new FormControl(""),
      givename: new FormControl(""),
      organization: new FormControl("", [Validators.required, Validators.maxLength(64)]),
      organizationUnit: new FormControl(""),
      countryCode: new FormControl("", [Validators.required, Validators.maxLength(2), Validators.minLength(2)]),
      email: new FormControl("", [Validators.email]),
    }, {
      validator: [ValidForm],
    });
  }

  onChange() {
    this.createSubjectForm.patchValue({
      "commonName": "",
      "surname": "",
      "givename": "",
      "organization": "",
      "organizationUnit": "",
      "countryCode": "",
      "email": "",
    });
  }

  create() {
    if (this.createSubjectForm.invalid) {
      this.toastrService.error("Please enter all required fields.", "Could not create subject");
      return;
    }

    const subject = new Entity(
      // this.createSubjectForm.value.type,
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
