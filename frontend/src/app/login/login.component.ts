/*//Versione fatta da Angelo
import { Component, signal } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css'
})
export class SigninComponent {

  constructor(
    private auth : AuthService
  ){

  }
  onSubmit(form:NgForm){
    const username = form.value.username
    const password = form.value.password
    this.auth.signIn(
      {
        username: username,
        password: password
      }
    ).subscribe(data => {
      const expDate = new Date(new Date().getTime() + data.expires_in*1000)
      this.auth.createUser(username,data.access_token,data.refresh_token,expDate)
    })
  }
  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.preventDefault();
  }
}

 */
import { Component, signal } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { DialogService } from '../service/dialog.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(
    private auth : AuthService,
    private router : Router,
    private dialog : DialogService
  ){

  }
  onSubmit(form:NgForm){
    const username = form.value.username
    const password = form.value.password
    this.auth.signIn(
      {
        username: username,
        password: password
      }
    ).subscribe({
      next:data => {
        this.auth.createUser(username,data.access_token,data.refresh_token)
      },
      error:err => {
        this.dialog.showDialog("Errore: username o password errati.")
      }
    })
  }
  hide = signal(true);

  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.preventDefault();
  }
}


