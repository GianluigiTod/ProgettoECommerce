import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {API} from "../../constants";
import {MessageComponent} from "../finestraMessaggi/message/message.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  username: string = '';
  cartItemsCount: number = 0;
  cards: any[] = [];
  orders: any[] = [];
  errorMessage: string = '';

  constructor(private authService: AuthService,
              private http: HttpClient,
              private dialog: MatDialog) {}

  private getUser(username: string, token: string): Observable<any> {
    const url = API.backend + '/api/user/get';
    const options = {
      headers: new HttpHeaders()
        .set('Authorization', `Bearer ${token}`)
    };

    return this.http.get(url, {...options, params: {username}});
  }

  ngOnInit(): void {
    const token = this.authService.getToken();
    const username = this.authService.getUsername();


    this.getUser(username, token).subscribe(user => {
      this.username = user.username;
      this.cartItemsCount = user.cartItems.length;
      this.cards = user.cards.slice(0,3);
      this.orders = user.listaOrdini.slice(0, 3);
      this.errorMessage='';
    }, (error)=>{
      this.handleError(error, "Errore durante la ricerca dell'utente.");
    });
  }

  private handleError(error: any, defaultMessage: string): void {
    if (error.error && typeof error.error === 'string') {
      this.errorMessage = error.error;
      this.dialog.open(MessageComponent, { data: { message: error.error } });
    } else if (error.error && error.error.message) {
      this.errorMessage = error.error.message;
    } else {
      this.errorMessage = defaultMessage;
    }
  }
}
