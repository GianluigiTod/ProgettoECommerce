import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AreaPrivataComponent } from './area-privata.component';

describe('AreaPrivataComponent', () => {
  let component: AreaPrivataComponent;
  let fixture: ComponentFixture<AreaPrivataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AreaPrivataComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AreaPrivataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
