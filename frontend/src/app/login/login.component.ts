import { Component } from '@angular/core';
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService) {}

  login() {
    this.authService.login({username: this.username, password: this.password})
      .subscribe(response => {
        // Gestisci la risposta di successo
      }, error => {
        // Gestisci l'errore
      });
  }
}
