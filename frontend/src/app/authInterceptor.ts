import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from './service/auth.service';
import {MatDialog} from "@angular/material/dialog";
import { MessageComponent } from "./components/finestraMessaggi/message/message.component";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService:AuthService, private router: Router, private dialog : MatDialog) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          return this.handle401Error(request, next);
        }
        return throwError(error);
      })
    );
  }

  handle401Error(request: HttpRequest<any>, next: HttpHandler){
    return this.authService.refresh().pipe(
      switchMap((token: any) => {
        this.authService.setToken(token.access_token);
        const authRequest = request.clone({setHeaders: {Authorization: `Bearer ${token.access_token}`}});
        return next.handle(authRequest);
      }),
      catchError((error) => {
        if(error.status === 401 || error.status === 403) {
          this.handleAuthError()
          this.dialog.open(MessageComponent, {data: { message: 'Errore: non sei autenticato.' }});
        }
        return throwError(error);
      })
    );
  }

  private handleAuthError() {
    this.authService.logOut()
  }
}


