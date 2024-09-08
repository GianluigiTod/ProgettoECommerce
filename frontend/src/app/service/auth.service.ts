import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { API } from '../constants';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  isLoggedIn = false
  constructor(private http : HttpClient, private router : Router) { }
  user!: User

  isAuthenticated(){ return this.isLoggedIn }


  signIn(body?:any):Observable <any>{
    const url=API.tokenUrl

    const headers = new HttpHeaders().set('Content-Type','application/x-www-form-urlencoded')

    let username=body.username
    let password=body.password

    let httpParams=new URLSearchParams()
    httpParams.set('client_id',API.client_id)
    httpParams.set('client_secret',API.client_secret)
    httpParams.set('grant_type','password')
    httpParams.set('username',username)
    httpParams.set('password',password);
    let req =this.http.post(url,httpParams,{headers})

    return req;
  }

  createUser(username: string, token: string, refresh_token: string){
    this.user = new User(username,token,refresh_token)
    this.getUser(username,token).subscribe(
      utente => {
        console.log(utente)
        this.user.setUserObject(utente)
      }
    )
    this.isLoggedIn = true
    localStorage.setItem('user', JSON.stringify(this.user))
    this.router.navigate([''])
  }


  logOut(){
    this.isLoggedIn = false
    this.user = new User("","","")
    localStorage.removeItem('user')
    this.router.navigate(['login'])
  }


  register(body:{}){
    let url = API.backend+'/api/user/register'
    return this.http.post(url,body)
  }

  private getUser(username : string, token:string):Observable<any>{
    let url = API.backend + '/api/user/get';
    let httpParams = new HttpParams().set('username', username); // Assegna il risultato di set

    let access_token = token;
    let options: any = {
      headers: new HttpHeaders(),
      params: httpParams
    };

    if (access_token) {
      options.headers = options.headers
        .set('Authorization', 'Bearer ' + access_token)
        .set('Content-Type', 'application/json');
    }

    return this.http.get(url, options);
  }

  refresh(): Observable<any> {//dal refresh token riesco ad ottenere un nuovo token
    const refreshToken = this.user.refreshToken
    if (refreshToken) {
      const body = new URLSearchParams();
      body.set('grant_type', 'refresh_token');
      body.set('client_id', API.client_id);
      body.set('client_secret', API.client_secret);
      body.set('refresh_token', refreshToken);

      const headers = new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
      });
      return this.http.post<any>(API.tokenUrl, body.toString(), { headers });
    } else {
      throw new Error('No refresh token available');
    }
  }
  getUsername():string{
    return this.user.username
  }
  getToken():string{
    return this.user.token
  }
  getRefreshToken(){
    return this.user.refreshToken
  }

  setToken(t : string){
    this.user.setToken(t)
    localStorage.removeItem('user')
    localStorage.setItem('user',JSON.stringify(this.user))
  }

  User():User{
    const obj = localStorage.getItem('user')
    if(obj){
      let user = JSON.parse(obj) as User
      return user
    }
    console.log('No user available.')
    return this.user
  }
}
