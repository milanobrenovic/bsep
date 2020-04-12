import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Subject } from 'rxjs';
import { Entity } from 'app/models/entity';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  url = environment.baseUrl + environment.entity;
  createSuccessEmitter = new Subject<Entity>();

  constructor(
    private httpClient: HttpClient,
    private router: Router,
  ) { }

  public add(subject: Entity) {
    return this.httpClient.post(this.url + "/create-subject", subject);
  }

  public getAll(): any {
    return this.httpClient.get(this.url + "/all");
  }

  public getAllWithoutRootEntities(): any {
    return this.httpClient.get(this.url);
  }
  
}
