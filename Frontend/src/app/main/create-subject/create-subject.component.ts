import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { SubjectService } from 'app/services/subject.service';
import { Entity } from 'app/models/entity';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

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
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.initSubjectForm();
  }

  private initSubjectForm() {
    this.createSubjectForm = this.formBuilder.group({
      commonName: new FormControl(null, [Validators.required]),
      surname: new FormControl(null, [Validators.required]),
      givenName: new FormControl(null, [Validators.required]),
      organization: new FormControl(null, [Validators.required]),
      organizationUnit: new FormControl(null, [Validators.required]),
      country: new FormControl(null, [Validators.required, Validators.maxLength(2), Validators.minLength(2)]),
      email: new FormControl(null, [Validators.required, Validators.email]),
    });
  }

  public createSubject() {
    if (this.createSubjectForm.invalid) {
      this.toastrService.error("Please enter all required fields.", "Could not create subject");
      return;
    }

    const subject = new Entity(
      this.createSubjectForm.value.commonName,
      this.createSubjectForm.value.surname,
      this.createSubjectForm.value.givenName,
      this.createSubjectForm.value.organization,
      this.createSubjectForm.value.organizationUnit,
      this.createSubjectForm.value.country,
      this.createSubjectForm.value.email,
      false,
      false,
    );

    this.subjectService.createSubject(subject).subscribe(
      () => {
        this.toastrService.success("New subject created successfully.", "Subject created");
        this.router.navigate(["/pages/create-root-certificate"]);
      },
      (e: HttpErrorResponse) => {
        this.toastrService.error(e.error.message, "Could not create new subject");
      }
    );
  }

}
