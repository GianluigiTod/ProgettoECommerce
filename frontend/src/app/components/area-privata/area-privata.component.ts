import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from "../../service/auth.service";
import { MatSnackBar } from '@angular/material/snack-bar';
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
  editableUser: any = {}; // Dati modificabili dall'utente
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

  hasChanges(): boolean {
    return JSON.stringify(this.editableUser) !== JSON.stringify(this.user);
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
      responseType: 'text' as "json" //Specifico che mi aspetto di ricevere un testo
    };

    this.http.delete(url, options).subscribe({
      next: () => {

        this.authService.logOut();

        this.dialog.open(MessageComponent, {
          data: { message: 'Cancellazione avvenuta con successo' }
        });


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

    //Chiudi la finestra di conferma se è aperta
    this.showConfirmDialog = false;
  }

  saveChanges(): void {

    const updatedUser = {
      id: this.user.id, // Mantieni l'ID originale per identificare l'utente
      username: this.editableUser.username || this.user.username, // Usa il valore modificato o quello originale se non è stato modificato
      password: this.editableUser.password || this.user.password, // Come sopra
      nome: this.editableUser.nome || this.user.nome,
      cognome: this.editableUser.cognome || this.user.cognome,
      indirizzo: this.editableUser.indirizzo || this.user.indirizzo,
      email: this.editableUser.email || this.user.email,
      ruolo: this.editableUser.ruolo || this.user.ruolo,
    };

    const url = API.backend+`/api/user/update`;
    const options = {
      headers: new HttpHeaders()
        .set('Authorization', `Bearer ${this.token}`)
        .set('Content-Type', 'application/json')
    };

    this.http.put(url, updatedUser, options).subscribe({
      next: () => {
        this.editing = false; // Disabilita la modalità di modifica dopo il salvataggio
        console.log('Modifiche salvate con successo');
      },
      error: (error) => {
        console.error('Errore durante il salvataggio delle modifiche:', error);
      }
    });

    this.editUser();
  }

}
