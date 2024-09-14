import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from "../../service/auth.service";
import { MatDialog } from "@angular/material/dialog";
import { MessageComponent } from "../finestraMessaggi/message/message.component";
import { API } from "../../constants";
import {Observable} from "rxjs";


@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent implements OnInit {
  cartItems: any[] = [];
  errorMessage: string = '';
  token: string = '';
  username: string = '';
  total: number = 0;
  cartItemsId: any[] = []
  showConfirmation: boolean = false;
  ordine: any;

  constructor(private http: HttpClient, private authService: AuthService, private dialog: MatDialog, private router: Router) {}

  ngOnInit(): void {
    this.token = this.authService.getToken();
    this.username = this.authService.getUsername();
    this.getCartItems();
  }

  private getUser(username: string, token: string) : Observable<any> {
    const url = API.backend+'/api/user/get';
    const options = {
      headers: new HttpHeaders().set('Authorization', `Bearer ${token}`),
      params: { username }
    };

    return this.http.get(url, options);
  }

  getCartItems(): void {
    this.total=0;
    this.getUser(this.username, this.token).subscribe(user => {
      const userId = user.id;


      this.http.get<any>(`${API.backend}/api/cart/${userId}`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
        responseType: 'json' as 'json'
      }).subscribe((response) => {
          this.cartItems = response.items;
          console.log(this.cartItems);
          for (let i = 0; i < this.cartItems.length; i++) {
            this.total += this.cartItems[i].prezzo * this.cartItems[i].quantity;
          }

          if (response.hasChanged) {
            this.dialog.open(MessageComponent, {
              data: { message: response.message }
            });
            this.showConfirmation = false;
          }
        },
        (error) => {
          this.handleError(error, 'Si è verificato un errore durante il recupero degli articoli del carrello. Riprova.');
        });

    },(error) =>{
      this.handleError(error, "Si è verificato un errore durante il recupero dell'utente. Riprova.");
    });
  }

  updateCartItem(itemId: number, quantity: number): void {
    this.http.put(API.backend+`/api/cart/update/${itemId}`, {},{
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      params: { quantity: quantity.toString() }
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: 'Quantità aggiornata con successo!' } });
        this.getCartItems();
      },
      (error) => {
        this.handleError(error, 'Errore durante l\'aggiornamento della quantità dell\'articolo.');
      }
    );
  }

  deleteCartItem(itemId: number): void {
    this.http.delete(API.backend+`/api/cart/delete/${itemId}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'text' as "json"
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: 'Articolo rimosso con successo dal carrello!' } });
        this.getCartItems();
      },
      (error) => {
        this.handleError(error, 'Errore durante la rimozione dell\'articolo dal carrello.');
      }
    );
  }

  private handleError(error: any, defaultMessage: string): void {
    console.log(error);
    if (error.error && typeof error.error === 'string') {
      this.getCartItems();
      this.errorMessage = error.error;
      this.dialog.open(MessageComponent, { data: { message: error.error } });
    } else if (error.error && error.error.message) {
      this.errorMessage = error.error.message;
    } else {
      this.errorMessage = defaultMessage;
    }
  }

  showConfirmationDialog(): void {
    this.showConfirmation = true;
    this.getCartItems(); // Verifica se ci sono state modifiche
  }

  cancelCheckout(): void {
    this.showConfirmation = false;
  }

  proceedToCheckout(): void {
    this.getCartItems()
    if(this.showConfirmation){
      for (let i = 0; i<this.cartItems.length; i++) {
        this.cartItemsId[i] = this.cartItems[i].id;
        console.log(this.cartItemsId[i]);
      }
      this.http.post(API.backend+'/api/order/checkout', this.cartItemsId, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
      }).subscribe((response) => {
        console.log("Acquisto effettuato!");
        this.router.navigate(['lista-ordini']);
        this.showConfirmation = false;
      }, error => {
        this.handleError(error, "Errore durante il checkout");
      })
    }
  }



}
