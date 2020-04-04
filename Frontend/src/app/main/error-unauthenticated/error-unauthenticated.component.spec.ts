import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorUnauthenticatedComponent } from './error-unauthenticated.component';

describe('ErrorUnauthenticatedComponent', () => {
  let component: ErrorUnauthenticatedComponent;
  let fixture: ComponentFixture<ErrorUnauthenticatedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ErrorUnauthenticatedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorUnauthenticatedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
