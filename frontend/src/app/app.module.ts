import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AreaPrivataComponent } from './components/area-privata/area-privata.component';
import { MessageComponent } from "./components/finestraMessaggi/message/message.component";

import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { HomeComponent } from './components/home/home.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatDialogActions, MatDialogContent, MatDialogClose, MatDialogTitle} from "@angular/material/dialog";
import { ListaOrdiniComponent } from './components/lista-ordini/lista-ordini.component';
import { DettagliOrdineComponent } from './components/dettagli-ordine/dettagli-ordine.component';
import {MatLine, MatOption} from "@angular/material/core";
import { CartComponent } from './components/cart/cart.component';
import { SetComponent } from './components/set/set.component';
import { CardSellingComponent } from './components/card-selling/card-selling.component';
import {MatCell, MatHeaderCell, MatHeaderRow, MatRow, MatTable} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import {MatSelect} from "@angular/material/select";
import {AuthInterceptor} from "./authInterceptor";
import { CatalogComponent } from './components/catalog/catalog.component';



@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    HomeComponent,
    AreaPrivataComponent,
    MessageComponent,
    ListaOrdiniComponent,
    DettagliOrdineComponent,
    CartComponent,
    SetComponent,
    CardSellingComponent,
    CatalogComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    MatSidenavModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatFormFieldModule,
    MatInputModule,
    BrowserAnimationsModule,
    MatCardTitle,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatDialogContent,
    MatDialogActions,
    MatLine,
    MatDialogClose,
    MatDialogTitle,
    MatTable,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatPaginator,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatOption,
    MatSelect
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }





