import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSelfSignedCertificateComponent } from './create-self-signed-certificate.component';

describe('CreateSelfSignedCertificateComponent', () => {
  let component: CreateSelfSignedCertificateComponent;
  let fixture: ComponentFixture<CreateSelfSignedCertificateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateSelfSignedCertificateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateSelfSignedCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
