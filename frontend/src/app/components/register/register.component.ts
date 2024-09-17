import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { API } from "../../constants";
import {MatDialog} from "@angular/material/dialog";
import { MessageComponent } from "../finestraMessaggi/message/message.component";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  newUser: any = {
    username: '',
    password: '',
    email: '',
    ruolo: '',
    nome: '',
    cognome: '',
    indirizzo: '',
  };

  errorMessage: string = '';
  passwordVisible: boolean = false;

  constructor(private http: HttpClient, private router: Router, private dialog: MatDialog) {}

  registerUser(): void {
    if (!this.newUser.username || !this.newUser.password || !this.newUser.email || !this.newUser.ruolo) {
      this.dialog.open(MessageComponent, {data: {message: "I campi 'Username', 'Password', 'Email' e 'Ruolo' sono obbligatori."}})
      return;
    }

    const url = API.backend+'/api/user/register'; // Endpoint di registrazione
    const options = {
      headers: new HttpHeaders()
        .set('Content-Type', 'application/json')
    };

    this.http.post(url, this.newUser, options).subscribe({
      next: () => {
        this.dialog.open(MessageComponent, {data: {message: "Utente registrato con successo."}});
        this.errorMessage = '';
        this.router.navigate(['/login']);
      },
      error: (error) => {
        if (error.error && typeof error.error === 'string') {
          this.errorMessage = error.error;
          this.dialog.open(MessageComponent, { data: { message: error.error } });
        } else if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Si è verificato un errore durante la registrazione. Riprova.';
        }
      }
    });
  }

  cancelRegistration(): void {
    this.router.navigate(['/login']);
  }

  togglePassword(): void {
    this.passwordVisible = !this.passwordVisible;
  }
}
