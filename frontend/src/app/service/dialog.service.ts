import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MessageComponent } from '../components/finestraMessaggi/message/message.component';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog : MatDialog) { }
  showDialog(mex:string){
    this.dialog.open(MessageComponent, {
      width: '250px',
      data: { message: mex }
    })
  }
}
