import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRootCertificateComponent } from './create-root-certificate.component';

describe('CreateRootCertificateComponent', () => {
  let component: CreateRootCertificateComponent;
  let fixture: ComponentFixture<CreateRootCertificateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateRootCertificateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateRootCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
