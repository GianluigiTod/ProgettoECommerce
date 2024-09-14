import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {AuthService} from "../../service/auth.service";
import {MatDialog} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {API} from "../../constants";
import {MessageComponent} from "../finestraMessaggi/message/message.component";
import {MatTableDataSource} from "@angular/material/table";




@Component({
  selector: 'app-card-selling',
  templateUrl: './card-selling.component.html',
  styleUrl: './card-selling.component.css'
})


export class CardSellingComponent implements OnInit, AfterViewInit {

  dataSource = new MatTableDataSource<any>([]);
  @ViewChild(MatPaginator) paginator: MatPaginator | undefined;
  displayedColumns: string[] = ['name', 'prezzo', 'rarity', 'image', 'actions'];
  rarities: string[] = ['comune', 'non_comune', 'rara', 'rara_mitica'];
  cards: any[] = [];
  imageUrls: { [key: number]: string } = {};
  setIds: { [key: string]: number } = {};
  setCodes: string[] = [];
  newCard: any = {
    name: '',
    prezzo: 0,
    manaCost: '',
    type: '',
    rarity: '',
    text: '',
    power: 0,
    toughness: 0,
    quantity: 1,
    venditoreId: '',
    setId: 0
  };
  newCardSetCode: string = '';
  selectedCard: any;
  selectedFile: File | null = null;
  totalCards: number = 0;
  totalPages: number = 0;
  pageSize: number = 10;
  currentPage: number = 0;
  ruolo: string = '';
  token: string = '';
  username: string = '';
  errorMessage: string = '';
  userId: number = 0;
  isCreating: boolean = false;
  isEditing: boolean = false;

  constructor(private authService: AuthService,
              private http: HttpClient,
              private router: Router,
              private snackBar: MatSnackBar,
              private dialog: MatDialog) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.token = this.authService.getToken();
    this.getCards();
  }

  ngAfterViewInit() {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }else{
      console.log("ERRORE");
    }
  }

  private getUser(username: string, token: string) : Observable<any> {
    const url = API.backend+'/api/user/get';
    const options = {
      headers: new HttpHeaders().set('Authorization', `Bearer ${token}`),
      params: { username }
    };

    return this.http.get(url, options);
  }

  getCards(page: number = 0, size: number = 10): void {
    this.getUser(this.username, this.token).subscribe(user => {
      this.ruolo=user.ruolo;
      this.userId=user.id;

      const params = {
        page: page.toString(),
        size: size.toString()
      };

      this.http.get<any>(API.backend + `/api/card/seller/${this.userId}`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
        params: params
      }).subscribe((response) => {
        this.cards=response.cards;
        this.dataSource=new MatTableDataSource(this.cards);
        this.totalCards=response.numTotaleCarte;
        if (this.dataSource.paginator) {
          this.dataSource.paginator.length = this.totalCards;
        }
        this.totalPages=response.numPagine;
        this.errorMessage='';
        this.loadImageUrls();
        this.loadSetIds();
      }, (error) => {
        this.handleError(error, "Si è verificato un errore durante il recupero delle carte.");
      })

    }, (error) => {
      this.handleError(error, "Si è verificato un errore durante il recupero dell'utente.");
    })
  }

  loadSetIds(): void {
    this.http.get<any[]>(API.backend + '/api/set/all', {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'json' as 'json'
    }).subscribe(
      (response) => {
        response.forEach(set => {
          this.setIds[set.setCode] = set.id;
        });
        this.setCodes = Object.keys(this.setIds);
      },
      (error) => {
        this.handleError(error, "Errore durante il recupero dei set.");
      }
    );
  }


  loadImageUrls(): void {
    this.cards.forEach(card => {
      this.http.get(API.backend+`/api/card/${card.id}/image`, {
        headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
        responseType: 'text'
      }).subscribe((url: string) => {
        this.imageUrls[card.id] = url;
      }, (error) => {
        this.handleError(error, "Impossibile caricare l'immagine per il set.");
      });
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex;
    this.getCards(this.currentPage, this.pageSize);
  }

  editCard(card: any): void{
    this.isEditing = true;
    this.selectedFile=null;
    this.selectedCard=card;
    this.cancelCreate();
  }

  cancelEdit(): void {
    this.isEditing = false;
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  updateCard(card: any): void {
    console.log(this.selectedCard.rarity);
    if (!card.name || !card.prezzo || !card.rarity || !card.quantity) {
      this.dialog.open(MessageComponent, { data: { message: "I campi 'name', 'prezzo' e 'rarity' sono obbligatori." } });
      return;
    }

    const updatedCard = {
      id: card.id,
      name: card.name,
      prezzo: card.prezzo,
      manaCost: card.manaCost,
      type: card.type,
      rarity: card.rarity,
      text: card.text,
      power: card.power,
      toughness: card.toughness,
      quantity: card.quantity,
      usernameVenditore: card.usernameVenditore,//Da qui in poi penso siano campi necessari altrimenti il metodo nel controller non accetta il json
      setCode: card.setCode,
      imagePath: card.imagePath,
    };

    console.log(updatedCard);
    this.http.put<any>(API.backend + `/api/card/update`, updatedCard, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: "Carta aggiornata con successo." } });
        this.isEditing=false;
        this.errorMessage='';
        this.getCards(this.currentPage);
      },
      (error) => {
        // Gestisci l'errore
        this.handleError(error, "Si è verificato un errore durante l'aggiornamento della carta.");
      }
    );
  }

  uploadImage(file: File, cardId: number): void {
    const formData = new FormData();
    formData.append('file', file);

    this.http.put<any>(API.backend + `/api/card/${cardId}/setImage`, formData, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'json'
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: "Immagine aggiornata con successo." } });
        this.errorMessage='';
        this.getCards(this.currentPage); // Ricarica la lista delle carte
      },
      (error) => {
        this.handleError(error, "Si è verificato un errore durante l'aggiornamento dell'immagine.");
      }
    );
  }


  deleteCard(cardId: number): void {
    this.http.delete<any>(API.backend + `/api/card/delete/${cardId}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'text' as 'json' // Specifica che la risposta è di tipo testo
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: "Carta eliminata con successo." } });
        this.errorMessage='';
        this.getCards(this.currentPage);
      },
      (error) => {
        this.handleError(error, "Errore durante la cancellazione della carta.");
      }
    );
  }

  toggleCreateCard(): void {
    this.isCreating = true;
    this.isEditing = false;
    this.recoveryNewCard();
  }

  cancelCreate(): void {
    this.isCreating = false;
  }

  recoveryNewCard(): void{
    this.newCard = {
      name: '',
      prezzo: 0,
      manaCost: '',
      type: '',
      rarity: '',
      text: '',
      power: 0,
      toughness: 0,
      quantity: 1,
      venditoreId: this.userId,
      setId: 0
    };
  }


  createCard(): void {
    var card = this.newCard;
    console.log(card.rarity, this.newCardSetCode);
    if (!card.name || !card.prezzo || !card.rarity || !card.quantity) {
      this.dialog.open(MessageComponent, { data: { message: "I campi 'name', 'prezzo' e 'rarity' sono obbligatori." } });
      return;
    }

    const setId = this.setIds[this.newCardSetCode];
    if (!setId) {
      this.dialog.open(MessageComponent, { data: { message: `Il set '${this.newCardSetCode}' non esiste.` } });
      return;
    }

    card.setId = setId;

    this.http.post<any>(API.backend + `/api/card/create`, card, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
    }).subscribe(
      (response) => {
        this.dialog.open(MessageComponent, { data: { message: "Carta creata con successo." } });
        this.recoveryNewCard();
        this.errorMessage='';
        this.isCreating=false;
        this.getCards(this.currentPage);
      },
      (error) => {
        this.handleError(error, "Errore durante la creazione della carta.");
      }
    );
  }



  private handleError(error: any, defaultMessage: string): void {
    console.log(error);
    if (error.error && typeof error.error === 'string') {
      this.getCards();
      this.errorMessage = error.error;
      this.dialog.open(MessageComponent, { data: { message: error.error } });
    } else if (error.error && error.error.message) {
      this.errorMessage = error.error.message;
    } else {
      this.errorMessage = defaultMessage;
    }
  }


}
