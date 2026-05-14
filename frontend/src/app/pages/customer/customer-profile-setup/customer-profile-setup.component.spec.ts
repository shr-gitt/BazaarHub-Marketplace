import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerProfileSetupComponent } from './customer-profile-setup.component';

describe('CustomerProfileSetupComponent', () => {
  let component: CustomerProfileSetupComponent;
  let fixture: ComponentFixture<CustomerProfileSetupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerProfileSetupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerProfileSetupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
