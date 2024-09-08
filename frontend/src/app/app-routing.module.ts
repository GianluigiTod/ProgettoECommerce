import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AreaPrivataComponent } from './components/area-privata/area-privata.component';
import { authenticationGuard } from './auth/authentication.guard';

const routes: Routes = [
  { path: '', component: DashboardComponent, canActivate: [authenticationGuard], children: [
      {path: '', redirectTo: 'home', pathMatch: 'full'},
      { path: 'area-privata', component: AreaPrivataComponent },
      { path: 'home', component: HomeComponent }
    ]},

  {path: 'login', component: LoginComponent},
  { path: 'register', component: RegisterComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
