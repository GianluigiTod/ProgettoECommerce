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
  cardImageUrl: string = '';
  setImageUrl:string = '';
  set: any = null;
  token: string = '';

  constructor(private http: HttpClient,
              private authService: AuthService,
              private dialog: MatDialog,
              private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');  // Ottengo il parametro id
      this.cardId = id ? +id : null;  // Converte il parametro in un numero
      if (this.cardId) {
        this.token = this.authService.getToken();
        this.getCardDetails(this.cardId);
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cardId'] && this.cardId !== null) {
      this.getCardDetails(this.cardId);
    }
  }

  private getCardDetails(cardId: number): void {
    this.http.get<any>(`${API.backend}/api/card/${cardId}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
    }).subscribe(response => {
      this.card = response;
      this.loadSet(this.card);
      this.http.get(API.backend+`/api/card/${cardId}/image`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
        responseType: 'text'
      }).subscribe((url: string) => {
        this.cardImageUrl=url;
        this.errorMessage='';
      }, (error) => {
        this.handleError(error, "Impossibile caricare l'immagine della carta.");
      });
    }, error => {
      this.handleError(error, "Si è verificato un errore durante il recupero dei dettagli della carta.");
    });
  }

  loadSet(card: any): void{
    const setCode = card.setCode;
    this.http.get<any>(`${API.backend}/api/set/set-code/${setCode}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
    }).subscribe(response => {
      this.set=response;
      this.http.get(API.backend+`/api/set/image/${this.set.id}`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
        responseType: 'text'
      }).subscribe((url: string) => {
        this.setImageUrl=url;
      }, (error) => {
        this.handleError(error, "Impossibile caricare l'immagine del set.");
      });
    }, (error) => {
      this.handleError(error, "Si è verificato un problema durante il recupero del set.");
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
