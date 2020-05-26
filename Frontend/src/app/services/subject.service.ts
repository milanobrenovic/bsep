import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Subject } from 'rxjs';
import { Entity } from 'app/models/entity';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  url = environment.baseUrl + environment.subject;
  createSuccessEmitter = new Subject<Entity>();

  constructor(
    private httpClient: HttpClient,
    private router: Router,
  ) { }

  public createSubject(subject: Entity) {
    return this.httpClient.post(this.url + "/create-subject", subject);
  }

  public getSubjectBy(id: number) {
    let params = new HttpParams();
    params = params.append('id', id.toString());

    return this.httpClient.get(this.url + "/" + id, {
      params: params
    });
  }
  
  public getAllSubjects(): any {
    return this.httpClient.get(this.url + "/all");
  }
  
}
