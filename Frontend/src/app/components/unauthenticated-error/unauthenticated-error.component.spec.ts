import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnauthenticatedErrorComponent } from './unauthenticated-error.component';

describe('UnauthenticatedErrorComponent', () => {
  let component: UnauthenticatedErrorComponent;
  let fixture: ComponentFixture<UnauthenticatedErrorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnauthenticatedErrorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnauthenticatedErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
