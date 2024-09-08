import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { API } from "../../constants";

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

  errorMessage: string = ''; // Variabile per memorizzare il messaggio di errore
  passwordVisible: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  registerUser(): void {
    if (!this.newUser.username || !this.newUser.password || !this.newUser.email || !this.newUser.ruolo) {
      this.errorMessage = 'Per favore, compila tutti i campi obbligatori.';
      return;
    }

    const url = API.backend+'/api/user/register'; // Endpoint di registrazione
    const options = {
      headers: new HttpHeaders()
        .set('Content-Type', 'application/json')
    };

    this.http.post(url, this.newUser, options).subscribe({
      next: () => {
        console.log('Utente registrato con successo');
        this.router.navigate(['/login']); // Reindirizza al login
      },
      error: (error) => {
        if (error.error && typeof error.error === 'string') {
          // Se l'errore è una stringa (come il messaggio nel tuo ResponseEntity)
          this.errorMessage = error.error;
        } else if (error.error && error.error.message) {
          // Se c'è un messaggio strutturato nel campo `message`
          this.errorMessage = error.error.message;
        } else {
          // Messaggio generico in caso di errore
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
