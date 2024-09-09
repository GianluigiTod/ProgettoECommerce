import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {AuthService} from "../../service/auth.service";
import {Observable} from "rxjs";
import {API} from "../../constants";
import {MessageComponent} from "../finestraMessaggi/message/message.component";
import {MatDialog} from "@angular/material/dialog";
import {DettagliOrdineComponent} from "../dettagli-ordine/dettagli-ordine.component";

@Component({
  selector: 'app-lista-ordini',
  templateUrl: './lista-ordini.component.html',
  styleUrl: './lista-ordini.component.css'
})

export class ListaOrdiniComponent implements OnInit {
  orders: any[] = [];
  ordiniNonArrivati: any[] = [];
  ordiniArrivati: any[] = [];
  token: string = '';
  username: string = '';

  errorMessage: string = '';

  constructor(private http: HttpClient, private authService: AuthService, private dialog: MatDialog) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    const username = this.authService.getUsername();

    this.username=username;
    this.token=token;
    this.getOrders();
  }

  private getUser(username: string, token: string) : Observable<any> {
    const url = API.backend+'/api/user/get';
    const options = {
      headers: new HttpHeaders().set('Authorization', `Bearer ${token}`),
      params: { username }
    };

    return this.http.get(url, options);
  }

  getOrders(): void {
    this.getUser(this.username, this.token).subscribe(user => {
      const userId = user.id;

      this.http.get<any>(API.backend + `/api/order/${userId}`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
        responseType: 'json' as 'json'
      }).subscribe((orders: any[]) => {
        this.orders = orders;
        this.ordiniNonArrivati = this.orders.filter(ordine => !ordine.arrivato);
        this.ordiniArrivati = this.orders.filter(ordine => ordine.arrivato);
      }, error => {
        if (error.error && typeof error.error === 'string') {
          this.errorMessage = error.error;
        } else if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Si è verificato un errore durante il recupero degli ordini. Riprova.';
        }
      });
    }, error => {
      if (error.error && typeof error.error === 'string') {
        this.errorMessage = error.error;
      } else if (error.error && error.error.message) {
        this.errorMessage = error.error.message;
      } else {
        this.errorMessage = 'Si è verificato un errore durante il recupero dell\'utente. Riprova.';
      }
    });
  }

  confermaArrivo(id: number): void {
    this.http.put(API.backend+`/api/order/update/${id}`, {}, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'text' as "json"
    }).subscribe(response => {
      console.log('Conferma arrivo:', response);
      this.getOrders(); // Aggiorno la lista degli ordini
    }, error => {
      if (error.error && typeof error.error === 'string') {
        this.errorMessage = error.error;
      } else if (error.error && error.error.message) {
        this.errorMessage = error.error.message;
      } else {
        this.errorMessage = 'Si è verificato un errore durante la modifica. Riprova.';
      }
    });
  }

  eliminaOrdine(id: number): void {
    this.http.delete(API.backend+`/api/order/delete/${id}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'text' as "json"
    }).subscribe(response => {
      console.log('Eliminazione ordine:', response);

      this.dialog.open(MessageComponent, {
        data: {message: 'Cancellazione avvenuta con successo'}
      });

      this.getOrders(); // Aggiorno la lista degli ordini
    }, error => {
      if (error.error && typeof error.error === 'string') {
        this.errorMessage = error.error;
      } else if (error.error && error.error.message) {
        this.errorMessage = error.error.message;
      } else {
        this.errorMessage = 'Si è verificato un errore durante la modifica. Riprova.';
      }
    });
  }

  visualizzaDettagliOrdine(id: number): void {
    const ordine = this.orders.find(o => o.id === id);

    if (ordine) {
      this.dialog.open(DettagliOrdineComponent, {
        data: { ordine },
      });
    } else {
      this.errorMessage = 'Ordine non trovato. Riprova.';
    }
  }



}
