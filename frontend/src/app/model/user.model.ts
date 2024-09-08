export class User{
  constructor(
    public username : string,
    private _token : string,
    private _refreshToken :string,
  ){}
  private userObject : any
  setUserObject(us : any){
    this.userObject = us
  }
  setToken(t:string){
    this._token = t
  }
  get token(){
    return this._token
  }
  get refreshToken(){
    return this._refreshToken
  }
}
