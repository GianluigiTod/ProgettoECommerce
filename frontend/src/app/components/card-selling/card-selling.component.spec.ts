import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardSellingComponent } from './card-selling.component';

describe('CardSellingComponent', () => {
  let component: CardSellingComponent;
  let fixture: ComponentFixture<CardSellingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CardSellingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardSellingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
