import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {API} from "../../constants";
import {MessageComponent} from "../finestraMessaggi/message/message.component";
import {MatSnackBar} from "@angular/material/snack-bar";


@Component({
  selector: 'app-set',
  templateUrl: './set.component.html',
  styleUrl: './set.component.css'
})
export class SetComponent implements OnInit{
  setList: any[] = [];
  imageUrls: { [key: number]: string } = {};
  errorMessage: string = '';
  token: string = '';
  username: string = '';
  ruolo: string = '';
  isCreating: boolean = false;
  isEditing: boolean = false;
  selectedSet: any = null;
  updatedSetCode: string = '';
  updatedSetName: string = '';
  newSetCode: string = '';
  newSetName: string = '';
  selectedFile: File | null = null;

  constructor(private authService: AuthService, private http: HttpClient, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.token = this.authService.getToken();
    this.username = this.authService.getUsername();
    this.getSetList();
  }

  private getUser(username: string, token: string) : Observable<any> {
    const url = API.backend+'/api/user/get';
    const options = {
      headers: new HttpHeaders().set('Authorization', `Bearer ${token}`),
      params: { username }
    };

    return this.http.get(url, options);
  }

  getSetList() : void {
    this.getUser(this.username, this.token).subscribe(user => {
      this.ruolo=user.ruolo;
    }, (error) => {
      this.handleError(error, "Si è verificato un errore durante il recupero dell'utente.");
    })

    this.http.get<any[]>(API.backend + '/api/set/all', {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'json' as 'json'
    }).subscribe((response) => {
      this.setList=response;
      this.loadImageUrls();
      this.errorMessage='';
    }, (error) => {
      this.handleError(error, "Si è verificato un errore durante il recupero dei set.");
    })
  }

  loadImageUrls(): void {
    this.setList.forEach(set => {
      this.http.get(API.backend+`/api/set/image/${set.id}`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
        responseType: 'text'
      }).subscribe((url: string) => {
        this.imageUrls[set.id] = url;
      }, (error) => {
        this.handleError(error, "Impossibile caricare l'immagine per il set.");
      });
    });
  }

  deleteSet(id: number): void {
    this.http.delete(API.backend +`/api/set/delete/${id}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'text'
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: response}});
        this.getSetList();
      },
      (error) => {
        this.handleError(error, "Errore durante la cancellazione.");
      }
    );
  }

  editSet(set: any): void {
    this.isEditing = true;
    this.selectedSet = set;
    this.updatedSetCode = set.setCode;
    this.updatedSetName = set.setName;
    this.cancelCreate();
    setTimeout(() => {
      const element = document.getElementById('edit-set-container');
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
      }
    }, 100);
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.selectedSet = null;
    this.updatedSetCode = '';
    this.updatedSetName = '';
  }

  updateSet(): void {
    if(!this.updatedSetCode || !this.updatedSetName) {
      this.dialog.open(MessageComponent, {data: { message: "I campi 'Codice Set' e 'Nome Set' sono obbligatori" }});
      return;
    }
    const updatedSet = {
      id: this.selectedSet.id,
      setCode: this.updatedSetCode,
      setName: this.updatedSetName
    };

    this.http.put<any>(`${API.backend}/api/set/update`, updatedSet, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
    }).subscribe(
      () => {

        if (this.selectedFile) {
          const formData = new FormData();
          formData.append('file', this.selectedFile);

          this.http.put(API.backend+`/api/set/update/${this.selectedSet.id}/image`, formData, {
            headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
            responseType: 'text'
          }).subscribe(() => {},
            (error) => {
              this.handleError(error, "Si è verificato un problema durante l'aggiornamento dell'immagine.");
            }
          );
        }

        this.dialog.open(MessageComponent, { data: {message: "Set aggiornato con successo."}});
        this.getSetList();
        this.cancelEdit();
        this.selectedFile=null;
      },
      (error) => {
        this.handleError(error, "Si è verificato un problema durante l'aggiornamento.");
      }
    );


  }

  toggleCreateSet(): void {
    this.isCreating = !this.isCreating;
    this.cancelEdit();
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] ? event.target.files[0] : null;
  }

  cancelCreate(): void {
    this.isCreating = false;
    this.newSetCode = '';
    this.newSetName = '';
    this.selectedFile = null;
  }

  createSet(): void {
    let url;
    if (!this.newSetCode || !this.newSetName) {
      this.dialog.open(MessageComponent, { data: { message: "Tutti i campi devono essere compilati." } });
      return;
    }

    const formData = new FormData();
    formData.append('setCode', this.newSetCode);
    formData.append('setName', this.newSetName);
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
      url = API.backend + '/api/set/create-image';
    }else{
      url = API.backend + '/api/set/create';
    }

    this.http.post(url, formData, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: "Set creato con successo." } });
        this.getSetList();
        this.cancelCreate();
        this.selectedFile = null;
      },
      (error) => {
        this.handleError(error, "Si è verificato un problema durante la creazione del set.");
      }
    );
  }

  private handleError(error: any, defaultMessage: string): void {
    if (error.error && typeof error.error === 'string') {
      this.getSetList();
      this.errorMessage = error.error;
      this.dialog.open(MessageComponent, { data: { message: error.error } });
    } else if (error.error && error.error.message) {
      this.errorMessage = error.error.message;
    } else {
      this.errorMessage = defaultMessage;
    }
  }


}
