import { Injectable } from '@angular/core';
import { environment } from 'environments/environment';
import { Subject } from 'rxjs';
import { Entity } from 'app/models/entity';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

  public url = environment.baseUrl + environment.subject;

  constructor(
    private httpClient: HttpClient,
  ) { }

  public createSubject(subject: Entity) {
    console.log(subject);
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

  public getAllSubjectsWithoutCertificate(): any {
    return this.httpClient.get(this.url + "/subjects-without-certificate");
  }
  
}
