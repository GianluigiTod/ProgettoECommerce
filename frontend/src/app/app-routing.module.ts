import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AreaPrivataComponent } from './components/area-privata/area-privata.component';
import { authenticationGuard } from './auth/authentication.guard';
import {ListaOrdiniComponent} from "./components/lista-ordini/lista-ordini.component";
import {DettagliOrdineComponent} from "./components/dettagli-ordine/dettagli-ordine.component";

const routes: Routes = [
  { path: '', component: DashboardComponent, canActivate: [authenticationGuard], children: [
      {path: '', redirectTo: 'home', pathMatch: 'full'},
      { path: 'area-privata', component: AreaPrivataComponent },
      { path: 'home', component: HomeComponent },
      { path: 'lista-ordini', component: ListaOrdiniComponent},
      { path: 'dettagli-ordine', component: DettagliOrdineComponent}
    ]},

  {path: 'login', component: LoginComponent},
  { path: 'register', component: RegisterComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
