import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'error',
  templateUrl: './error-internal-server.component.html',
  styleUrls: ['./error-internal-server.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ErrorInternalServerComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
