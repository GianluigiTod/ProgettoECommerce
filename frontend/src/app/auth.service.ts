import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authUrl = 'http://localhost:8080'; // URL dell'API per l'autenticazione

  constructor(private http: HttpClient) { }

  login(credentials: { username: string, password: string }): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/login`, credentials);
  }

  register(user: { username: string, password: string }): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/registro`, user);
  }
}
