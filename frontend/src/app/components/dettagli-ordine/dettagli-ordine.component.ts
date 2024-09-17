import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-dettagli-ordine',
  templateUrl: './dettagli-ordine.component.html',
  styleUrl: './dettagli-ordine.component.css'
})
export class DettagliOrdineComponent implements OnInit {
  ordine: any;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {}

  ngOnInit(): void {
    this.ordine = this.data.ordine;
  }
}
