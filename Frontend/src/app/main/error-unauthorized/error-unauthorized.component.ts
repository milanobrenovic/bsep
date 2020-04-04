import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'error',
  templateUrl: './error-unauthorized.component.html',
  styleUrls: ['./error-unauthorized.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ErrorUnauthorizedComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
