import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import {MagazineState} from '../../magazine.state';
import {MagazinesService} from '../../magazines.service';

@Component({
  selector: 'app-facets',
  templateUrl: './facets.component.html',
  styleUrls: ['./facets.component.scss']
})
export class FacetsComponent implements OnInit {
  
  subscriptions: Subscription[] = [];
  active : boolean = false;
  
  constructor(public state: MagazineState, private service: MagazinesService) {
    
  }

  ngOnInit() {
    this.openCollapsible();
    //this.active = this.state.facets.length > 0;
    this.subscriptions.push(this.state.stateChangedSubject.subscribe((st) => {
      setTimeout(() => {
        this.active = true;
      }, 1000);
      
    }));
    
  }
  
  openCollapsible(){
    this.active = false;
    setTimeout(() => {
        this.active = this.state.facets.length > 0;
      }, 100);
  }

  ngOnDestroy() {
    this.active = false;
    this.subscriptions.forEach((s: Subscription) => {
      s.unsubscribe();
    });
    this.subscriptions = [];
  }
  
  addFilter(field: string, value: string){
    if (!this.state.isFacetUsed(field, value)){
      this.state.addFilter(field, value);
      this.service.getMagazines().subscribe();
    }
  }

}
