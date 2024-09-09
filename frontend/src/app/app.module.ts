import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
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
import {MatLine} from "@angular/material/core";


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
    DettagliOrdineComponent
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
    MatDialogTitle
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }





