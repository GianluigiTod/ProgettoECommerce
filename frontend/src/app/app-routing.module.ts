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
import { CartComponent } from "./components/cart/cart.component";
import {SetComponent} from "./components/set/set.component";
import { CardSellingComponent } from "./components/card-selling/card-selling.component";
import {CatalogComponent} from "./components/catalog/catalog.component";

const routes: Routes = [
  { path: '', component: DashboardComponent, canActivate: [authenticationGuard], children: [
      {path: '', redirectTo: 'home', pathMatch: 'full'},
      { path: 'area-privata', component: AreaPrivataComponent },
      { path: 'home', component: HomeComponent },
      { path: 'lista-ordini', component: ListaOrdiniComponent},
      { path: 'dettagli-ordine', component: DettagliOrdineComponent},
      { path: 'cart', component: CartComponent },
      { path: 'set', component: SetComponent },
      { path: 'card-selling', component: CardSellingComponent },
      { path: 'catalog', component: CatalogComponent },
    ]},

  {path: 'login', component: LoginComponent},
  { path: 'register', component: RegisterComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
