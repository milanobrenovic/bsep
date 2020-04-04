import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'error',
  templateUrl: './error-unauthenticated.component.html',
  styleUrls: ['./error-unauthenticated.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ErrorUnauthenticatedComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
