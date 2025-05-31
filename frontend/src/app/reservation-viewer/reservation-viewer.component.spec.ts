import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationViewerComponent } from './reservation-viewer.component';

describe('ReservationViewerComponent', () => {
  let component: ReservationViewerComponent;
  let fixture: ComponentFixture<ReservationViewerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationViewerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReservationViewerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
