import { Component, signal } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MessageComponent } from "../components/finestraMessaggi/message/message.component";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(
    private auth : AuthService,
    private router : Router,
    private dialog : MatDialog
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
        this.dialog.open(MessageComponent, {data: { message: "Errore: username o password errati." }});
      }
    })
  }
  hide = signal(true);

  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.preventDefault();
  }
}


