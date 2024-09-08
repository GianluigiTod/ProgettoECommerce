import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable} from "rxjs";
import {API} from "../../constants";

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

  constructor(private authService: AuthService, private http: HttpClient, private router: Router) {
  }

  private getUser(username: string, token: string): Observable<any> {
    const url = API.backend + '/api/user/get';
    const options = {
      headers: new HttpHeaders()
        .set('Authorization', `Bearer ${token}`)
    };

    return this.http.get(url, {...options, params: {username}});
  }

  ngOnInit(): void {
    const token = this.authService.getToken(); // Recupera il token in base al tuo flusso di autenticazione
    const username = this.authService.getUsername(); // Recupera l'username


    this.getUser(username, token).subscribe(user => {
      this.username = user.username;
      this.cartItemsCount = user.cartItems.length;
      this.cards = user.cards;
      this.orders = user.listaOrdini.slice(0, 3);
    });
  }
}
