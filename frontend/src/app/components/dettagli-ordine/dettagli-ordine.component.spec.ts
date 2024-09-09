import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DettagliOrdineComponent } from './dettagli-ordine.component';

describe('DettagliOrdineComponent', () => {
  let component: DettagliOrdineComponent;
  let fixture: ComponentFixture<DettagliOrdineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DettagliOrdineComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DettagliOrdineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
