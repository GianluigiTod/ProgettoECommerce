import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { API } from '../../constants';
import { AuthService } from '../../service/auth.service';
import {MessageComponent} from "../finestraMessaggi/message/message.component";
import {MatDialog} from "@angular/material/dialog";
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-card-details',
  templateUrl: './card-details.component.html',
  styleUrls: ['./card-details.component.css']
})
export class CardDetailsComponent implements OnInit, OnChanges {
  cardId: number | null = null;
  card: any = null;
  errorMessage: string = '';
  imageUrl: string = '';

  constructor(private http: HttpClient,
              private authService: AuthService,
              private dialog: MatDialog,
              private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');  // Verifica che il parametro 'id' sia corretto
      this.cardId = id ? +id : null;  // Converte il parametro in un numero
      console.log(this.cardId);
      if (this.cardId) {
        this.getCardDetails(this.cardId);  // Chiama la funzione solo se l'id è valido
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cardId'] && this.cardId !== null) {
      this.getCardDetails(this.cardId);
    }
  }

  private getCardDetails(cardId: number): void {
    const token = this.authService.getToken();
    this.http.get<any>(`${API.backend}/api/card/${cardId}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${token}`)
    }).subscribe(response => {
      this.card = response;
      console.log(this.card);
      this.http.get(API.backend+`/api/card/${cardId}/image`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${token}`),
        responseType: 'text'
      }).subscribe((url: string) => {
        this.imageUrl=url;
        console.log(this.imageUrl);
      }, (error) => {
        this.handleError(error, "Impossibile caricare l'immagine della carta.");
      });
    }, error => {
      this.handleError(error, "Si è verificato un errore durante il recupero dei dettagli della carta.");
    });
  }

  private handleError(error: any, defaultMessage: string): void {
    if (error.error && typeof error.error === 'string') {
      this.errorMessage = error.error;
      this.dialog.open(MessageComponent, { data: { message: error.error } });
    } else {
      this.errorMessage = defaultMessage;
    }
  }
}
