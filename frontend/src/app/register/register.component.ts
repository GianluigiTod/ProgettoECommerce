import { Component } from '@angular/core';
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService) {}

  register() {
    this.authService.register({username: this.username, password: this.password})
      .subscribe(response => {
        // Gestisci la risposta di successo
      }, error => {
        // Gestisci l'errore
      });
  }
}
