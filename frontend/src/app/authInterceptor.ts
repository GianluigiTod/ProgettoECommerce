import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from './service/auth.service';
import { DialogService } from './service/dialog.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService:AuthService, private router: Router, private dialog : DialogService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          return this.handle401Error(request, next);//Gestiamo questo errore ricreando il token
        }
        if(error.status === 500){
          this.dialog.showDialog(error.error.message)
        }
        return throwError(error);//Nel caso sia un qualsiasi altro errore lancialo e basta
      })
    );
  }

  handle401Error(request: HttpRequest<any>, next: HttpHandler){
    return this.authService.refresh().pipe(//utilizzo il metodo refresh per effettuare una richiesta http per ottenere il nuovo token
      switchMap((token: any) => {
        this.authService.setToken(token.access_token);
        const authRequest = request.clone({setHeaders: {Authorization: `Bearer ${token.access_token}`}});
        return next.handle(authRequest);
      }),
      catchError((error) => {
        this.handleAuthError()
        console.log(this.authService.User())//Mostro l'utente nella console del browser
        this.dialog.showDialog('Errore: non sei autenticato.')
        return throwError(error)
      })
    );
  }

  private handleAuthError() {
    this.authService.logOut()
  }
}


