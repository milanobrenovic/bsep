import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialogRef } from '@angular/material/dialog';
import { Template } from 'app/models/template';

@Component({
  selector: 'app-choose-template',
  templateUrl: './choose-template.component.html',
  styleUrls: ['./choose-template.component.scss']
})
export class ChooseTemplateComponent implements OnInit {

  chooseTemplateForm: FormGroup;
  templateSelected: Template;
  isSelfSigned: boolean;

  blank = new Template(false, false, false, false, false, false, false, false, false, false);
  ca = new Template(true, true, true, false, false, true, true, false, false, false);
  sslServer = new Template(true, true, false, true, true, false, false, true, false, false);
  sslClient = new Template(true, true, false, true, true, false, false, false, true, false);
  codeSigning = new Template(true, true, false, true, false, false, false, false, false, true);

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ChooseTemplateComponent>
  ) {
    this.templateSelected = this.blank;
    this.isSelfSigned = false;
  }

  ngOnInit() {
    this.chooseTemplateForm = this.formBuilder.group({
      template: new FormControl(this.blank, Validators.required),
      selfSigned: new FormControl(false, null),
    });
  }

  chooseTemplate() {
    this.templateSelected = this.chooseTemplateForm.value.template;
    this.isSelfSigned = this.chooseTemplateForm.value.selfSigned;
    this.dialogRef.close();

    if (JSON.parse(localStorage.getItem('selectedTemplate'))) {
      localStorage.removeItem('selectedTemplate');
    }
    localStorage.setItem('selectedTemplate', JSON.stringify(this.templateSelected));

    if (this.isSelfSigned) {
      this.router.navigate(['/pages/create-self-signed-certificate']);
    } else {
      this.router.navigate(['/pages/create-certificate']);
    }
  }

}
