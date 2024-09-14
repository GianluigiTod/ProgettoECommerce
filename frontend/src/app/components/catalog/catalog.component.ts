import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { API } from '../../constants';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageComponent } from "../finestraMessaggi/message/message.component";
import { AuthService } from "../../service/auth.service";

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css']
})
export class CatalogComponent implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator) paginator: MatPaginator | undefined;
  displayedColumns: string[] = ['name', 'rarity', 'price', 'quantity', 'image'];
  dataSource = new MatTableDataSource<any>([]);
  setCodes: string[] = [];
  cards: any[] = [];
  imageUrls: { [key: number]: string } = {};
  totalCards: number = 0;
  pageSize: number = 10;
  currentPage: number = 0;
  errorMessage: string = '';
  username: string = '';
  token: string = '';
  currentSortField: string = 'name';
  sortDirection: string = 'asc';

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private authService: AuthService
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

  getCards(page: number = 0, size: number = 10, sortBy: string = 'name', direction: string = 'asc'): void {
    const params = {
      page: page.toString(),
      size: size.toString(),
      sortBy,
      direction
    };

    this.http.get<any>(API.backend + '/api/card/all', {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      params
    }).subscribe(response => {
      this.dataSource.data = response.cards;
      this.cards=response.cards;
      this.totalCards = response.numTotaleCarte;
      if (this.paginator) {
        this.paginator.length = this.totalCards;
      }
      this.loadSetCodes();
      this.loadImageUrls();
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
          this.setCodes.push(set.setCode);
        });
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

  toggleSort(field: string): void {
    if (this.currentSortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortField = field;
      this.sortDirection = 'asc';
    }
    this.getCards(this.currentPage, this.pageSize, this.currentSortField, this.sortDirection);
  }

  getCardById(id: number): void {
    this.http.get<any>(`${API.backend}/api/card/${id}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
    }).subscribe(response => {
      this.dataSource.data = [response]; // Example of setting data for a single card
    }, error => {
      this.handleError(error, "Si è verificato un errore durante il recupero della carta.");
    });
  }

  getCardsBySetCode(event: KeyboardEvent): void {
    const inputElement = event.target as HTMLInputElement;
    const setCode = inputElement.value;

    if (!setCode) {
      this.getCards(); // Get all cards if search is cleared
      return;
    }

    this.http.get<any>(`${API.backend}/api/card/set/${setCode}`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`)
    }).subscribe(response => {
      this.dataSource.data = response.cards;
    }, error => {
      this.handleError(error, "Si è verificato un errore durante il recupero delle carte per il set.");
    });
  }

  searchCards(event: KeyboardEvent): void {
    const inputElement = event.target as HTMLInputElement;
    const name = inputElement.value;

    if(!name){
      this.getCards();
      return
    }

    this.http.get<any>(`${API.backend}/api/card/search`, {
      headers: new HttpHeaders().set('Authorization', `Bearer ${this.token}`),
      params: { name }
    }).subscribe(response => {
      this.dataSource.data = response.cards;
    }, error => {
      this.handleError(error, "Si è verificato un errore durante la ricerca delle carte.");
    });
  }

  onPageChange(event: any): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex;
    this.getCards(this.currentPage, this.pageSize);
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
