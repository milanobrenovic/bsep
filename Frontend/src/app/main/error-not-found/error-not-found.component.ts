import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'error',
  templateUrl: './error-not-found.component.html',
  styleUrls: ['./error-not-found.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ErrorNotFoundComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
