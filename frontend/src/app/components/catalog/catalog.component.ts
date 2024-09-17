import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { API } from '../../constants';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageComponent } from "../finestraMessaggi/message/message.component";
import { AuthService } from "../../service/auth.service";
import { Router } from '@angular/router';
import {Observable} from "rxjs";

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator) paginator: MatPaginator | undefined;
  displayedColumns: string[] = ['name', 'rarity', 'price', 'image', 'view', 'quantityBlock', 'addToCart'];
  dataSource = new MatTableDataSource<any>([]);
  setCodes: string[] = [];
  setIds: { [key: string]: number } = {};
  selectedSetCode: string = 'Nessun Set';
  cards: any[] = [];
  imageUrls: { [key: number]: string } = {};
  totalCards: number = 0;
  pageSize: number = 10;
  currentPage: number = 0;
  errorMessage: string = '';
  username: string = '';
  token: string = '';
  selectedQuantity: { [key: number]: number } = {};
  currentSortField: string = 'name';
  sortDirection: string = 'asc';

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.token = this.authService.getToken();
    this.getCards();
  }

  ngAfterViewInit() {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
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

  getCards(page: number = 0, size: number = 10, sortBy: string = 'name', direction: string = 'asc'): void {
    const params = {
      page: page.toString(),
      size: size.toString(),
      sortBy,
      direction
    };

    this.selectedSetCode="Nessun Set";
    this.http.get<any>(API.backend + '/api/card/all', {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      params
    }).subscribe(response => {
      this.dataSource = new MatTableDataSource<any>(response.cards);
      this.cards=response.cards;
      this.totalCards = response.numTotaleCarte;
      if (this.paginator) {
        this.paginator.length = this.totalCards;
      }
      this.cards.forEach(card => {
        this.selectedQuantity[card.id] = 1;
      })
      this.loadSetCodes();
      this.loadImageUrls();
      this.errorMessage='';
    }, error => {
      this.handleError(error, "Si è verificato un errore durante il recupero delle carte.");
    });
  }

  loadSetCodes(): void{
    this.http.get<any[]>(API.backend + '/api/set/all', {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      responseType: 'json' as 'json'
    }).subscribe(
      (response) => {
        response.forEach(set => {
          this.setIds[set.setCode] = set.id;
        });
        this.setCodes = Object.keys(this.setIds);
        this.setCodes.unshift('Nessun Set');
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
        this.handleError(error, "Impossibile caricare l'immagine per una carta.");
      });
    });
  }

  toggleSort(field: string): void {
    if (this.currentSortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortField = field;
      this.sortDirection = 'asc';
    }
    this.getCards(this.currentPage, this.pageSize, this.currentSortField, this.sortDirection);
  }

  addToCart(card: any): void {
    if (this.selectedQuantity[card.id] > 0 && this.selectedQuantity[card.id] <= card.quantity) {
      this.getUser(this.username, this.token).subscribe(
        (user) => {
          const userId = user.id;
          const cartDTO = {
            utenteId: userId,
            cardId: card.id,
            quantity: this.selectedQuantity[card.id],
          };

          this.http.post<any>(`${API.backend}/api/cart/add`, cartDTO, {
            headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
          }).subscribe(() => {
              this.dialog.open(MessageComponent, {data: { message: `Aggiunto al carrello: ${card.name}, Quantità: ${this.selectedQuantity[card.id]}` }});
              this.getCards(this.currentPage, this.pageSize, this.currentSortField, this.sortDirection);
            }, (error) => {
              this.handleError(error, "Si è verificato un errore durante l'aggiunta della carta al carrello.");
            }
          );
        }, (error) => {
          this.handleError(error, "Si è verificato un errore durante il recupero dell'utente. Riprova.");
        }
      );
    } else {
      this.dialog.open(MessageComponent, { data: { message: 'Quantità non valida' } });
    }
  }


  getCardsBySetCode(setCode: string, page: number = 0, size: number = 10): void {
    if(setCode === "Nessun Set"){
      this.getCards(this.currentPage, this.pageSize, this.currentSortField, this.sortDirection);
      return
    }
    const setId = this.setIds[setCode];
    const params = {
      page: page.toString(),
      size: size.toString(),
    };

    this.http.get<any>(`${API.backend}/api/card/set/${setId}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      params: params
    }).subscribe(response => {
      this.dataSource = new MatTableDataSource<any>(response.cards);
      if (this.paginator) {
        this.paginator.length = response.numTotaleCarte;
      }
    }, error => {
      this.handleError(error, "Si è verificato un errore durante il recupero delle carte per il set.");
    });
  }

  searchCards(event: KeyboardEvent): void {
    const inputElement = event.target as HTMLInputElement;
    const name = inputElement.value;

    if(!name){
      this.getCards(this.currentPage, this.pageSize, this.currentSortField, this.sortDirection);
      return
    }

    this.http.get<any>(`${API.backend}/api/card/search`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      params: { name }
    }).subscribe(response => {
      this.dataSource = new MatTableDataSource<any>(response.cards);
      if (this.paginator) {
        this.paginator.length = response.numTotaleCarte;
      }
      this.errorMessage='';
    }, error => {
      this.handleError(error, "Si è verificato un errore durante la ricerca delle carte.");
    });
  }

  onPageChange(event: any): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex;
    this.errorMessage='';
    if(this.selectedSetCode !== "Nessun Set"){
      this.getCardsBySetCode(this.selectedSetCode, this.currentPage, this.pageSize);
    }else{
      this.getCards(this.currentPage, this.pageSize, this.currentSortField, this.sortDirection);
    }
  }

  viewCardDetails(cardId: number): void {
    this.errorMessage='';
    this.router.navigate(['/card-details', cardId])
      .then(() => {
        console.log('Navigazione completata con successo');
      })
      .catch(err => {
        console.error('Errore durante la navigazione', err);
      });
  }

  private handleError(error: any, defaultMessage: string): void {
    if (error.error && typeof error.error === 'string') {
      this.errorMessage = error.error;
      this.dialog.open(MessageComponent, { data: { message: error.error } });
    } else {
      this.errorMessage = defaultMessage;
    }
  }
}
