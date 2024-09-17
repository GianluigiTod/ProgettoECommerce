import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from "../../service/auth.service";
import {API} from "../../constants";
import {MessageComponent} from "../finestraMessaggi/message/message.component";
import {MatDialog} from "@angular/material/dialog";


@Component({
  selector: 'app-area-privata',
  templateUrl: './area-privata.component.html',
  styleUrl: './area-privata.component.css'
})
export class AreaPrivataComponent implements OnInit{
  user: any = {};
  username: string
  token: string
  editing: boolean = false;
  showConfirmDialog: boolean = false;

  errorMessage: string = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.username = this.authService.getUsername();
    this.token = this.authService.getToken();
  }

  ngOnInit(): void {
    this.getUser(this.username, this.token).subscribe(user => {
      this.user = user;
      this.username = user.username;
    });
  }

  private getUser(username: string, token: string): Observable<any> {
    const url = API.backend+'/api/user/get';
    const options = {
      headers: new HttpHeaders()
        .set('Authorization', `Bearer ${token}`)
    };

    return this.http.get(url, { ...options, params: { username } });
  }

  editUser(): void {
    this.editing = !this.editing;
  }


  confirmDelete(): void {
    this.showConfirmDialog = true;
  }

  deleteUser(): void {
    const url = API.backend+`/api/user/delete/${this.user.id}`;
    const options = {
      headers: new HttpHeaders()
        .set('Authorization', `Bearer ${this.token}`),
      responseType: 'text' as 'json'
    };
    this.http.delete(url, options).subscribe({
      next: () => {
        this.errorMessage='';
        this.authService.logOut();
        this.dialog.open(MessageComponent, {
          data: { message: 'Cancellazione avvenuta con successo' }
        });


      },
      error: (error) => {
        if (error.error && typeof error.error === 'string') {
          this.errorMessage = error.error;
        } else if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Si è verificato un errore durante la registrazione. Riprova.';
        }
      }
    });

    this.showConfirmDialog = false;
  }

  saveChanges(): void {
    if(!this.user.username || !this.user.password || !this.user.email || !this.user.ruolo){
      this.dialog.open(MessageComponent, { data: {message: "I campi 'Username', 'Password', 'Email' e 'Ruolo' sono obbligatori."}});
      return;
    }

    const updatedUser = {
      id: this.user.id,
      username: this.user.username,
      password: this.user.password,
      nome: this.user.nome,
      cognome: this.user.cognome,
      indirizzo: this.user.indirizzo,
      email: this.user.email,
      ruolo: this.user.ruolo,
    };

    const url = API.backend+`/api/user/update`;
    const options = {
      headers: new HttpHeaders()
        .set('Authorization', `Bearer ${this.token}`)
        .set('Content-Type', 'application/json')
    };

    this.http.put(url, updatedUser, options).subscribe({
      next: () => {
        this.editUser();
        this.errorMessage='';
      }, error: (error) => {
        this.handleError(error, "Si è verificato un problema durante la modifica.")
      }
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
