import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewMachinesComponent } from './view-machines.component';

describe('ViewMachinesComponent', () => {
  let component: ViewMachinesComponent;
  let fixture: ComponentFixture<ViewMachinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewMachinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewMachinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
